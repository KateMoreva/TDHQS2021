//package map.together
//
//import map.together.api.ApiInterface
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.mockwebserver.Dispatcher
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import okhttp3.mockwebserver.RecordedRequest
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.IOException
//
//
//class IntegrationApiModule {
//    @Throws(IOException::class)
//    fun getApiInterface(mockWebServer: MockWebServer): ApiInterface {
//        mockWebServer.start()
//        val testUtils = TestUtils()
//        val dispatcher: Dispatcher = object : Dispatcher() {
//            @Throws(InterruptedException::class)
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                if (request.path == "/users/" + TestConst.TEST_OWNER.toString() + "/repos") {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/repos.json"))
//                } else if (request.path == "/repos/" + TestConst.TEST_OWNER.toString() + "/" + TestConst.TEST_REPO.toString() + "/branches") {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/branches.json"))
//                } else if (request.path == "/repos/" + TestConst.TEST_OWNER.toString() + "/" + TestConst.TEST_REPO.toString() + "/contributors") {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/contributors.json"))
//                }
//                return MockResponse().setResponseCode(404)
//            }
//        }
//        mockWebServer.setDispatcher(dispatcher)
//        val baseUrl = mockWebServer.url("/")
//        return getApiInterface(baseUrl.toString())!!
//    }
//
//    fun getApiInterface(url: String?): ApiInterface? {
//        val httpClient = OkHttpClient()
//        if (false) {
//            httpClient.interceptors.add(Interceptor { chain: Interceptor.Chain ->
//                val original: Request = chain.request()
//                val request: Request = original.newBuilder()
//                    .header("Authorization", "Basic AUTH_64")
//                    .method(original.method, original.body)
//                    .build()
//                chain.proceed(request)
//            })
//        }
////        if (true) {
////            val interceptor = HttpLoggingInterceptor()
////            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
////            httpClient.interceptors.i(interceptor)
////        }
//        val builder = Retrofit.Builder().baseUrl(url)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//        builder.client(httpClient)
//        return builder.build().create(ApiInterface::class.java)
//    }
//}