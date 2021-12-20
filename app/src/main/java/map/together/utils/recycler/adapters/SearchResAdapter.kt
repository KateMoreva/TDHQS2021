package map.together.utils.recycler.adapters

import androidx.annotation.LayoutRes
import map.together.items.ItemsList
import map.together.items.SearchItem
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.SearchViewHolder
import kotlin.reflect.KClass

class SearchResAdapter(
    holderType: KClass<out SearchViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<SearchItem>,
    private val onClick: (SearchItem) -> Unit = {},
) : BaseListAdapter<SearchItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<SearchItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val layerHolder = (holder as SearchViewHolder)

        layerHolder.setOnItemClickListener {
            onClick.invoke(item)
        }
    }
}