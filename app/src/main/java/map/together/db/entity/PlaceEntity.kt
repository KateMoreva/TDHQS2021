package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
//        foreignKeys = [
//            ForeignKey(
//                    entity = UserEntity::class,
//                    parentColumns = ["id"],
//                    childColumns = ["ownerId"],
//                    onDelete = ForeignKey.CASCADE
//            ),
//            ForeignKey(
//                    entity = CategoryEntity::class,
//                    parentColumns = ["id"],
//                    childColumns = ["categoryId"],
//                    onDelete = ForeignKey.SET_DEFAULT
//            )
//        ]
)
data class PlaceEntity(
        @ColumnInfo
        var name: String,
        @ColumnInfo
        var ownerId: Long,
        @ColumnInfo
        var latitude: String,
        @ColumnInfo
        var longitude: String,
        @ColumnInfo
        var categoryId: Long = 1,
        @ColumnInfo
        var serverId: Long = -1,
        @PrimaryKey(autoGenerate = true) var id: Long = 0
)
