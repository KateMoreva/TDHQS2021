package map.together

import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.mockActivities.auth.FakeLoginActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.internal.DoNotInstrument


@InternalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml")
@DoNotInstrument
class IntegrationBaseTest {
    private var activity: FakeLoginActivity? = null

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(FakeLoginActivity::class.java)
            .create().get()
    }

    @Test
    @Throws(Exception::class)
    fun checkActivityNotNull() {
        assertNotNull(activity)
    }

}
//    var component: IntegrationTestComponent? = null
//    var testUtils: TestUtils? = null
//
//    @Inject
//    protected var mockWebServer: MockWebServer? = null
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        component = App.getComponent() as IntegrationTestComponent
//        testUtils = TestUtils()
//        component.inject(this)
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun tearDown() {
//        mockWebServer!!.shutdown()
//    }
//
//    protected fun setErrorAnswerWebServer() {
//        mockWebServer!!.setDispatcher(object : Dispatcher() {
//            @Throws(InterruptedException::class)
//            override fun dispatch(request: RecordedRequest?): MockResponse {
//                return MockResponse().setResponseCode(500)
//            }
//        })
//    }
//
//    protected fun setCustomAnswer(enableBranches: Boolean, enableContributors: Boolean) {
//        mockWebServer!!.setDispatcher(object : Dispatcher() {
//            @Throws(InterruptedException::class)
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                if (request.path == "/users/" + TestConst.TEST_OWNER.toString() + "/repos") {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/repos.json"))
//                } else if (request.path == "/repos/" + TestConst.TEST_OWNER.toString() + "/" + TestConst.TEST_REPO.toString() + "/branches" && enableBranches) {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/branches.json"))
//                } else if (request.path == "/repos/" + TestConst.TEST_OWNER.toString() + "/" + TestConst.TEST_REPO.toString() + "/contributors" && enableContributors) {
//                    return MockResponse().setResponseCode(200)
//                        .setBody(testUtils.readString("json/contributors.json"))
//                }
//                return MockResponse().setResponseCode(404)
//            }
//        })
//    }
//}