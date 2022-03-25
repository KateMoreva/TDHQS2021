package map.together;

import map.together.dto.db.LayerDto
import map.together.dto.db.MapDto
import map.together.dto.db.UserMapDto
import java.util.*

data class MapData(val name: String, val user: UserMapDto, val layer: LayerDto) {
    val mapDto: MapDto = MapDto(1, name, 1, 1, 1, false, true, "", 1, 1)
    val users: List<UserMapDto> = Collections.singletonList(user)
    val layers: List<LayerDto> = Collections.singletonList(layer)
    val demonstrationTimestamp: Long = 1L
}
