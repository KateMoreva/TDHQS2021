package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import map.together.activities.auth.LoginActivity
import map.together.api.Api
import map.together.api.ApiUtils
import map.together.screens.LoginScreen
import map.together.utils.logger.Logger
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch

@LargeTest
class ProfileScreenTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)
    private val loginScreen = LoginScreen()

    private val testUserEmail = "test@test.test"
    private val testUserPassword = "qwerty"

    @Test
    fun userCanChangeUsername() {

        val usernameFromServer = getUsernameFromServer()
        Assert.assertNotNull(usernameFromServer)

        val newUserName = UUID.randomUUID().toString()
        loginScreen
                .login(testUserEmail, testUserPassword)
                .pressSettingsButton()
                .pressProfileButton()
                .isUsernameEqualsValue(usernameFromServer!!)
                .pressChangeUsernameButton()
                .enterValue(newUserName)
                .pressPositiveButton()
                .isUsernameEqualsValue(newUserName)

        val usernameFromServer2 = getUsernameFromServer()
        Assert.assertNotNull(usernameFromServer2)
        Assert.assertEquals(usernameFromServer2, newUserName)
    }

    private fun getUsernameFromServer(): String? {
        var usernameFromServer: String? = null
        val latch = CountDownLatch(1)
        val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(testUserEmail, testUserPassword)
        val disposable = Api.login(token).subscribe ({response ->
            val userDto = response.body()!!
            usernameFromServer = userDto.userName
            latch.countDown()
        }, {error ->
            Logger.e(error)
            latch.countDown()
        })
        latch.await()
        disposable.dispose()
        return usernameFromServer
    }
}