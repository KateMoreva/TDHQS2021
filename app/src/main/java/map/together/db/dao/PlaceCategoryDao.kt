package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.PlaceCategoryEntity

@Dao
interface PlaceCategoryDao {
    @Query("SELECT * FROM PlaceCategoryEntity")
    fun getAll(): List<PlaceCategoryEntity>

    @Query("SELECT * FROM PlaceCategoryEntity WHERE placeId = :placeId")
    fun getByPlaceId(placeId: Long): List<PlaceCategoryEntity>

    @Query("SELECT * FROM PlaceCategoryEntity WHERE categoryId = :categoryId")
    fun getByCategoryId(categoryId: Long): List<PlaceCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(placeCategory: PlaceCategoryEntity): Long

    @Insert
    fun insert(placeCategoryList: List<PlaceCategoryEntity>): List<Long>

    @Update
    fun update(placeCategory: PlaceCategoryEntity): Int

    @Update
    fun update(placeCategoryList: List<PlaceCategoryEntity>): Int

    @Delete
    fun delete(placeCategory: PlaceCategoryEntity): Int

    @Delete
    fun delete(placeCategoryList: List<PlaceCategoryEntity>): Int
}