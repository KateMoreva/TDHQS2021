package map.together.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import map.together.model.UserInfo

object CurrentUserRepository {

    val CURRENT_USER_EMPTY: UserInfo = UserInfo(
            id = -1,
            uid = "-1",
            userName = "userName",
            email = "email",
            pictureUrlStr = null)

    var currentUser: MutableLiveData<UserInfo> = MutableLiveData()
        internal set

    fun getCurrentUserToken(context: Context): String? =
            context.getSharedPreferences(AuthRepository.MAP_PREFERENCE, Context.MODE_PRIVATE)
                    .getString(AuthRepository.USER_TOKEN, null)

    fun getCurrentUserFromStorage(context: Context): UserInfo? =
            Gson().fromJson(context.getSharedPreferences(AuthRepository.MAP_PREFERENCE, Context.MODE_PRIVATE)
                    .getString(AuthRepository.USER_INFO_KEY, null), UserInfo::class.java)
}