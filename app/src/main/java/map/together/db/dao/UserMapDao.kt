package map.together.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import map.together.db.entity.UserMapEntity

@Dao
interface UserMapDao {
    @Query("SELECT * FROM UserMapEntity")
    fun getAll(): List<UserMapEntity>

    @Query("SELECT * FROM UserMapEntity WHERE userId = :userId")
    fun getByUserId(userId: Long): List<UserMapEntity>

    @Query("SELECT * FROM UserMapEntity WHERE mapId = :mapId")
    fun getByMapId(mapId: Long): List<UserMapEntity>

    @Query("SELECT * FROM UserMapEntity WHERE mapId = :mapId and userId = :userId")
    fun getByMapIdAndUserId(mapId: Long, userId: Long): UserMapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userMap: UserMapEntity): Long

    @Insert
    fun insert(userMapList: List<UserMapEntity>): List<Long>

    @Update
    fun update(userMap: UserMapEntity): Int

    @Update
    fun update(userMapList: List<UserMapEntity>): Int

    @Delete
    fun delete(userMap: UserMapEntity): Int

    @Delete
    fun delete(userMapList: List<UserMapEntity>): Int
}