package map.together.api

import io.reactivex.Single
import map.together.dto.UserDto
import map.together.dto.UserSignUpDto
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

    /**
     * @GET("path/path...")
     * fun getUserTrainings(userId: String): Single<TrainingDto>
     */

    @Headers("Content-Type: application/json")
    @POST("/register")
    fun createUserRequest(@Body userSignUpDto: UserSignUpDto): Single<Response<UserDto>>

    @GET("/users/login")
    fun loginRequest(@Header("Authorization") token: String): Single<Response<UserDto>>

    @PATCH("/users/{id}")
    fun changeUserData(
        @Path("id") id: Long,
        @Header("Authorization") token: String,
        @Body nameDto: Any
    ): Single<Response<UserDto>>
}