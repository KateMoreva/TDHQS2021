package map.together.db.generators

import android.content.Context
import map.together.db.entity.*

class InitialDataGenerator {
    companion object {

        fun getCategoryDao(context: Context): List<CategoryEntity> {
            return listOf(
                CategoryEntity("Без категории", 0),
                CategoryEntity("Любимое", 0),
                CategoryEntity("Без категории", null),
            )
        }

        fun getPlace(context: Context): PlaceEntity {
            return PlaceEntity("Место", 0, "", "", 1)
        }

        fun getLayer(context: Context): LayerEntity {
            return LayerEntity("Мой слой", null)
        }

        fun getMap(context: Context): MapEntity {
            return MapEntity("Карта", null, null, null)
        }
    }
}