package map.together.hello

interface HelloService {
    fun hello(): String
}


// service class with injected helloModel instance
/**
 * Hello Service Impl
 * Will use HelloMessageData data
 */
class HelloServiceImpl(private val helloMessageData: HelloMessageData) : HelloService {

    override fun hello() = "Hey, ${helloMessageData.message}"
}