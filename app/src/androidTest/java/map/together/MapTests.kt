package map.together

import androidx.test.ext.junit.rules.ActivityScenarioRule
import map.together.activities.auth.LoginActivity
import map.together.api.Api
import map.together.api.ApiUtils
import map.together.dto.db.MapDto
import map.together.dto.db.PlaceDto
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
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)
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
        val maps1 = getMaps()
        Assert.assertNotNull(maps1)

        mapsListScreen
            .chooseMapByIndex(1)
    }

    @Test
    fun selectWinterPalace() {
        val mapsSize = getMaps()!!.size
        Assert.assertTrue(mapsSize > 1)

        val placeAddress = mapsListScreen
            .chooseMapByIndex(mapsSize - 1)
            .zoomIn()
            .clickOnMap()
            .getAddress()
        Assert.assertTrue(placeAddress == "Дворцовая площадь, 2" || placeAddress == "Palace Square, 2")
    }

    @Test
    fun savePlace() {
        val places = getPlaces()
        Assert.assertNotNull(places)
        val mapsSize = getMaps()!!.size
        Assert.assertTrue(mapsSize > 1)

        mapsListScreen
            .createMap()
            .chooseMapByIndex(mapsSize - 1)
            .zoomIn()
            .clickOnMap()
            .clickSavePlace()

        val places2 = getPlaces()
        Assert.assertNotNull(places2)
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

        val layersCount = getLayersCount(mp.id)
        Assert.assertEquals(1, layersCount)
        mapsListScreen
                .chooseMapByIndex(maps2.size - 1)
                .openLayers()
                .createLayer()
                .checkLayerCreated(1)
        Assert.assertEquals(getLayersCount(mp.id), 2)
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
        }, { error ->
            Logger.e(error)
            latch.countDown()
        })
        latch.await()
        disposable.dispose()
        return mapsListFromServer
    }

    private fun getPlaces(): List<PlaceDto>? {
        var placesListFromServer: List<PlaceDto>? = null
        val latch = CountDownLatch(1)
        val token = ApiUtils.encodeEmailAndPasswordToAuthorizationHeader(email, password)
        val disposable = Api.getMyPlaces(token, "").subscribe({ response ->
            val places = response.body()!!
            placesListFromServer = places
            latch.countDown()
        }, { error ->
            Logger.e(error)
            latch.countDown()
        })
        latch.await()
        disposable.dispose()
        return placesListFromServer
    }
}