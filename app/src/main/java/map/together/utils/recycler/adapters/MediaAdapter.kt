package map.together.utils.recycler.adapters

import android.view.View
import androidx.annotation.LayoutRes
import map.together.items.ItemsList
import map.together.items.MediaItem
import map.together.viewholders.BaseViewHolder
import map.together.viewholders.MediaViewHolder
import kotlin.reflect.KClass

class MediaAdapter(
    holderType: KClass<out MediaViewHolder>,
    @LayoutRes layoutId: Int,
    dataSource: ItemsList<MediaItem>,
    onClick: (MediaItem) -> Unit = {},
    private val onImageClick: (MediaItem, View) -> Unit = { _, _ -> }
) : BaseListAdapter<MediaItem>(holderType, layoutId, dataSource, onClick) {

    override fun onBindViewHolder(holder: BaseViewHolder<MediaItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = data[position]
        val imageHolder = (holder as MediaViewHolder)

        imageHolder.setOnClickListener { view ->
            onImageClick.invoke(item, view)
        }
    }
}
