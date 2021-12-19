package map.together.fragments


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_setting_profile.*
import map.together.R
import map.together.activities.BaseActivity
import map.together.api.Api
import map.together.api.RetrofitApiUtils.IVANSON_SERVER_URL
import map.together.dto.UserDto
import map.together.items.MediaItem
import map.together.repository.AuthRepository
import map.together.repository.CurrentUserRepository
import map.together.utils.MediaLoaderWrapper
import map.together.utils.ResponseActions
import java.net.HttpURLConnection

class SettingsProfileFragment : BaseFragment() {

    var mediaLoaderWrapper: MediaLoaderWrapper? = null

    override fun getFragmentLayoutId(): Int = R.layout.fragment_setting_profile

    override fun getAppbarTitle(): Int = R.string.account

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
        val photoUri = getPhotoUri()
        val mediaItem = if (photoUri == null) null else MediaItem("0", photoUri, MediaItem.DisplayMode.FIT_CENTER)
        mediaLoaderWrapper = MediaLoaderWrapper(
                this,
                change_user_profile_pic_button_id,
                remove_user_profile_pic_button_id,
                mediaItem
        ) {
            val token = CurrentUserRepository.getCurrentUserToken(this.requireContext())
            Api.changeUserData(token!!, null, null, null, "").subscribe(
                    { ResponseActions.onResponse(it, this.requireContext(), HttpURLConnection.HTTP_OK,
                            this::updateUser, HttpURLConnection.HTTP_BAD_REQUEST) },
                    { ResponseActions.onFail(it, this.requireContext()) }
            )
        }
        logout_button_id.setOnClickListener {
            AuthRepository.doOnLogout(this.activity as BaseActivity)
        }
    }

    private fun updateUser(userDto: UserDto?) {
        AuthRepository.doOnLogin(
                this.activity as BaseActivity, CurrentUserRepository.getCurrentUserToken(this.requireContext())!!, false,
                userDto?.toUserInfo() ?: CurrentUserRepository.CURRENT_USER_EMPTY, false
        ) {
            loadImage()
        }
    }

    private fun getPhotoUri(): String? {
        val currentUser = CurrentUserRepository.currentUser
        if (currentUser.value?.pictureUrlStr == null) {
            return null
        }
        return IVANSON_SERVER_URL + "api/auth/photo/" + currentUser.value?.pictureUrlStr
    }

    fun loadImage() {
        Glide
                .with(user_profile_picture_id)
                .asBitmap()
                .load(getPhotoUri())
                .fitCenter()
                .placeholder(R.drawable.ic_access_time_black_24dp)
                .error(R.drawable.ic_outline_error_outline_24)
                .into(user_profile_picture_id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaLoaderWrapper?.onActivityResult(requestCode, resultCode, data)
    }
}
