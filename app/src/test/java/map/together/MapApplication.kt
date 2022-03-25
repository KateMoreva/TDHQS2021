package map.together

import io.ktor.utils.io.*
import map.together.dto.db.LayerDto
import map.together.dto.db.PlaceDto
import map.together.dto.db.UserMapDto
import map.together.hello.HelloApplication
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import java.util.*

class MapApplication : KoinComponent {
    private val place: PlaceDto = PlaceDto(1, "", 1, "", "", true, false, 1, 1, "", 1)
    private val userMapDto: UserMapDto = UserMapDto(1, "", "", null, "", 1, true, false)
    private val layerDto: LayerDto =
        LayerDto(1, "", 1, true, false, Collections.singletonList(place), 1)

    // Inject HelloService
    val mapService: MapService by inject() { parametersOf("a", userMapDto, layerDto) }

    // display our data
    fun getMap() = println(mapService.getMap())
}

/**
 * run app from here
 */
fun main() {
    startKoin {
        printLogger()
        modules(mapModule)
    }
    MapApplication().getMap()
}