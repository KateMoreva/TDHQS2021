package map.together.hello

import org.koin.dsl.module

// Koin module
val helloModule = module {
    single { HelloMessageData() }
    single<HelloService> { HelloServiceImpl(get()) }
}

/*
val helloModule = module {
    single<HelloMessageData>()
    single<HelloServiceImpl>() bind HelloService::class
}
 */
