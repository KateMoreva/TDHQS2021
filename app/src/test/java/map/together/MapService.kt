package map.together

import map.together.dto.db.LayerDto
import map.together.dto.db.MapDto
import map.together.dto.db.MapInfoDto
import map.together.dto.db.UserMapDto
import java.util.*

interface MapService {
    fun getMap(): MutableList<MapDto>
    fun getMapInfo(): MapInfoDto
}

class MapServiceImpl(private val map: MapData) : MapService {

    override fun getMap(): MutableList<MapDto> = Collections.singletonList(map.mapDto)
    override fun getMapInfo(): MapInfoDto =
        MapInfoDto(map.mapDto, map.users, map.layers, null, map.demonstrationTimestamp)
}