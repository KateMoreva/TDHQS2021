package map.together.items

import map.together.items.interfaces.WithId

data class UserItem(
    override val id: String,
    var name: String,
//    var role: UserRole
) : WithId
