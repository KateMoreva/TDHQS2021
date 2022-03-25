//package map.together
//
//import com.google.gson.JsonParser
//import junit.framework.Assert.assertEquals
//import map.together.utils.AuthenticationManager
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.koin.core.context.startKoin
//import org.koin.dsl.module
//import org.koin.test.KoinTest
//
//
//class AuthenticationManagerTest : KoinTest {
//
//    lateinit var server : MockWebServer
//
//    @Before
//    fun initTest(){
//        server = MockWebServer()
//    }
//
//    @After
//    fun shutdown(){
//        server.shutdown()
//    }
//
//    @Test
//    fun authentication_sends_proper_body(){
//        server.apply{
//            enqueue(MockResponse().setBody(MockResponseFileReader("login_success.json").content))
//        }
//
//        val baseUrl = server.url("")
//
//        //we create the AuthenticationManager using the Base Url provided by the Mock Server
//        startKoin(listOf(module {
//            single { AuthenticationManager(baseUrl.url().toString()) }
//        }))
//
//        get<AuthenticationManager>().apply {
//            authenticateBlocking()
//        }
//
//        val testBody = LoginBody(AuthenticationManager.username, AuthenticationManager.password)
//        val requestBody = server.takeRequest().body.readUtf8()
//
//        val json = JsonParser().parse(requestBody).asJsonObject
//        assertEquals(json.get("username").toString().replace("\"",""), testBody.username)
//    }
//}