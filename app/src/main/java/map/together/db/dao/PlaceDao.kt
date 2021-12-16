package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM PlaceEntity")
    fun getAll(): List<PlaceEntity>

    @Query("SELECT * FROM PlaceEntity WHERE id = :id")
    fun getById(id: Long): PlaceEntity

    @Query("SELECT * FROM PlaceEntity WHERE ownerId = :ownerId")
    fun getByOwnerId(ownerId: Long): List<PlaceEntity>

    @Query("select * from placeentity where id in (SELECT p.id FROM PlaceEntity as p join placelayerentity as pl join layermapentity as lm join mapentity as m where pl.placeId = p.id and lm.layerId = pl.layerId and m.id = lm.mapId and m.id = :mapId)")
    fun getByMapId(mapId: Long): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(place: PlaceEntity): Long

    @Insert
    fun insert(placeList: List<PlaceEntity>): List<Long>

    @Update
    fun update(place: PlaceEntity): Int

    @Update
    fun update(placeList: List<PlaceEntity>): Int

    @Delete
    fun delete(place: PlaceEntity): Int

    @Delete
    fun delete(placeList: List<PlaceEntity>): Int
}