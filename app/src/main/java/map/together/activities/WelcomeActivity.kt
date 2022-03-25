package map.together.activities

import android.os.Bundle
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.api.Api
import map.together.repository.AuthRepository
import map.together.repository.CurrentUserRepository
import map.together.utils.logger.Logger
import java.net.HttpURLConnection

@InternalCoroutinesApi
class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserFromStorage = CurrentUserRepository.getCurrentUserFromStorage(this)
        val token = CurrentUserRepository.getCurrentUserToken(this)

        if (token != null) {
            if (currentUserFromStorage != null) {
                AuthRepository.doOnLogin(this, token, false, currentUserFromStorage)
                Logger.d(this, "successfully login from storage")
            } else {
                taskContainer.add(
                    Api.login(token).subscribe(
                        {
                            if (it.code() == HttpURLConnection.HTTP_OK) {
                                AuthRepository.doOnLogin(
                                    this, token, true,
                                    it.body()?.toUserInfo()
                                        ?: CurrentUserRepository.CURRENT_USER_EMPTY
                                )
                                Logger.d(this, "successfully login with code ${it.code()}")
                            } else {
                                router?.showLoginPage()
                                Logger.d(this, "Incorrect password or email : ${it.code()}")
                                finish()
                            }
                        },
                        {
                            router?.showLoginPage()
                            Logger.e(this, "Login failed : ${it.message}")
                            finish()
                        }
                    )
                )
            }
        } else {
            router?.showLoginPage()
            finish()
        }
    }

    override fun getActivityLayoutId() = R.layout.activity_welcome
}