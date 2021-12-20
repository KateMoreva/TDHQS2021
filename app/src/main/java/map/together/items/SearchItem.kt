package map.together.items

import com.yandex.mapkit.geometry.Point
import map.together.items.interfaces.WithId

data class SearchItem(
    override val id: String,
    var title: String,
    var text: String,
    var coord: Point
) : WithId