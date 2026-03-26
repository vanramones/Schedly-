package com.example.schedly.data.local

import com.example.schedly.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeCategories().map { entities -> entities.map { it.toDomain() } }

    suspend fun insert(name: String): Int? = withContext(Dispatchers.IO) {
        val id = categoryDao.insert(CategoryEntity(name = name))
        if (id == -1L) null else id.toInt()
    }

    suspend fun delete(categoryId: Int) = withContext(Dispatchers.IO) {
        categoryDao.deleteById(categoryId)
    }

    suspend fun seedDefaults(names: List<String>) = withContext(Dispatchers.IO) {
        names.forEach { categoryDao.insert(CategoryEntity(name = it)) }
    }

    suspend fun getAll(): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getAll().map { it.toDomain() }
    }

    suspend fun findByName(name: String): Category? = withContext(Dispatchers.IO) {
        categoryDao.findByName(name)?.toDomain()
    }
}

private fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name
)
