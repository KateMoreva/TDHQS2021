package map.together.db.generators

import android.content.Context
import android.graphics.Color
import map.together.db.entity.CategoryEntity
import map.together.db.entity.LayerEntity
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceEntity
import map.together.db.entity.UserEntity

class InitialDataGenerator {
    companion object {
        fun getTestUser(): UserEntity {
            return UserEntity(
                "Tester",
                "test@test.test",
                null,
                0,
                0
            )
        }

        fun getCategoryDao(context: Context): List<CategoryEntity> {
            return listOf(
                CategoryEntity("Без категории", 0, Color.GRAY, 0),
                CategoryEntity("Любимое", 0, Color.RED, 1),
            )
        }

        fun getPlace(context: Context): PlaceEntity {
            return PlaceEntity("Место", 0, "", "", 1, -1, 0)
        }

        fun getLayer(context: Context): LayerEntity {
            return LayerEntity("Мой слой", 0, -1, 0)
//            LayerEntity(context.resources.getString(R.string))
        }

        fun getMap(context: Context): MapEntity {
            return MapEntity("Карта", 0, 0)
//                MapEntity(context.resources.getString(R.string))
        }
    }
}