package map.together.viewholders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_search_result.view.*
import map.together.items.SearchItem

class SearchViewHolder(
    itemView: View
) : BaseViewHolder<SearchItem>(itemView) {

    private var title: TextView = itemView.search_result_text
    private var text: TextView = itemView.search_result_text_additional
    private var result: ConstraintLayout = itemView.search_result_item
    private var item: SearchItem? = null

    override fun bind(item: SearchItem) {
        this.item = item
        title.setText(item.title)
        text.setText(item.text)

    }

    fun setOnItemClickListener(onClick: () -> Unit) {
        result.setOnClickListener {
            onClick()
        }
    }
}