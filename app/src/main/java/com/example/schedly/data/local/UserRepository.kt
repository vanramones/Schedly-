package com.example.schedly.data.local

import com.example.schedly.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    fun observeUsers(): Flow<List<User>> =
        userDao.observeUsers().map { entities -> entities.map { it.toDomain() } }

    suspend fun register(user: User): Boolean = withContext(Dispatchers.IO) {
        val existing = userDao.findByUsername(user.username)
        if (existing != null) {
            false
        } else {
            userDao.insert(user.toEntity())
            true
        }
    }

    suspend fun authenticate(username: String, password: String): User? = withContext(Dispatchers.IO) {
        userDao.authenticate(username, password)?.toDomain()
    }

    suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.findByUsername(username)?.toDomain()
    }

    suspend fun update(originalUsername: String, updated: User): Boolean = withContext(Dispatchers.IO) {
        val existing = userDao.findByUsername(originalUsername) ?: return@withContext false
        val targetUser = updated.toEntity()
        if (originalUsername == updated.username) {
            userDao.update(targetUser)
            true
        } else {
            val usernameTaken = userDao.findByUsername(updated.username) != null
            if (usernameTaken) {
                false
            } else {
                userDao.deleteByUsername(originalUsername)
                userDao.insert(targetUser)
                true
            }
        }
    }

    suspend fun seedDefaults(defaults: List<User>) = withContext(Dispatchers.IO) {
        if (!userDao.hasAny()) {
            defaults.forEach { userDao.insert(it.toEntity()) }
        }
    }
}

private fun UserEntity.toDomain(): User = User(
    email = email,
    username = username,
    password = password
)

private fun User.toEntity(): UserEntity = UserEntity(
    username = username,
    email = email,
    password = password
)
