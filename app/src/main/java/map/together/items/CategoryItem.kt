package map.together.items

import map.together.R
import map.together.items.interfaces.WithId

data class CategoryItem(
    override val id: String,
    var name: String,
    var colorRecourse: Int,
    var ownerId: Long? = null,
): WithId
