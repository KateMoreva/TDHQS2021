package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
        primaryKeys = ["placeId", "categoryId"],
        foreignKeys = [
            ForeignKey(
                    entity = PlaceEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["placeId"],
                    onDelete = ForeignKey.CASCADE

            ),
            ForeignKey(
                    entity = CategoryEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["categoryId"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class PlaceCategoryEntity(
        @ColumnInfo
        var placeId: Long,
        @ColumnInfo
        var categoryId: Long,
        var serverId: Long = -1,
)