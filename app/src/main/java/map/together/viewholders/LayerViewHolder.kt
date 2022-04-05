package map.together.viewholders

import android.view.View
import android.widget.EditText
import android.widget.ImageView
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
    private var title: EditText = itemView.layer_title
    private var removeBtn: FloatingActionButton = itemView.remove_layer
    private var item: LayerItem? = null

    override fun bind(item: LayerItem) {
        this.item = item
        title.setText(item.title)
        setLayerVisibility()
        setLayerEditable()
        visibilityCheckbox.isEnabled = !item.disabled
        if (item.disabled) {
            title.setTextColor(itemView.context.getColor(R.color.gray_3))
        } else {
            title.setTextColor(itemView.context.getColor(R.color.gray_1))
        }
    }

    private fun setLayerVisibility() {
        if (item!!.selected) {
            visibilityCheckbox.background?.setTint(itemView.context.getColor(R.color.dusty_blue))
        } else {
            if (item!!.isVisible) {
                visibilityCheckbox.background?.setTint(itemView.context.getColor(R.color.white))
            } else {
                visibilityCheckbox.background?.setTint(itemView.context.getColor(R.color.gray_4))
            }
        }
        if (item!!.isVisible) {
            visibilityImage.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
        } else {
            visibilityImage.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24)
        }
    }

    private fun setLayerEditable() {
        title.isEnabled = item!!.editable
        removeBtn.isEnabled = item!!.editable
        title.isClickable = item!!.editable
        if (item!!.editable) {
            removeBtn.visibility = View.VISIBLE
        } else {
            removeBtn.visibility = View.INVISIBLE
        }
    }

    fun setOnRemoveBtnClickListener(onRemove: (View) -> Unit) {
        removeBtn.setOnClickListener(onRemove)
    }

    fun setOnChangeVisibilityClickListener(onClick: (View) -> Unit) {
        visibilityCheckbox.setOnClickListener{
            onClick.invoke(it)
            setLayerVisibility()
        }
    }
}