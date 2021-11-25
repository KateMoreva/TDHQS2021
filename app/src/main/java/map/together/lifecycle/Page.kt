package map.together.lifecycle

import map.together.activities.MapActivity
import map.together.activities.WelcomeActivity
import map.together.activities.auth.LoginActivity
import map.together.activities.auth.RegistrationActivity
import map.together.fragments.BaseFragment
import map.together.fragments.MapFragment
import java.io.Serializable
import kotlin.reflect.KClass

sealed class Page : Serializable {

    sealed class Activity : Page() {

        abstract val clazz: KClass<out android.app.Activity>

        object Login : Activity() {
            override val clazz = LoginActivity::class
        }

        object Main : Activity() {
            override val clazz = MapActivity::class
        }

        object Registration : Activity() {
            override val clazz = RegistrationActivity::class
        }
    }

    sealed class Fragment : Page() {

        abstract val clazz: KClass<out BaseFragment>

        object MainMap : Fragment() {
            override val clazz = MapFragment::class
        }

    }

    companion object {
        const val PAGE_KEY = "PAGE"
        const val USER_ID_KEY = "USER_ID"
        const val MAP_ID_KEY = "MAP_ID"
        const val LAYER_ID_KEY = "MAP_ID"
    }
}