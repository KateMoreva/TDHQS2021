package map.together

import org.koin.dsl.module

val mapModule = module {
    single { params -> MapData(params.get(), params.get(), params.get()) }
    single<MapService> { MapServiceImpl(get()) }
}
