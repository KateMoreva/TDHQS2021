package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.MapEntity
import map.together.db.entity.UserEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM MapEntity")
    fun getAll(): List<MapEntity>

    @Query("SELECT * FROM MapEntity WHERE id = :id")
    fun getById(id: Long): MapEntity

    @Query("SELECT * FROM MapEntity WHERE mainLayerId = :mainLayerId")
    fun getByMainLayer(mainLayerId: Long): MapEntity

    @Query("Select * from MapEntity where id in (SELECT mapId FROM MapEntity as m join usermapentity as um WHERE m.id = um.mapId and um.userId = :userId)")
    fun getByUserHasAccess(userId: Long): List<MapEntity>

    @Query("SELECT ownerId FROM MapEntity as m join layerentity as l WHERE m.mainLayerId = l.id")
    fun getOwnerId(): Long

    @Query("select * from userentity where id in (SELECT userId FROM usermapentity as um WHERE :mapId  = um.mapId)")
    fun getMembersById(mapId: Long): List<UserEntity>

    @Query("select count(id) from userentity where id in (SELECT userId FROM usermapentity as um WHERE um.mapId = :mapId)")
    fun countMembersById(mapId: Long): Long

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