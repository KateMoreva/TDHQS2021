package map.together.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_layer.view.*
import map.together.R
import map.together.items.LayerItem

class LayerViewHolder (
        itemView: View
) : BaseViewHolder<LayerItem>(itemView) {
    private var visibilityImage: ImageView = itemView.visibility_img
    private var visibilityCheckbox: MaterialCardView = itemView.layer_background_btn
    private var title: TextView = itemView.layer_title
    private var removeBtn: FloatingActionButton = itemView.remove_layer
    private var item: LayerItem? = null

    override fun bind(item: LayerItem) {
        title.text = item.title
        setLayerVisibility(item.isVisible)
        this.item = item
    }

    private fun setLayerVisibility(checked: Boolean) {
        if (checked) {
            visibilityCheckbox.background?.setTint(itemView.context.getColor(R.color.dusty_blue))
            visibilityImage.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)

        } else {
            visibilityCheckbox.background?.setTint(itemView.context.getColor(R.color.white))
            visibilityImage.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24)
        }
    }

    fun setOnRemoveBtnClickListener(onRemove: (View) -> Unit) {
        removeBtn.setOnClickListener(onRemove)
    }

    fun setOnChangeVisibilityClickListener(onClick: (View) -> Unit) {
        visibilityCheckbox.setOnClickListener{
            onClick.invoke(it)
            setLayerVisibility(item!!.isVisible)
        }
    }
}