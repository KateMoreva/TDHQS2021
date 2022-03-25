package map.together.hello

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class HelloApplication : KoinComponent {

    // Inject HelloService
    val helloService: HelloService by inject()

    // display our data
    fun sayHello() = println(helloService.hello())
}

/**
 * run app from here
 */
fun main() {
    startKoin {
        printLogger()
        modules(helloModule)
    }
    HelloApplication().sayHello()
}