package map.together.db.generators

import android.content.Context
import android.graphics.Color
import map.together.R
import map.together.db.entity.CategoryEntity
import map.together.db.entity.LayerEntity
import map.together.db.entity.LayerMapEntity
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceEntity
import map.together.db.entity.PlaceLayerEntity
import map.together.db.entity.UserEntity
import map.together.db.entity.UserMapEntity
import okhttp3.internal.immutableListOf

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

        fun getTestUser2(): UserEntity {
            return UserEntity(
                "Tester",
                "test@test.test",
                null,
                -1,
                3
            )
        }

        fun getCategoryDao(context: Context): List<CategoryEntity> {
            return listOf(
                CategoryEntity("Без категории", 1, R.color.grey, 1),
                CategoryEntity("Любимое", 1, R.color.red, 2),
            )
        }

        fun getPlace(context: Context): List<PlaceEntity> {
            return immutableListOf(
                PlaceEntity("Место", 3, "59.9408455", "30.3131542", 1, -1, 2),
                PlaceEntity("Место", 3, "59.9538455", "30.3561542", 2, -1, 1)
            )
        }

        fun getPlaceLayer(context: Context): List<PlaceLayerEntity> {
            return immutableListOf(
                PlaceLayerEntity(1, 1, -1),
                PlaceLayerEntity(2, 1, -1)
            )

        }

        fun getLayerMap(context: Context): LayerMapEntity {
            return LayerMapEntity(1, 1, -1)
        }

        fun getLayer(context: Context): LayerEntity {
            return LayerEntity("Мой слой", 1, -1, 1)
        }

        fun getUserMap(context: Context): UserMapEntity {
            return UserMapEntity(1, 1, -1)
        }

        fun getMap(context: Context): MapEntity {
            return MapEntity("Карта", 1, 1)
        }
    }
}