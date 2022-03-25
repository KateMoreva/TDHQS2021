//package map.together
//
//import com.google.gson.JsonObject
//import io.reactivex.rxjava3.subscribers.TestSubscriber
//import junit.framework.Assert.assertEquals
//import kotlinx.android.synthetic.main.activity_login.*
//import map.together.api.Api
//import map.together.api.ApiUtils
//import map.together.dto.db.MapDto
//import map.together.utils.ResponseActions.onFail
//import map.together.utils.ResponseActions.onResponse
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import okio.Buffer
//import org.junit.Test
//import retrofit2.Response
//
//class ApiModelTests {
//
//    @Test
//    fun testGetRepoList() {
//
//        val server = MockWebServer()
//
//// Schedule the authentication response.
//        server.enqueue(MockResponse().apply {
//            addHeader("Content-Type", "application/json")
//            // Generate the authentication response body.
//            body = Buffer().readFrom(getAuthorizationResponseJson().toString().byteInputStream())
//        })
////// Schedule the response for the document download.
////        server.enqueue(MockResponse().apply {
////            addHeader("Content-Type", "application/pdf")
////            // Serve PDF document data from a local file.
////            body = Buffer().readFrom(File(testDocumentPath).inputStream())
////        })
//
//        val testSubscriber: TestSubscriber<Response<List<MapDto>>> = TestSubscriber()
//        val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(
//            "email",
//            "password_et.text.toString()"
//        )
//        Api.fakeGetMyMaps(token).subscribe(testSubscriber)
//        testSubscriber.assertNoErrors()
//        testSubscriber.assertValueCount(1)
//        val actual: List<MapDto> = testSubscriber.getOnNextEvents().get(0)
//        assertEquals(1, actual.size)
//    }
//
//    private fun getAuthorizationResponseJson(): JsonObject {
//        return JsonObject()
//    }
//}