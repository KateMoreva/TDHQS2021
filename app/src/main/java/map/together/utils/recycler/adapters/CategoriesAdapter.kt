package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.CategoryViewHolder
import kotlin.reflect.KClass

class CategoriesAdapter(
        holderType: KClass<out CategoryViewHolder>,
        @LayoutRes layoutId: Int,
        dataSource: ItemsList<CategoryItem>,
        onClick: (CategoryItem) -> Unit = {},
        private val onEdit: (CategoryItem) -> Unit = {},
        private val onRemove: (CategoryItem) -> Unit = {},
) : BaseListAdapter<CategoryItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<CategoryItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val categoryHolder = (holder as CategoryViewHolder)

        categoryHolder.setOnEditBtnClickListener {
            onEdit.invoke(item)
        }
        categoryHolder.setOnRemoveBtnClickListener {
            onRemove.invoke(item)
        }
    }
}
