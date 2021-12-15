package map.together.api

import com.mikepenz.fastadapter.GenericItem
import io.reactivex.Single
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
import map.together.dto.db.CategoryDto
import map.together.dto.db.LayerDto
import map.together.dto.db.MapDto
import map.together.dto.db.PlaceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Your Request-methods should be here
 */
interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("/auth/registration")
    fun createUserRequest(@Body userSignUpDto: UserSignUpDto): Single<Response<UserDto>>

    @GET("/auth/login")
    fun loginRequest(): Single<Response<UserDto>>

    @POST("/auth/profile/change")
    fun changeUserData(
        userName: String,
        passwordHash: String,
        oldPassword: String,
        picture: Any
    ): Single<Response<UserDto>>

    @GET("/owner/places")
    fun getMyPlaces(search: String?): Single<Response<List<PlaceDto>>>

    @GET("/owner/categories")
    fun getMyCategories(search: String?): Single<Response<List<CategoryDto>>>

    @GET("/owner/layers")
    fun getMyLayers(search: String?): Single<Response<List<LayerDto>>>

    @GET("/owner/maps")
    fun getMyMaps(search: String?): Single<Response<List<MapDto>>>

    @POST("/owner/maps/create")
    fun createMap(mapName: String): Single<Response<MapDto>>

    @POST("/owner/categories/create")
    fun createCategory(categoryName: String): Single<Response<CategoryDto>>

    @POST("/owner/categories/change")
    fun changeCategory(categoryId: Long, newName: String): Single<Response<CategoryDto>>

    @POST("/shared/users/change_role")
    fun changeUserRole(mapId: Long, email: String, role: Long): Single<Response<UserDto>>

    @GET("/shared/users/list")
    fun getUsersByMap(mapId: Long, search: String?): Single<Response<List<UserDto>>>

    @POST("/shared/places/remove")
    fun removePlaceByLayerAndMap(
        mapId: Long,
        layerId: Long,
        placeId: Long
    ): Single<Response<PlaceDto>>

    @POST("/shared/places/change")
    fun changePlace(
        mapId: Long,
        layerId: Long,
        placeId: Long,
        newName: String
    ): Single<Response<PlaceDto>>

    @POST("/shared/places/create")
    fun createPlace(
        placeName: String,
        mapId: Long,
        layerId: Long,
        latitude: String,
        longitude: String,
        categoryId: Long
    ): Single<Response<PlaceDto>>

    @POST("/shared/maps/remove")
    fun removeMap(mapId: Long): Single<Response<MapDto>>

    @POST("/shared/maps/change")
    fun changeMap(mapId: Long, newName: String): Single<Response<MapDto>>

    @POST("/shared/layers/change")
    fun changeLayer(mapId: Long, layerId: Long, newName: String): Single<Response<LayerDto>>

    @GET("/shared/layers/list")
    fun getLayersByMap(mapId: Long, search: String?): Single<Response<List<LayerDto>>>

    @POST("/shared/layers/remove")
    fun removeLayerByMap(mapId: Long, layerId: Long): Single<Response<LayerDto>>

    @POST("/shared/layers/create")
    fun createLayer(LayerName: String, mapId: Long): Single<Response<LayerDto>>

    @POST("/shared/layers/demonstrate")
    fun demonstrateLayers(mapId: Long, layersIds: List<Long>): Single<Response<List<Long>>>
}