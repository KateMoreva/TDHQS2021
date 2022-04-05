package map.together.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.tag_list_item.view.*
import map.together.items.PlaceItem

class PlaceViewHolder(
    itemView: View
) : BaseViewHolder<PlaceItem>(itemView) {

    private var nameTextView: TextView = itemView.tag_name
    private var addressTextView: TextView = itemView.tag_address
    private var layerTextView: TextView = itemView.tag_layer
    private var iconView: ImageView = itemView.tag_icon
    private var removeDtn: Button = itemView.tag_delete

    private var item: PlaceItem? = null

    override fun bind(item: PlaceItem) {
        this.item = item
        nameTextView.setText(item.name)
        addressTextView.setText(item.address)
        layerTextView.setText(item.layer_name)
        item.categoryResource.let { color ->
            iconView.setColorFilter(
                ContextCompat.getColor(itemView.context, color),
                android.graphics.PorterDuff.Mode.SRC_IN
            );

        }
    }


    fun setOnRemoveBtnClickListener(onRemove: (View) -> Unit) {
        removeDtn.setOnClickListener(onRemove)
    }

}