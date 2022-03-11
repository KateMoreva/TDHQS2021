package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.api.Api
import map.together.api.ApiUtils
import map.together.dto.db.MapDto
import map.together.activities.auth.LoginActivity
import map.together.mockActivities.auth.FakeLoginActivity
import map.together.screens.LoginScreen
import map.together.screens.MapsLibraryScreen
import map.together.utils.logger.Logger
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class MapTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(FakeLoginActivity::class.java)
    private val loginScreen = LoginScreen()
    private var mapsListScreen = MapsLibraryScreen()

    private val email = "test@test.test"
    private val password = "qwerty"

    @Before
    fun login() {
        mapsListScreen = loginScreen
            .typeLogin(email)
            .typePassword(password)
            .pressConfirmButton()
    }

    @Test
    fun openMap() {
        loginScreen
            .pressConfirmButton()
            .chooseFirstMap()
            .click()

    }

    @Test
    fun createLayer() {
        val maps1 = getMaps()
        Assert.assertNotNull(maps1)

        mapsListScreen.createMap()

        val maps2 = getMaps()
        Assert.assertNotNull(maps2)

        val filteredMaps = maps2!!.filter { mp2 -> maps1!!.find { mp1 -> mp1.id == mp2.id } == null }
        Assert.assertNotNull(filteredMaps)
        Assert.assertEquals(1, filteredMaps.size)
        val mp = filteredMaps[0]

        val expectedIndex = getLayersCount(mp.id)
        Assert.assertEquals(0, expectedIndex)
        Assert.assertNotNull(expectedIndex)
        mapsListScreen
                .chooseMapByIndex(maps2.size - 1)
                .openLayers()
                .createLayer()
                .checkLayerCreated(expectedIndex!!)
        Assert.assertEquals(getLayersCount(mp.id), expectedIndex + 1)
    }

    private fun getLayersCount(mapId: Long): Int? {
        var layersCountFromServer: Int? = null
        val latch = CountDownLatch(1)
        val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(email, password)
        val disposable = Api.getMapInfo(token, mapId).subscribe ({ response ->
            val mapInfo = response.body()!!
            layersCountFromServer = mapInfo.layers.size
            latch.countDown()
        }, {error ->
            Logger.e(error)
            latch.countDown()
        })
        latch.await()
        disposable.dispose()
        return layersCountFromServer
    }

    private fun getMaps(): List<MapDto>? {
        var mapsListFromServer: List<MapDto>? = null
        val latch = CountDownLatch(1)
        val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(email, password)
        val disposable = Api.getMyMaps(token, "").subscribe ({ response ->
            val maps = response.body()!!
            mapsListFromServer = maps
            latch.countDown()
        }, {error ->
            Logger.e(error)
            latch.countDown()
        })
        latch.await()
        disposable.dispose()
        return mapsListFromServer
    }
}