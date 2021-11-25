package map.together.lifecycle

import map.together.activities.WelcomeActivity
import map.together.activities.auth.LoginActivity
import map.together.activities.auth.RegistrationActivity
import map.together.fragments.BaseFragment
import java.io.Serializable
import kotlin.reflect.KClass

sealed class Page : Serializable {

    sealed class Activity : Page() {

        abstract val clazz: KClass<out android.app.Activity>

        object Login : Activity() {
            override val clazz = LoginActivity::class
        }

        object Main : Activity() {
            override val clazz = WelcomeActivity::class
        }

        object Registration : Activity() {
            override val clazz = RegistrationActivity::class
        }
    }

    sealed class Fragment : Page() {

        abstract val clazz: KClass<out BaseFragment>

    }

    companion object {
        const val PAGE_KEY = "PAGE"
        const val USER_ID_KEY = "USER_ID"
        const val MAP_ID_KEY = "MAP_ID"
    }
}