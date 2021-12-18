package map.together.viewholders

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.category_item.view.*
import map.together.R
import map.together.items.CategoryItem

class CategoryViewHolder(
    itemView: View
) : BaseViewHolder<CategoryItem>(itemView) {
    private var title: TextView = itemView.category_item_name_id
    private var item: CategoryItem? = null

    override fun bind(item: CategoryItem) {
        title.text = item.title
        this.item = item
    }

}