package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = UserEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["ownerId"],
            )
        ]
)
data class LayerEntity(
        @ColumnInfo
        var name: String,
        @ColumnInfo
        var ownerId: Long,
        var serverId: Long = -1,
        @PrimaryKey(autoGenerate = true) var id: Long = 0
)