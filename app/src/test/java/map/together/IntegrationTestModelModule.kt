//package map.together
//
//import java.io.IOException;
//
//import javax.inject.Named;
//import javax.inject.Singleton;
//
//import dagger.Module;
//import dagger.Provides;
//import io.reactivex.rxjava3.core.Scheduler
//import io.reactivex.rxjava3.schedulers.Schedulers
//import map.together.api.ApiInterface
//import okhttp3.mockwebserver.MockWebServer
//
//
//@Module
//class IntegrationTestModelModule {
//    @Provides
//    @Singleton
//    fun provideApiInterface(mockWebServer: MockWebServer?): ApiInterface {
//        return try {
//            IntegrationApiModule().getApiInterface(mockWebServer)
//        } catch (e: IOException) {
//            throw RuntimeException("Can't create ApiInterface")
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun provideMockWebServer(): MockWebServer {
//        return MockWebServer()
//    }
//
//    @Provides
//    @Singleton
//    @Named("UI_THREAD")
//    fun provideSchedulerUI(): Scheduler {
//        return Schedulers.single()
//    }
//
//    @Provides
//    @Singleton
//    @Named("IO_THREAD")
//    fun provideSchedulerIO(): Scheduler {
//        return Schedulers.single()
//    }
//}