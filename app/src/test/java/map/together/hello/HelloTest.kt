package map.together.hello

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class HelloTest : KoinTest {
    val model by inject<HelloMessageData>()
    val service by inject<HelloService>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(helloModule)
    }

    @Test
    fun unit_test() {
        val helloApp = HelloApplication()
        helloApp.sayHello()

        assertEquals(service, helloApp.helloService)
        assertEquals("Hey, ${model.message}", service.hello())
    }
}