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
                -1,
                1
            )
        }

        fun getCategoryDao(context: Context): List<CategoryEntity> {
            return listOf(
                CategoryEntity("Без категории", 1, Color.GRAY, 1),
                CategoryEntity("Любимое", 1, Color.RED, 2),
            )
        }

        fun getPlace(context: Context): PlaceEntity {
            return PlaceEntity("Место", 1, "", "", 1, -1, 1)
        }

        fun getLayer(context: Context): LayerEntity {
            return LayerEntity("Мой слой", 1, -1, 1)
//            LayerEntity(context.resources.getString(R.string))
        }

        fun getMap(context: Context): MapEntity {
            return MapEntity("Карта", 1, 1)
//                MapEntity(context.resources.getString(R.string))
        }
    }
}