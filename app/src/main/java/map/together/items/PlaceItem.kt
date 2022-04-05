package map.together.items

import map.together.items.interfaces.WithId

data class PlaceItem(
    override val id: String,
    val name: String,
    val address: String,
    val layer_name: String,
    val categoryResource: Int
) : WithId