package map.together.items

import map.together.items.interfaces.WithId

data class CategoryItem(
        override val id: String,
        var name: String,
        var colorRecourse: Int? = null,
        var ownerId: Long? = null,
): WithId
