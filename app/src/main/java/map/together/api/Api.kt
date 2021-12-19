package map.together.api

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
import retrofit2.Response

/**
 *
 * High-level access to api
 *
 */
object Api {

    private val api = RetrofitApiUtils.createApi()

    /**
     *
     * Example:
     * User: class, required in our client
     * UserDto: class, that represents entity, returned from request
     *
     * Reason: sometimes it's easier to use data structure different from
     * structure retrieved from backend
     *
     * So, it's better to have two separate classes, one for data transfer
     * and one for usage in our app
     *
     * Also, other actions could be performed in these methods, including
     * subscribeOn() and observeOn()
     *
     *  fun getUserTrainings(user: User) =
     *      api.getUserTrainings(user.uid)
     *          .subscribeOn(Schedulers.io())
     *          .observeOn(AndroidSchedulers.mainThread())
     *          .map {
     *              Training.fromDto(it)
     *          }
     *          .some_other_logic
     */

    fun createUser(userSignUpDto: UserSignUpDto): Single<Response<UserDto>> =
        api.createUserRequest(userSignUpDto)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun login(token: String): Single<Response<UserDto>> =
        api.loginRequest(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun fakeLogin(token: String): Single<Response<UserDto>> {
        val testUser = UserDto(
            1,
            "Testov",
            "test@test.test",
            null
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }

    fun changeUserData(
        userName: String,
        passwordHash: String,
        oldPassword: String,
        picture: Any
    ): Single<Response<UserDto>> =
        api.changeUserData(userName, passwordHash, oldPassword, picture)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
