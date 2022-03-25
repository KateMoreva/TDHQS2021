package map.together

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class MapTest : KoinTest {
    val model by inject<MapData>()
    val service by inject<MapService>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(mapModule)
    }

    @Test
    fun unit_test() {
        val helloApp = MapApplication()
        helloApp.getMap()

        Assert.assertEquals(service, helloApp.mapService)
        Assert.assertEquals(model.name, service.getMap()[0].name)
    }
}