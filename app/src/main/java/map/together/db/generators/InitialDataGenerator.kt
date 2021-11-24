package training.journal.db.generators

import android.content.Context
import map.together.db.entity.CategoryEntity
import map.together.db.entity.LayerEntity
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceEntity

class InitialDataGenerator {
    companion object {

        fun getCategoryDao(context: Context): List<CategoryEntity> {
            return listOf(
                    CategoryEntity("Без категории", 0),
                    CategoryEntity("Любимое", 0),
            )
        }

        fun getPlace(context: Context): PlaceEntity {
            return PlaceEntity("Место", 0, "", "")
        }

        fun getLayer(context: Context): LayerEntity {
            return LayerEntity("Мой слой", 0)
//            LayerEntity(context.resources.getString(R.string))
        }

        fun getMap(context: Context): MapEntity {
            return MapEntity("Карта", 0, 0)
//                MapEntity(context.resources.getString(R.string))
        }
    }
}