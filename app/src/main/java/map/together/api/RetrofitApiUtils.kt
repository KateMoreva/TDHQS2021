package map.together.api

import map.together.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApiUtils {

    const val IVANSON_SERVER_URL = "http://ee68-95-55-86-98.ngrok.io/"
//    const val IVANSON_SERVER_URL = "http://e1ea-31-134-188-195.ngrok.io/"

    @JvmStatic
    fun createApi(): ApiInterface {
        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            okHttpClient.addInterceptor(httpLoggingInterceptor)
        }

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(IVANSON_SERVER_URL)
            .client(okHttpClient.build())
            .build()
            .create(ApiInterface::class.java)
    }
}