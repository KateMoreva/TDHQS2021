package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.CategoryViewHolder
import kotlin.reflect.KClass

class CategoryAdapter(
    holderType: KClass<out CategoryViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<CategoryItem>,
    private val onClick: (CategoryItem) -> Unit = {},
    private val onRemove: (CategoryItem) -> Unit = {},
) : BaseListAdapter<CategoryItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<CategoryItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as CategoryViewHolder)

    }
}