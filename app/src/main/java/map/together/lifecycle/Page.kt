package map.together.lifecycle

import map.together.activities.MapActivity
import map.together.activities.MapsListFragment
import map.together.activities.auth.LoginActivity
import map.together.activities.auth.RegistrationActivity
import map.together.fragments.AboutFragment
import map.together.fragments.BaseFragment
import map.together.fragments.PlacesFragment
import map.together.fragments.SettingsCategoriesFragment
import map.together.fragments.SettingsProfileFragment
import map.together.fragments.SettingsFragment
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.mockActivities.auth.FakeMapActivity
import map.together.mockActivities.auth.FakeMapsList
import map.together.mockActivities.auth.FakeRegistrationActivity
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

        object FakeMain : Activity() {
            override val clazz = FakeMapActivity::class
        }


        object Registration : Activity() {
            override val clazz = RegistrationActivity::class
        }

        object FakeRegistration : Activity() {
            override val clazz = FakeRegistrationActivity::class
        }
    }

    sealed class Fragment : Page() {

        abstract val clazz: KClass<out BaseFragment>

        object Settings : Fragment() {
            override val clazz = SettingsFragment::class
        }

        object SettingsAbout : Fragment() {
            override val clazz = AboutFragment::class
        }

        object SettingsProfile : Fragment() {
            override val clazz = SettingsProfileFragment::class
        }

        object SettingsCategories : Fragment() {
            override val clazz = SettingsCategoriesFragment::class
        }

        object Places : Fragment() {
            override val clazz = PlacesFragment::class
        }

        object MapsLibrary : Fragment() {
            override val clazz = MapsListFragment::class
        }

        object FakeMapsLibrary : Fragment() {
            override val clazz = FakeMapsList::class
        }

    }

    companion object {
        const val PAGE_KEY = "PAGE"
        const val LAYERS_IDS = "LAYERS_IDS"
        const val USER_ID_KEY = "USER_ID"
        const val MAP_ID_KEY = "MAP_ID"
        const val LAYER_ID_KEY = "MAP_ID"
    }
}