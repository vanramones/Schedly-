package com.example.schedly

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schedly.data.local.CategoryRepository
import com.example.schedly.data.local.ReminderRepository
import com.example.schedly.data.local.ScheduleCategoryCrossRef
import com.example.schedly.data.local.ScheduleCategoryRepository
import com.example.schedly.data.local.ScheduleRepository
import com.example.schedly.data.local.SchedlyDatabase
import com.example.schedly.data.local.UserRepository
import com.example.schedly.ui.theme.SchedlyTheme
import com.example.schedly.User
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

class MainActivity : ComponentActivity() {

    private lateinit var database: SchedlyDatabase
    lateinit var scheduleRepository: ScheduleRepository
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var reminderRepository: ReminderRepository
        private set
    lateinit var categoryRepository: CategoryRepository
        private set
    lateinit var scheduleCategoryRepository: ScheduleCategoryRepository
        private set
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = SchedlyDatabase.getInstance(applicationContext)
        scheduleRepository = ScheduleRepository(database.scheduleDao())
        userRepository = UserRepository(database.userDao())
        reminderRepository = ReminderRepository(database.reminderDao())
        categoryRepository = CategoryRepository(database.categoryDao())
        scheduleCategoryRepository = ScheduleCategoryRepository(database.scheduleCategoryDao())
        lifecycleScope.launch {
            val seededSchedules = scheduleRepository.seedDefaults(initialSchedules())
            categoryRepository.seedDefaults(listOf("Work", "Personal", "School"))
            if (!reminderRepository.hasAny()) {
                val schedulesForReminderSeed = if (seededSchedules.isNotEmpty()) {
                    seededSchedules
                } else {
                    scheduleRepository.getAll().map { schedule -> schedule to schedule.id }
                }
                schedulesForReminderSeed.forEach { (schedule, assignedId) ->
                    val reminderTime = schedule.reminderDateTime ?: LocalDateTime.of(schedule.date, LocalTime.of(8, 0))
                    reminderRepository.insert(assignedId, reminderTime, "Reminder for ${schedule.title}")
                }
            }
            if (!scheduleCategoryRepository.hasAny()) {
                val categoriesByName = categoryRepository.getAll().associateBy { it.name }
                val fallbackCategoryId = categoriesByName["Personal"]?.id
                val assignments = mapOf(
                    "Class 1" to listOf("School"),
                    "Class 2" to listOf("School"),
                    "Team Sync" to listOf("Work")
                )
                val schedulesForCategories = if (seededSchedules.isNotEmpty()) {
                    seededSchedules
                } else {
                    scheduleRepository.getAll().map { schedule -> schedule to schedule.id }
                }
                val crossRefs = schedulesForCategories.flatMap { (schedule, id) ->
                    val categoryNames = assignments[schedule.title] ?: listOf("Personal")
                    categoryNames.mapNotNull { name ->
                        val categoryId = categoriesByName[name]?.id ?: fallbackCategoryId
                        categoryId?.let { ScheduleCategoryCrossRef(scheduleId = id, categoryId = it) }
                    }
                }
                if (crossRefs.isNotEmpty()) {
                    scheduleCategoryRepository.insertAll(crossRefs)
                }
            }
        }
        requestPostNotificationPermission()
        setContent {
            SchedlyTheme {
                SchedlyApp(scheduleRepository, userRepository, this)
            }
        }
    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun SchedlyApp(scheduleRepository: ScheduleRepository, userRepository: UserRepository, activity: MainActivity) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val currentUserState = remember { mutableStateOf<User?>(null) }
    val ownerUsername = currentUserState.value?.username
    val schedulesFlow = remember(ownerUsername) {
        ownerUsername?.let { scheduleRepository.observeSchedules(it) } ?: flowOf(emptyList())
    }
    val schedules by schedulesFlow.collectAsState(initial = emptyList())
    val profileErrorState = remember { mutableStateOf<String?>(null) }
    val loginErrorState = remember { mutableStateOf<String?>(null) }
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            StartScreen(onGetStartedClick = { navController.navigate("login") })
        }
        composable("login") {
            LoginScreen(
                onCreateAccountClick = { navController.navigate("signup") },
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { navController.navigate("reset_password") },
                onLoginClick = { username, password ->
                    coroutineScope.launch {
                        val user = userRepository.authenticate(username, password)
                        if (user != null) {
                            loginErrorState.value = null
                            currentUserState.value = user
                            navController.navigate("home")
                        }
                        else {
                            loginErrorState.value = "Invalid username or password"
                        }
                    }
                },
                errorMessage = loginErrorState.value,
                onErrorDismiss = { loginErrorState.value = null }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignInClick = { navController.navigate("login") },
                onBackClick = { navController.popBackStack() },
                onSignUpClick = { email, username, password ->
                    coroutineScope.launch {
                        val registered = userRepository.register(User(email, username, password))
                        if (registered) {
                            loginErrorState.value = null
                            navController.navigate("login")
                        }
                    }
                }
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onContinueClick = { navController.navigate("verification_code") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("verification_code") {
            VerificationCodeScreen(
                onContinueClick = { navController.navigate("new_password") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("new_password") {
            NewPasswordScreen(
                onContinueClick = { navController.navigate("password_reset_success") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("password_reset_success") {
            PasswordResetSuccessScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("home") {
            HomeScreen(
                schedules = schedules,
                userName = currentUserState.value?.username ?: "Schedly User",
                onAddSchedClick = { navController.navigate("new_sched") },
                onProfileClick = { navController.navigate("profile") },
                onToggleScheduleCompletion = { scheduleId, isCompleted ->
                    coroutineScope.launch {
                        val schedule = schedules.find { it.id == scheduleId } ?: return@launch
                        val updated = schedule.copy(isCompleted = isCompleted)
                        scheduleRepository.update(updated)
                    }
                },
                onUpdateSchedule = { updatedSchedule ->
                    coroutineScope.launch {
                        scheduleRepository.update(updatedSchedule)
                        ReminderScheduler.cancelReminder(activity, updatedSchedule.id)
                        ReminderScheduler.scheduleReminder(activity, updatedSchedule)
                    }
                },
                onDeleteSchedule = { scheduleId ->
                    coroutineScope.launch {
                        scheduleRepository.delete(scheduleId)
                        ReminderScheduler.cancelReminder(activity, scheduleId)
                    }
                }
            )
        }
        composable("new_sched") {
            val user = currentUserState.value
            if (user == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("new_sched") { inclusive = true }
                    }
                }
            } else {
                NewSchedScreen(
                    onBackClick = { navController.popBackStack() },
                    ownerUsername = user.username,
                    onSaveSchedule = { scheduleDraft ->
                        coroutineScope.launch {
                            val assignedId = if (scheduleDraft.id == 0) {
                                scheduleRepository.insert(scheduleDraft)
                            } else {
                                scheduleRepository.update(scheduleDraft)
                                scheduleDraft.id
                            }
                            val savedSchedule = scheduleDraft.copy(id = assignedId)
                            ReminderScheduler.cancelReminder(activity, savedSchedule.id)
                            ReminderScheduler.scheduleReminder(activity, savedSchedule)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
        composable("profile") {
            val user = currentUserState.value
            if (user == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            } else {
                ProfileScreen(
                    initialEmail = user.email,
                    initialUsername = user.username,
                    initialPassword = user.password,
                    initialImageUri = null,
                    errorMessage = profileErrorState.value,
                    onErrorDismiss = { profileErrorState.value = null },
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        currentUserState.value = null
                        profileErrorState.value = null
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onSaveClick = { email, username, password, _ ->
                        coroutineScope.launch {
                            val originalUsername = user.username
                            val updatedUser = User(email, username, password)
                            val success = userRepository.update(originalUsername, updatedUser)
                            if (success) {
                                currentUserState.value = updatedUser
                                profileErrorState.value = null
                                navController.popBackStack()
                            } else {
                                profileErrorState.value = "Username already taken"
                            }
                        }
                    }
                )
            }
        }
    }
}
