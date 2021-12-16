package map.together.items

import map.together.items.interfaces.WithId

data class PlaceItem(
    override val id: String,
    var name: String,
    var isVisible: Boolean
) : WithId