package map.together.api

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import map.together.dto.ImageUrlDto
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
import map.together.dto.db.*
import okhttp3.MultipartBody
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
     *  fun getUserData(user: User) =
     *      api.getUserData(user.uid)
     *          .subscribeOn(Schedulers.io())
     *          .observeOn(AndroidSchedulers.mainThread())
     *          .map {
     *              Data.fromDto(it)
     *          }
     *          .some_other_logic
     */

    fun uploadImage(image: MultipartBody.Part?): Single<Response<ImageUrlDto>> {
        if (image == null) {
            val imageUrlDto: ImageUrlDto? = null
            val response: Response<ImageUrlDto> = Response.success(201, imageUrlDto)
            return Single.just(response)
        }
        return api.getImageUrl(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createUser(userSignUpDto: UserSignUpDto): Single<Response<UserDto>> =
        api.createUserRequest(userSignUpDto)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun login(token: String): Single<Response<UserDto>> =
        api.loginRequest(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getMyMaps(token: String, search: String?): Single<Response<List<MapDto>>> =
            api.getMyMaps(token, search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun createMap(token: String, name: String): Single<Response<MapDto>> =
            api.createMap(token, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun createLayer(token: String, name: String, mapId: Long): Single<Response<LayerDto>> =
            api.createLayer(token, name, mapId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun createPlace(token: String, placeName: String, mapId: Long, layerId: Long, latitude: String,
                    longitude: String, categoryId: Long): Single<Response<PlaceDto>> =
            api.createPlace(token, placeName, mapId, layerId, latitude, longitude, categoryId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun removeMap(token: String, mapId: Long): Single<Response<MapDto>> =
            api.removeMap(token, mapId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun removeLayer(token: String, mapId: Long, layerId: Long): Single<Response<LayerDto>> =
            api.removeLayerByMap(token, mapId, layerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun removePlace(token: String, mapId: Long, layerId: Long, placeId: Long): Single<Response<PlaceDto>> =
            api.removePlaceByLayerAndMap(token, mapId, layerId, placeId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun leaveMap(token: String, mapId: Long): Single<Response<MapDto>> =
            api.leaveMap(token, mapId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getMapInfo(token: String, mapId: Long): Single<Response<MapInfoDto>> =
            api.getMapinfo(token, mapId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun deactivateUser(token: String): Single<Response<UserDto>> =
            api.deactivateUser(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun changeCategory(token: String, categoryId: Long, name: String, color: Int): Single<Response<CategoryDto>> =
            api.changeCategory(token, categoryId, name, color)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun changeRole(token: String, mapId: Long, email: String, role: Long): Single<Response<UserMapDto>> =
            api.changeUserRole(token, mapId, email, role)
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
        token: String,
        userName: String?,
        passwordHash: String?,
        oldPassword: String?,
        picture: String?
    ): Single<Response<UserDto>> =
        api.changeUserData(token, userName, passwordHash, oldPassword, picture)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getMyCategories(token: String): Single<Response<List<CategoryDto>>> =
            api.getMyCategories(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}
