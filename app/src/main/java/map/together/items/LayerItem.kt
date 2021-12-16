package map.together.items

import map.together.items.interfaces.WithId

data class LayerItem(
        override val id: String,
        var title: String,
        var isVisible: Boolean,
        var ownerId: Long?,
        var editable: Boolean = true,
        var disabled: Boolean = false,
        var selected: Boolean = false,
): WithId