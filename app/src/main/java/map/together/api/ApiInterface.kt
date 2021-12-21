package map.together.api

import io.reactivex.Single
import map.together.dto.ImageUrlDto
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
import map.together.dto.db.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Your Request-methods should be here
 */
interface ApiInterface {

    @Multipart
    @POST("/api/auth/loadImage")
    fun getImageUrl(@Part image: MultipartBody.Part): Single<Response<ImageUrlDto>>

    @Headers("Content-Type: application/json")
    @POST("/api/auth/registration")
    fun createUserRequest(@Body userSignUpDto: UserSignUpDto): Single<Response<UserDto>>

    @GET("/api/auth/login")
    fun loginRequest(@Header("Authorization") token: String): Single<Response<UserDto>>

    @POST("/api/auth/deactivateAccount")
    fun deactivateUser(@Header("Authorization") token: String): Single<Response<UserDto>>

    @FormUrlEncoded
    @POST("/api/auth/profile/change")
    fun changeUserData(
            @Header("Authorization") token: String,
            @Field("userName") userName: String?,
            @Field("passwordHash") passwordHash: String?,
            @Field("oldPasswordHash") oldPasswordHash: String?,
            @Field("imageUrl") imageUrl: String?
    ): Single<Response<UserDto>>

    @GET("/api/owner/places")
    fun getMyPlaces(search: String?): Single<Response<List<PlaceDto>>>

    @GET("/api/owner/categories")
    fun getMyCategories(search: String?): Single<Response<List<CategoryDto>>>

    @GET("/api/owner/layers")
    fun getMyLayers(search: String?): Single<Response<List<LayerDto>>>

    @GET("/api/owner/maps")
    fun getMyMaps(@Header("Authorization") token: String,
                  @Query("search") search: String?): Single<Response<List<MapDto>>>

    @FormUrlEncoded
    @POST("/api/owner/maps/create")
    fun createMap(@Header("Authorization") token: String,
                  @Field("mapName") mapName: String): Single<Response<MapDto>>

    @POST("/api/owner/categories/create")
    fun createCategory(categoryName: String): Single<Response<CategoryDto>>

    @FormUrlEncoded
    @POST("/api/owner/categories/change")
    fun changeCategory(@Header("Authorization") token: String,
                       @Field("categoryId") categoryId: Long,
                       @Field("newName") newName: String,
                       @Field("color") color: Int): Single<Response<CategoryDto>>

    @POST("/api/shared/users/change_role")
    fun changeUserRole(mapId: Long, email: String, role: Long): Single<Response<UserDto>>

    @GET("/api/shared/maps/updates")
    fun getMapinfo(@Header("Authorization") token: String,
                   @Query("mapId") mapId: Long): Single<Response<MapInfoDto>>

    @POST("/api/shared/places/remove")
    fun removePlaceByLayerAndMap(
        mapId: Long,
        layerId: Long,
        placeId: Long
    ): Single<Response<PlaceDto>>

    @POST("/api/shared/places/change")
    fun changePlace(
        mapId: Long,
        layerId: Long,
        placeId: Long,
        newName: String
    ): Single<Response<PlaceDto>>

    @POST("/api/shared/places/create")
    fun createPlace(
        placeName: String,
        mapId: Long,
        layerId: Long,
        latitude: String,
        longitude: String,
        categoryId: Long
    ): Single<Response<PlaceDto>>

    @FormUrlEncoded
    @POST("/api/shared/maps/remove")
    fun removeMap(@Header("Authorization") token: String,
                  @Field("mapId") mapId: Long): Single<Response<MapDto>>

    @FormUrlEncoded
    @POST("/api/shared/maps/leave")
    fun leaveMap(@Header("Authorization") token: String,
                  @Field("mapId") mapId: Long): Single<Response<MapDto>>

    @POST("/api/shared/maps/change")
    fun changeMap(mapId: Long, newName: String): Single<Response<MapDto>>

    @POST("/api/shared/layers/change")
    fun changeLayer(mapId: Long, layerId: Long, newName: String): Single<Response<LayerDto>>

    @GET("/api/shared/layers/list")
    fun getLayersByMap(mapId: Long, search: String?): Single<Response<List<LayerDto>>>

    @POST("/api/shared/layers/remove")
    fun removeLayerByMap(mapId: Long, layerId: Long): Single<Response<LayerDto>>

    @POST("/api/shared/layers/create")
    fun createLayer(LayerName: String, mapId: Long): Single<Response<LayerDto>>

    @POST("/api/shared/layers/demonstrate")
    fun demonstrateLayers(mapId: Long, layersIds: List<Long>): Single<Response<List<Long>>>
}