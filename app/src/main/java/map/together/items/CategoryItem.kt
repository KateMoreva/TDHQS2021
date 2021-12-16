package map.together.items

import android.graphics.Color
import map.together.items.interfaces.WithId

data class CategoryItem(
    override val id: String,
    var title: String,
    var color: String
) : WithId