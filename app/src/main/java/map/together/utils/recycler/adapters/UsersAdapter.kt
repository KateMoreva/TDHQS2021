package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.R
import map.together.items.ItemsList
import map.together.items.UserItem
import map.together.utils.showSimpleMaterialDialog
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.UsersViewHolder
import kotlin.reflect.KClass

class UsersAdapter(
    holderType: KClass<out UsersViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<UserItem>,
    private val onClick: (UserItem) -> Unit = {},
    private val onChangeRole: (UserItem) -> Unit = {},
    private val onRemove: (UserItem) -> Unit = {}
) : BaseListAdapter<UserItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<UserItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as UsersViewHolder)

        layerHolder.setOnChangeRoleListener {
            onChangeRole.invoke(item)
        }

        layerHolder.setOnRemoveItemClickListener {
            showSimpleMaterialDialog(
                context = holder.itemView.context,
                title = holder.itemView.context.getString(R.string.confirmation),
                message = holder.itemView.context.getString(R.string.delete_user_confirmation),
                positiveButtonText = holder.itemView.context.getString(R.string.yes),
                negativeButtonText = holder.itemView.context.getString(R.string.cancel),
                onPositiveClicked = { onRemove.invoke(item) },
            )
        }
    }
}