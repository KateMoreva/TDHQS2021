package map.together.activities.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.item_media_loader.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.BaseActivity
import map.together.api.Api
import map.together.dto.ImageUrlDto
import map.together.dto.UserDto
import map.together.fragments.MediaLoaderWrapper
import map.together.items.ItemsList
import map.together.items.MediaItem
import map.together.model.UserSignUpInfo
import map.together.toast.ToastUtils
import map.together.utils.auth.AuthorizationHelper
import map.together.utils.auth.SignUpDataCorrectType
import map.together.utils.logger.Logger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.net.HttpURLConnection

class RegistrationActivity : BaseActivity() {
    private val userIcons: ItemsList<MediaItem> = ItemsList(mutableListOf())
    private var mediaLoader: MediaLoaderWrapper? = null


    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancel_button.setOnClickListener { finish() }


        mediaLoader = MediaLoaderWrapper(
            this,
            exercise_image_switcher,
            edit_content_btn,
            no_content,
            userIcons
        )

        confirm_button.setOnClickListener {
            resetRequiredTips()
            val userSignUpInfo = UserSignUpInfo(
                email_et.text.toString(),
                password_et.text.toString(),
                confirm_password_et.text.toString(),
                user_name_et.text.toString(),
                null
            )
            when (AuthorizationHelper.checkCorrectnessInputData(userSignUpInfo)) {
                SignUpDataCorrectType.INCORRECT_EMAIL -> {
                    email_required.setTextColor(Color.RED)
                    ToastUtils.showShortToast(this, R.string.incorrect_email)
                }
                SignUpDataCorrectType.INCORRECT_PASSWORD -> {
                    password_required.setTextColor(Color.RED)
                    ToastUtils.showShortToast(this, R.string.incorrect_password)
                }
                SignUpDataCorrectType.INCORRECT_CONFIRM_PASSWORD -> {
                    confirm_password_required.setTextColor(Color.RED)
                    ToastUtils.showShortToast(this, R.string.incorrect_confirm_passowrd)
                }
                SignUpDataCorrectType.INCORRECT_NAME -> {
                    user_name_required.setTextColor(Color.RED)
                    ToastUtils.showShortToast(this, R.string.incorrect_first_name)
                }
                SignUpDataCorrectType.CORRECT -> taskContainer.add(
                    Api.uploadImage(loadImage(mediaLoader!!.getLoadedData().firstOrNull())).subscribe(
                            { onImageLoaderResponse(it, userSignUpInfo) },
                            { onFail(it) }
                    )

                )
            }
        }
    }

    private fun loadImage(imageUrl: String?): MultipartBody.Part? {
        if (imageUrl == null)
            return null
        val file = File(imageUrl)
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaLoader?.onActivityResult(requestCode, resultCode, data)
    }

    private fun onImageLoaderResponse(response: Response<ImageUrlDto>, userSignUpInfo: UserSignUpInfo) {
        when (response.code()) {
            HttpURLConnection.HTTP_CREATED -> {
                Logger.d(this, "img loaded successfully with code ${response.code()}")
                userSignUpInfo.imageUrl = response.body()?.photoUrl
                Api.createUser(userSignUpInfo.toUserSignUpDto()).subscribe(
                        { onResponse(it) },
                        { onFail(it) }
                )
            }
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                MaterialDialog(this).show {
                    title(R.string.cannot_sign_up)
                    message(text = response.errorBody()?.string())
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, "fail registration ${response.code()}")
            }
            else -> {
                ToastUtils.showShortToast(this, R.string.failed_registr)
                Logger.d(this, "unsupported code ${response.code()}")
            }
        }
    }


    private fun onResponse(response: Response<UserDto>) {
        when (response.code()) {
            HttpURLConnection.HTTP_CREATED -> {
                ToastUtils.showShortToast(this, R.string.successfully)
                finish()
                Logger.d(this, "successfully sign up with code ${response.code()}")
            }
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                MaterialDialog(this).show {
                    title(R.string.cannot_sign_up)
                    message(text = response.errorBody()?.string())
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                email_required.setTextColor(Color.RED)
                Logger.d(this, "fail registration ${response.code()}")
            }
            else -> {
                ToastUtils.showShortToast(this, R.string.failed_registr)
                Logger.d(this, "unsupported code ${response.code()}")
            }
        }
    }

    private fun onFail(throwable: Throwable) {
        ToastUtils.showErrorToast(this)
        Logger.e(this, throwable.message ?: throwable)
    }

    private fun resetRequiredTips() {
        email_required.setTextColor(Color.BLACK)
        password_required.setTextColor(Color.BLACK)
        confirm_password_required.setTextColor(Color.BLACK)
        user_name_required.setTextColor(Color.BLACK)
    }

    override fun getActivityLayoutId(): Int = R.layout.activity_registration
}