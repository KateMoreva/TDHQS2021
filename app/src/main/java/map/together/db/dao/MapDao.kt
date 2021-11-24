package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.MapEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM MapEntity")
    fun getAll(): List<MapEntity>

    @Query("SELECT * FROM MapEntity WHERE id = :id")
    fun getById(id: Long): MapEntity

    @Query("SELECT * FROM MapEntity WHERE mainLayerId = :mainLayerId")
    fun getByMainLayer(mainLayerId: Long): MapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(map: MapEntity): Long

    @Insert
    fun insert(mapList: List<MapEntity>): List<Long>

    @Update
    fun update(map: MapEntity): Int

    @Update
    fun update(mapList: List<MapEntity>): Int

    @Delete
    fun delete(map: MapEntity): Int
}