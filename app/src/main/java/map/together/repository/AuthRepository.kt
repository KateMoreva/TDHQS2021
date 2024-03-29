package map.together.repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.activities.BaseActivity
import map.together.db.entity.UserEntity
import map.together.model.UserInfo

object AuthRepository {
    const val MAP_PREFERENCE = "Working"
    const val USER_TOKEN = "user_token_key"
    const val USER_INFO_KEY = "user_info_key"

    fun doOnLogin(activity: BaseActivity, token: String, needRemember: Boolean, userInfo: UserInfo, goToMain: Boolean = true, actionsAfter: () -> Unit = {}) {
        activity.apply {
            CurrentUserRepository.currentUser.value = userInfo
            GlobalScope.launch(Dispatchers.IO) {
                var user = database?.userDao()?.getByEmail(userInfo.email)
                if (user == null) {
                    user = UserEntity(
                            userInfo.userName,
                            userInfo.email,
                            userInfo.photoUrl,
                            userInfo.id
                    )
                    database?.userDao()?.insert(user)
                } else {
                    user.userName = userInfo.userName
                    user.email = userInfo.email
                    user.photoUrl = userInfo.photoUrl
                    user.id = userInfo.id
                    database?.userDao()?.update(user)
                }
                withContext(Dispatchers.Main) {
                    if (needRemember) {
                        getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit()
                            .putString(USER_TOKEN, token).apply()
                        getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit()
                            .putString(USER_INFO_KEY, Gson().toJson(user)).apply()
                    }
                    if (goToMain) {
                        // TODO: save recently used map and load it again
                        router?.showMapsLibraryPage()
                        finish()
                    }
                    actionsAfter.invoke()
                }
            }
        }
    }

    fun doOnLoginFake(
        activity: BaseActivity,
        token: String,
        needRemember: Boolean,
        userInfo: UserInfo,
        goToMain: Boolean = true,
        actionsAfter: () -> Unit = {}
    ) {
        activity.apply {
            CurrentUserRepository.currentUser.value = userInfo
            GlobalScope.launch(Dispatchers.IO) {
                var user = database?.userDao()?.getByEmail(userInfo.email)
                if (user == null) {
                    user = UserEntity(
                        userInfo.userName,
                        userInfo.email,
                        userInfo.photoUrl,
                        userInfo.id
                    )
                    database?.userDao()?.insert(user)
                } else {
                    user.userName = userInfo.userName
                    user.email = userInfo.email
                    user.photoUrl = userInfo.photoUrl
                    user.id = userInfo.id
                    database?.userDao()?.update(user)
                }
                withContext(Dispatchers.Main) {
                    if (needRemember) {
                        getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit()
                            .putString(USER_TOKEN, token).apply()
                        getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit()
                            .putString(USER_INFO_KEY, Gson().toJson(user)).apply()
                    }
                    if (goToMain) {
                        // TODO: save recently used map and load it again
                        router?.showMapsLibraryPage()
                        finish()
                    }
                    actionsAfter.invoke()
                }
            }
        }
    }

    fun doOnLogout(activity: BaseActivity) {
        activity.apply {
            CurrentUserRepository.currentUser.value = null
            getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit().remove(USER_TOKEN)
                .apply()
            getSharedPreferences(MAP_PREFERENCE, Context.MODE_PRIVATE).edit().remove(USER_INFO_KEY)
                .apply()
            router?.showLoginPage()
            finish()
        }
    }
}