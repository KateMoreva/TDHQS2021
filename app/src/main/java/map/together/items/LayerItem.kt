package map.together.items

import map.together.items.interfaces.WithId

data class LayerItem(
        override val id: String,
        var title: String,
        var isVisible: Boolean
): WithId