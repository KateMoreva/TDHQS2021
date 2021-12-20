package map.together.utils.recycler.adapters

import android.app.ProgressDialog.show
import androidx.annotation.LayoutRes
import map.together.R
import map.together.fragments.dialogs.CategoryColorDialog
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.utils.showSimpleMaterialDialog
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.CategoryViewHolder
import kotlin.reflect.KClass

class CategoriesAdapter(
    holderType: KClass<out CategoryViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<CategoryItem>,
    private val onClick: (CategoryItem) -> Unit = {},
    private val onEdit: (CategoryItem) -> Unit = {},
    private val onRemove: (CategoryItem) -> Unit = {},
) : BaseListAdapter<CategoryItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<CategoryItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val categoryHolder = (holder as CategoryViewHolder)
        categoryHolder.setOnEditColorListener {
            onClick.invoke(item)
        }

        categoryHolder.setOnEditBtnClickListener {
            onEdit.invoke(item)
        }
        categoryHolder.setOnRemoveBtnClickListener {
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
