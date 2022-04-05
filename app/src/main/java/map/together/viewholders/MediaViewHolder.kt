package map.together.viewholders

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_media.view.*
import map.together.R
import map.together.items.MediaItem

class MediaViewHolder(
    itemView: View
) : BaseViewHolder<MediaItem>(itemView) {

    private val mediaView: ImageView = itemView.media_view

    override fun bind(item: MediaItem) {
        val requestBuilder = Glide
            .with(mediaView)
            .asDrawable()
            .load(item.uri)
        when (item.displayMode) {
            MediaItem.DisplayMode.FIT_CENTER -> requestBuilder.fitCenter()
            MediaItem.DisplayMode.CENTER_CROP -> requestBuilder.centerCrop()
        }
            .placeholder(R.drawable.ic_access_time_black_24dp)
            .error(R.drawable.ic_outline_error_outline_24)
            .into(mediaView)
    }

    fun setOnClickListener(onCLick: (View) -> Unit) {
        mediaView.setOnClickListener(onCLick)
    }
}