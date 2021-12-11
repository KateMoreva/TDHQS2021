package map.together.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import map.together.db.converter.DateConverter
import map.together.db.dao.CategoryDao
import map.together.db.dao.LayerDao
import map.together.db.dao.LayerMapDao
import map.together.db.dao.MapDao
import map.together.db.dao.PlaceCategoryDao
import map.together.db.dao.PlaceDao
import map.together.db.dao.PlaceLayerDao
import map.together.db.dao.UserDao
import map.together.db.dao.UserMapDao
import map.together.db.entity.CategoryEntity
import map.together.db.entity.LayerEntity
import map.together.db.entity.LayerMapEntity
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceCategoryEntity
import map.together.db.entity.PlaceEntity
import map.together.db.entity.PlaceLayerEntity
import map.together.db.entity.UserEntity
import map.together.db.entity.UserMapEntity
import map.together.db.generators.InitialDataGenerator

@Database(
        entities = [
            CategoryEntity::class,
            LayerEntity::class,
            LayerMapEntity::class,
            MapEntity::class,
            PlaceCategoryEntity::class,
            PlaceEntity::class,
            PlaceLayerEntity::class,
            UserEntity::class,
            UserMapEntity::class,
        ],
        version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME: String = "mapTogetherDB"

        private var instance: AppDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context): AppDatabase? {
            if (instance != null) {
                return instance
            }

            return synchronized(this) {
                if (instance != null) {
                    instance
                } else {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DATABASE_NAME
                    ).addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            GlobalScope.launch(Dispatchers.IO) {
                                InitialDataGenerator.getMap(context)?.let { instance?.mapDao()?.insert(it) }
                                instance?.categoryDao()?.insert(InitialDataGenerator.getCategoryDao(context))
                                InitialDataGenerator.getLayer(context)?.let { instance?.layerDao()?.insert(it) }
                            }
                        }
                    })
                            .build()
                    instance
                }
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
