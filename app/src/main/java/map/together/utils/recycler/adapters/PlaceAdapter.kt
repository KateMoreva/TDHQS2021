package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.R
import map.together.items.ItemsList
import map.together.items.PlaceItem
import map.together.utils.showSimpleMaterialDialog
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.PlaceViewHolder
import kotlin.reflect.KClass

class PlaceAdapter(
    holderType: KClass<out PlaceViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<PlaceItem>,
    onClick: (PlaceItem) -> Unit = {},
    private var onRemove: (PlaceItem) -> Unit = {}
) : BaseListAdapter<PlaceItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<PlaceItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as PlaceViewHolder)
        layerHolder.setOnRemoveBtnClickListener {
            showSimpleMaterialDialog(
                context = holder.itemView.context,
                title = holder.itemView.context.getString(R.string.confirmation),
                message = holder.itemView.context.getString(R.string.delete_category_confirmation),
                positiveButtonText = holder.itemView.context.getString(R.string.yes),
                negativeButtonText = holder.itemView.context.getString(R.string.cancel),
                onPositiveClicked = { onRemove.invoke(item) },
            )
        }

    }
}