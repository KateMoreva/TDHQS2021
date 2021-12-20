package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.ItemsList
import map.together.items.UserItem
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.UsersViewHolder
import kotlin.reflect.KClass

class UsersAdapter(
    holderType: KClass<out UsersViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<UserItem>,
    private val onClick: (UserItem) -> Unit = {},
) : BaseListAdapter<UserItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<UserItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as UsersViewHolder)
//
//        layerHolder.setOnItemClickListener {
//            onClick.invoke(item)
//        }
    }
}