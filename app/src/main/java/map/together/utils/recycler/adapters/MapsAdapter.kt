package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.db.entity.MapEntity
import map.together.items.ItemsList
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.MapViewHolder
import kotlin.reflect.KClass

class MapsAdapter(
        holderType: KClass<out MapViewHolder>,
        @LayoutRes layoutId: Int,
        dataSource: ItemsList<MapEntity>,
        private val onClick: (MapEntity) -> Unit = {},
        private val onRemove: (MapEntity) -> Unit = {},
) : BaseListAdapter<MapEntity>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<MapEntity>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val mapHolder = (holder as MapViewHolder)

        mapHolder.setOnRemoveBtnClickListener {
            onRemove.invoke(item)
        }
        mapHolder.setOnClickListener {
            onClick.invoke(item)
        }
    }
}
