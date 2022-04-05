package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.ItemsList
import map.together.items.LayerItem
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.LayerViewHolder
import kotlin.reflect.KClass

class LayersAdapter(
        holderType: KClass<out LayerViewHolder>,
        @LayoutRes layoutId: Int,
        dataSource: ItemsList<LayerItem>,
        private val onClick: (LayerItem) -> Unit = {},
        private val onRemove: (LayerItem) -> Unit = {},
        private val onChangeCommonLayer: (LayerItem) -> Unit = {},
) : BaseListAdapter<LayerItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<LayerItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as LayerViewHolder)

        layerHolder.setOnRemoveBtnClickListener {
            onRemove.invoke(item)
        }
        layerHolder.setOnChangeVisibilityClickListener {
            onClick.invoke(item)
            if (item.ownerId == 0L) {
                onChangeCommonLayer(item)
            }
        }
    }
}
