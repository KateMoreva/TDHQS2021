package map.together.activities.auth

import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.email_et
import kotlinx.android.synthetic.main.activity_login.password_et
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.BaseActivity
import map.together.api.Api
import map.together.api.ApiUtils
import map.together.dto.UserDto
import map.together.repository.AuthRepository
import map.together.repository.CurrentUserRepository
import map.together.utils.logger.Logger
import retrofit2.Response
import java.net.HttpURLConnection

class LoginActivity : BaseActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgot_password_tv.visibility = View.INVISIBLE
        not_exist_acc_tv.setOnClickListener {
            router?.showRegistrationPage()
        }

        confirm_button.setOnClickListener {
            val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(
                email_et.text.toString(),
                password_et.text.toString()
            )
            taskContainer.add(
                Api.login(token).subscribe(
                    { onResponse(it, token) },
                    { onFail(it) }
                )
            )
        }
    }

    private fun onResponse(response: Response<UserDto>, token: String) {
        when (response.code()) {
            HttpURLConnection.HTTP_OK -> {
                AuthRepository.doOnLogin(
                    this, token, false,
                    response.body()?.toUserInfo() ?: CurrentUserRepository.CURRENT_USER_EMPTY
                )
                Logger.d(this, "successfully login with code ${response.code()}")
            }
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                MaterialDialog(this).show {
                    title(R.string.cannot_login)
                    message(R.string.incorrect_email_or_password)
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, response.code())
            }
            HttpURLConnection.HTTP_NOT_FOUND -> {
                MaterialDialog(this).show {
                    title(R.string.cannot_login)
                    message(R.string.server_not_available)
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, response.code())
            }
            else -> {
                MaterialDialog(this).show {
                    title(R.string.cannot_login)
                    message(R.string.server_not_available)
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, "unsupported code ${response.code()}")
            }
        }
    }

    private fun onFail(throwable: Throwable) {
        MaterialDialog(this).show {
            title(R.string.cannot_login)
            message(R.string.failed_login)
            negativeButton(R.string.close) {
                it.cancel()
            }
        }
        Logger.e(this, "Login failed : ${throwable.message}")
    }

    override fun getActivityLayoutId() = R.layout.activity_login
}