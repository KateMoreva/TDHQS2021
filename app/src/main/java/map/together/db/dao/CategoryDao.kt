package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM CategoryEntity WHERE id = :id")
    fun getById(id: Long): CategoryEntity

    @Query("SELECT * FROM CategoryEntity WHERE ownerId = :ownerId")
    fun getByOwnerId(ownerId: Long): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: CategoryEntity): Long

    @Insert
    fun insert(categoryList: List<CategoryEntity>): List<Long>

    @Update
    fun update(category: CategoryEntity): Int

    @Update
    fun update(categoryList: List<CategoryEntity>): Int

    @Delete
    fun delete(category: CategoryEntity): Int
}