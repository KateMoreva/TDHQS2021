package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.LayerEntity

@Dao
interface LayerDao {
    @Query("SELECT * FROM LayerEntity")
    fun getAll(): List<LayerEntity>

    @Query("SELECT * FROM LayerEntity WHERE id = :id")
    fun getById(id: Long): LayerEntity

    @Query("SELECT * FROM LayerEntity WHERE ownerId = :ownerId")
    fun getByOwnerId(ownerId: Long): LayerEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(layer: LayerEntity): Long

    @Insert
    fun insert(layerList: List<LayerEntity>): List<Long>

    @Update
    fun update(layer: LayerEntity): Int

    @Update
    fun update(layerList: List<LayerEntity>): Int

    @Delete
    fun delete(layer: LayerEntity): Int

    @Delete
    fun delete(layerList: List<LayerEntity>): Int
}