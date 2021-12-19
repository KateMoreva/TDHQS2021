package map.together.lifecycle

import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import map.together.db.dao.*

abstract class AppMap {

    companion object {
        private const val MAP_API_KEY: String = "91d7da16-2d86-4367-9ee8-4092731bbd2f"

        private var isInitialized: Boolean = false

        @InternalCoroutinesApi
        fun initialize() {
            if (isInitialized) {
                return
            }

            synchronized(this) {
                if (isInitialized) {
                    return
                }
                isInitialized = true
                MapKitFactory.setApiKey(MAP_API_KEY)
            }
        }
    }

    abstract fun categoryDao(): CategoryDao
    abstract fun layerDao(): LayerDao
    abstract fun layerMapDao(): LayerMapDao
    abstract fun mapDao(): MapDao
    abstract fun placeCategoryDao(): PlaceCategoryDao
    abstract fun placeDao(): PlaceDao
    abstract fun placeLayerDao(): PlaceLayerDao
    abstract fun userDao(): UserDao
    abstract fun userMapDao(): UserMapDao
}
