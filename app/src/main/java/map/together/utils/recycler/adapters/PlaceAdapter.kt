package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.ItemsList
import map.together.items.PlaceItem
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.PlaceViewHolder
import kotlin.reflect.KClass

class PlaceAdapter(
    holderType: KClass<out PlaceViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<PlaceItem>,
    onClick: (PlaceItem) -> Unit = {},
) : BaseListAdapter<PlaceItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<PlaceItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as PlaceViewHolder)
    }
}