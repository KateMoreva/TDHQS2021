package map.together.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_category.view.*
import map.together.fragments.dialogs.CategoryColorDialog.Companion.COLORS_ARRAY
import map.together.items.CategoryItem

class CategoryViewHolder (
        itemView: View
) : BaseViewHolder<CategoryItem>(itemView) {
    private var tagImage: ImageView = itemView.category_colorized_img
    private var title: TextView = itemView.category_title
    private var editBtn: FloatingActionButton = itemView.edit_category
    private var removeBtn: FloatingActionButton = itemView.remove_category
    private var item: CategoryItem? = null

    override fun bind(item: CategoryItem) {
        this.item = item
        item.colorRecourse.let { color ->
            tagImage.setColorFilter(
                ContextCompat.getColor(itemView.context, COLORS_ARRAY[color]),
                android.graphics.PorterDuff.Mode.SRC_IN
            );

        }
        title.text = item.name
    }

    fun setOnEditColorListener(onClick: (View) -> Unit) {
        tagImage.setOnClickListener(onClick)
    }

    fun setOnEditBtnClickListener(onEdit: (View) -> Unit) {
        editBtn.setOnClickListener(onEdit)
    }

    fun setOnRemoveBtnClickListener(onRemove: (View) -> Unit) {
        removeBtn.setOnClickListener(onRemove)
    }
}
