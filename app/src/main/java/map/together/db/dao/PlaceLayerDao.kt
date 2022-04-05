package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.PlaceLayerEntity

@Dao
interface PlaceLayerDao {
    @Query("SELECT * FROM PlaceLayerEntity")
    fun getAll(): List<PlaceLayerEntity>

    @Query("SELECT * FROM PlaceLayerEntity WHERE placeId = :placeId")
    fun getByPlaceId(placeId: Long): List<PlaceLayerEntity>

    @Query("SELECT * FROM PlaceLayerEntity WHERE layerId = :layerId")
    fun getByLayerId(layerId: Long): List<PlaceLayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(placeLayer: PlaceLayerEntity): Long

    @Insert
    fun insert(placeLayerList: List<PlaceLayerEntity>): List<Long>

    @Update
    fun update(placeLayer: PlaceLayerEntity): Int

    @Update
    fun update(placeLayerList: List<PlaceLayerEntity>): Int

    @Delete
    fun delete(placeLayer: PlaceLayerEntity): Int

    @Delete
    fun delete(placeLayerList: List<PlaceLayerEntity>): Int
}