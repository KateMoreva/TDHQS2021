package map.together.fragments


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
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
import map.together.utils.MediaLoaderWrapper.Companion.loadImage
import map.together.utils.ResponseActions
import map.together.utils.showSimpleInputMaterialDialog
import map.together.utils.showSimpleMaterialDialog
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

class SettingsProfileFragment : BaseFragment() {

    var mediaLoaderWrapper: MediaLoaderWrapper? = null

    override fun getFragmentLayoutId(): Int = R.layout.fragment_setting_profile

    override fun getAppbarTitle(): Int = R.string.account

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImage()
        val photoUri = getPhotoUri()
        val mediaItem = if (photoUri == null) null else MediaItem("0", photoUri, MediaItem.DisplayMode.FIT_CENTER)
        mediaLoaderWrapper = MediaLoaderWrapper(
            this,
            change_user_profile_pic_button_id,
            remove_user_profile_pic_button_id,
            mediaItem,
            { changeUserPhoto("") },
            { localUrl ->
                (activity as BaseActivity).taskContainer.add(
                    Api.uploadImage(loadImage(localUrl)).subscribe(
                        {
                            ResponseActions.onResponse(it,
                                this.requireContext(),
                                HttpsURLConnection.HTTP_CREATED,
                                { imageUrlDto ->
                                    changeUserPhoto(
                                        imageUrlDto?.photoUrl ?: ""
                                    )
                                },
                                HttpsURLConnection.HTTP_BAD_REQUEST
                            )
                        },
                        { ResponseActions.onFail(it, this.requireContext()) }
                    )
                )

            })
        logout_button_id.setOnClickListener {
            AuthRepository.doOnLogout(this.activity as BaseActivity)
        }
        view.findViewById<MaterialCardView>(R.id.delete_user_button_id).apply {
            setOnClickListener {
                showSimpleMaterialDialog(
                    context = view.context,
                    title = view.context.getString(R.string.confirmation),
                    message = view.context.getString(R.string.delete_account_confirmation),
                    positiveButtonText = view.context.getString(R.string.yes),
                    negativeButtonText = view.context.getString(R.string.cancel),
                    onPositiveClicked = {
                        //TOD: delete user
                        AuthRepository.doOnLogout(activity as BaseActivity)

                    },
                )
            }
        }
        view.findViewById<ImageButton>(R.id.change_user_name_button_id).apply {
            setOnClickListener {
                showSimpleInputMaterialDialog(
                    context = view.context,
                    title = view.context.getString(R.string.edit_name),
                    message = view.context.getString(R.string.enter_new_name),
                    positiveButtonText = view.context.getString(R.string.edit),
                    negativeButtonText = view.context.getString(R.string.cancel),
                    prefillData = user_name_text_field_id.text,
                    maxLength = 50,
                    inputCallback = { _, resultName ->
                        run {
                            user_name_text_field_id.text = resultName
                            // todo add saving to db
                        }
                    }
                )
            }
        }
        user_name_text_field_id.text = CurrentUserRepository.currentUser.value?.userName
            ?: "Username"
        user_email_text_field_id.text = CurrentUserRepository.currentUser.value?.email
            ?: "email@email.email"
    }

    private fun changeUserPhoto(url: String) {
        val token = CurrentUserRepository.getCurrentUserToken(this.requireContext())
        (activity as BaseActivity).taskContainer.add(
            Api.changeUserData(token!!, null, null, null, url).subscribe(
                {
                    ResponseActions.onResponse(
                        it, this.requireContext(), HttpURLConnection.HTTP_OK,
                        this::updateUser, HttpURLConnection.HTTP_BAD_REQUEST
                    )
                },
                { ResponseActions.onFail(it, this.requireContext()) }
            )
        )
    }

    private fun updateUser(userDto: UserDto?) {
        AuthRepository.doOnLogin(
                this.activity as BaseActivity, CurrentUserRepository.getCurrentUserToken(this.requireContext())!!, false,
                userDto?.toUserInfo() ?: CurrentUserRepository.CURRENT_USER_EMPTY, false
        ) {
            setImage()
        }
    }

    private fun getPhotoUri(): String? {
        val currentUser = CurrentUserRepository.currentUser
        if (currentUser.value?.photoUrl == null) {
            return null
        }
        return IVANSON_SERVER_URL + "api/auth/photo/" + currentUser.value?.photoUrl
    }

    fun setImage() {
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
