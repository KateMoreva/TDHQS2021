package map.together.api

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import map.together.dto.ImageUrlDto
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
import map.together.dto.db.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*

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

    fun getMyPlaces(token: String, search: String?): Single<Response<List<PlaceDto>>> =
        api.getMyPlaces(token, search)
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

    fun removePlace(
        token: String,
        mapId: Long,
        layerId: Long,
        placeId: Long
    ): Single<Response<PlaceDto>> =
        api.removePlaceByLayerAndMap(token, mapId, layerId, placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun removeAllPlaces(token: String): Single<Response<List<Long>>> =
        api.removeMyPlaces(token, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun removeAllLayers(token: String): Single<Response<List<Long>>> =
        api.removeMyLayers(token, 1)
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

    fun changeCategory(
        token: String,
        categoryId: Long,
        name: String,
        color: Int
    ): Single<Response<CategoryDto>> =
        api.changeCategory(token, categoryId, name, color)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun changeRole(
        token: String,
        mapId: Long,
        email: String,
        role: Long
    ): Single<Response<UserMapDto>> =
        api.changeUserRole(token, mapId, email, role)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun fakecreatePlace(
        token: String, placeName: String, mapId: Long, layerId: Long, latitude: String,
        longitude: String, categoryId: Long
    ): Single<Response<PlaceDto>> {
        val testUser = PlaceDto(
            7,
            placeName,
            1,
            latitude,
            longitude,
            true,
            true,
            1, 1, "name",
            1
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }

    fun fakeremovePlace(
        token: String,
        mapId: Long,
        layerId: Long,
        placeId: Long
    ): Single<Response<PlaceDto>> {
        val testUser = PlaceDto(
            placeId,
            "name",
            1,
            "true",
            "true",
            true,
            true,
            1, 1, "name",
            1
        )
        val response = Response.success(200, testUser)
        return Single.just(response)

    }


    fun fakecreateLayer(token: String, name: String, mapId: Long): Single<Response<LayerDto>> {
        val testUser = LayerDto(
            7,
            name,
            1,
            true,
            true,
            Collections.emptyList(),
            1
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }

    fun fakeremoveLayer(token: String, mapId: Long, layerId: Long): Single<Response<LayerDto>> {
        val testUser = LayerDto(
            layerId,
            "name",
            1,
            true,
            true,
            Collections.emptyList(),
            1
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }


    fun fakeUploadImage(image: MultipartBody.Part?): Single<Response<ImageUrlDto>> {
        if (image == null) {
            val imageUrlDto: ImageUrlDto? = null
            val response: Response<ImageUrlDto> = Response.success(201, imageUrlDto)
            return Single.just(response)
        }
        val uu: ImageUrlDto = ImageUrlDto(
            "data:image/jpeg;base64,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"
        )
        val response = Response.success(200, uu)
        return Single.just(response)
    }

    fun fakechangeRole(
        token: String,
        userID: String,
        name: String,
        email: String,
        role: Long
    ): Single<Response<UserMapDto>> {
        val testUser = UserMapDto(
            1,
            name,
            email,
            null,
            role.toString(),
            1, true, true
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }

    fun fakeCreateUser(userSignUpDto: UserSignUpDto): Single<Response<UserDto>> {
        val testUser = UserDto(
            1,
            userSignUpDto.userName,
            userSignUpDto.email,
            userSignUpDto.photoUrl
        )
        val response = Response.success(200, testUser)
        return Single.just(response)
    }


    fun fakeGetMapInfo(token: String, mapId: Long): Single<Response<MapInfoDto>> {
        val testUser = UserMapDto(
            1,
            "name",
            "email",
            null,
            "role.toString()",
            1, true, true
        )
        val map = MapDto(
            mapId,
            "Testv", 1, 1, 1, false, false, "ADMIN", 1, 1
        )
        val layer = LayerDto(
            1,
            "name",
            1,
            true,
            true,
            Collections.emptyList(),
            1
        )
        val mapInfo = MapInfoDto(
            map,
            Collections.singletonList(testUser),
            Collections.singletonList(layer),
            Collections.emptyList(),
            1
        )
        val response = Response.success(200, mapInfo)
        return Single.just(response)
    }


    fun fakeLogin(token: String, email: String): Single<Response<UserDto>> {
        val testUser = UserDto(
            1,
            "Testov",
            "test@test.test",
            null
        )
        if (email != "test@test.test") {
            val jsonObject = JSONObject()
            jsonObject.put("name", "Ancd test")
            val body = jsonObject.toString()
                .toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val response = Response.error<UserDto>(403, body)
            return Single.just(response)
        }
        val response = Response.success(200, testUser)
        return Single.just(response)
    }

    fun fakeCreateMap(token: String, name: String): Single<Response<MapDto>> {
        val map = MapDto(
            1,
            "Testv", 1, 1, 1, true, true, "ADMIN", 1, 1
        )
        val response = Response.success(200, map)
        return Single.just(response)
    }

    fun fakeremoveMap(token: String, mapId: Long): Single<Response<MapDto>> {
        val map = MapDto(
            mapId,
            "Testv", 1, 1, 1, false, false, "ADMIN", 1, 1
        )
        val response = Response.success(200, map)
        return Single.just(response)
    }

    fun fakeleaveMap(token: String, mapId: Long): Single<Response<MapDto>> {
        val map = MapDto(
            mapId,
            "Testv", 1, 1, 1, false, false, "ADMIN", 1, 1
        )
        val response = Response.success(200, map)
        return Single.just(response)
    }

    fun fakegetMyMaps(token: String, search: String?): Single<Response<List<MapDto>>> {
        val map = MapDto(
            1,
            "Testv", 1, 1, 1, true, true, "ADMIN", 1, 1
        )
        val response = Response.success(200, Collections.singletonList(map))
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

    fun fakegetMyCategories(token: String): Single<Response<List<CategoryDto>>> {
        val map = CategoryDto(
            1,
            "Testv", 1, 1
        )
        val response = Response.success(200, Collections.singletonList(map))
        return Single.just(response)

    }

    fun getMyCategories(token: String): Single<Response<List<CategoryDto>>> =
        api.getMyCategories(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun createCategory(token: String, categoryName: String): Single<Response<CategoryDto>> {
        return api.createCategory(token, categoryName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
