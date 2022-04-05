package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.LayerMapEntity

@Dao
interface LayerMapDao {
    @Query("SELECT * FROM LayerMapEntity")
    fun getAll(): List<LayerMapEntity>

    @Query("SELECT * FROM LayerMapEntity WHERE mapId = :mapId")
    fun getByMapId(mapId: Long): List<LayerMapEntity>

    @Query("SELECT * FROM LayerMapEntity WHERE layerId = :layerId")
    fun getByLayerId(layerId: Long): List<LayerMapEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(layerMap: LayerMapEntity): Long

    @Insert
    fun insert(layerMapList: List<LayerMapEntity>): List<Long>

    @Update
    fun update(layerMap: LayerMapEntity): Int

    @Update
    fun update(LayerMapList: List<LayerMapEntity>): Int

    @Delete
    fun delete(layerMap: LayerMapEntity): Int

    @Delete
    fun delete(layerMapList: List<LayerMapEntity>): Int
}