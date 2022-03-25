//package map.together.utils
//
//import io.reactivex.Single
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.POST
//
//class AuthenticationManager(endpoint: String) {
//    var login : LoginResponse? = null
//
//    private val okHttpClient = OkHttpClient.Builder().build()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(endpoint)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//        .build()
//
//    private val api = retrofit.create(LoginCall::class.java)
//
//    fun authenticateBlocking(): LoginResponse?{
//        return api.login(LoginBody(username, password))
//            .doOnSuccess { login = it }
//            .blockingGet()
//    }
//
//    companion object {
//        private const val username = "fake_address@gmail.com"
//        private const val password = "hrfuqai@8979457"
//
//        const val PROD_ENDPOINT = "https://any_api.com"
//    }
//}
//
//interface LoginCall {
//    @POST("/v2/auth/login")
//    fun login(@Body loginBody: LoginBody): Single<LoginResponse>
//}