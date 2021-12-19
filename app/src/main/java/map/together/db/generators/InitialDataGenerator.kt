package map.together.db.generators

import android.content.Context
import map.together.db.entity.*

class InitialDataGenerator {
    companion object {

        fun getPlace(context: Context): PlaceEntity {
            return PlaceEntity("Место", 1, "", "")
        }

        fun getUser(context: Context): List<UserEntity>{
            return listOf(
                UserEntity("none", "none", null, -1, 1),
                UserEntity("vano", "i.kotov2000@gmail.com", null, -1, 2))
//                MapEntity(context.resources.getString(R.string))
        }
    }
}