package map.together.items

import map.together.items.interfaces.WithId

data class UserItem(
    override val id: String,
    var name: String,
    var email: String,
    var roleName: String,
    var canEdit: Boolean,
    var canDelete: Boolean,
    var role: Long = 0
) : WithId
