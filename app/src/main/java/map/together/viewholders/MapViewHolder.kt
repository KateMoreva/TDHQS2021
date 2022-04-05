package map.together.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import map.together.R
import map.together.db.entity.MapEntity

class MapViewHolder (
        itemView: View
) : BaseViewHolder<MapEntity>(itemView) {
    var nameTextView: TextView = itemView.findViewById(R.id.map_name)
    var infoTextView: TextView = itemView.findViewById(R.id.map_inf0)
    var iconView: ImageView = itemView.findViewById(R.id.map_icon)
    var deleteButton: FloatingActionButton = itemView.findViewById(R.id.map_delete)
    var openMapBtn: MaterialCardView = itemView.findViewById(R.id.open_map_btn)

    override fun bind(item: MapEntity) {
        nameTextView.text = item.name
        infoTextView.text = item.roleName + ". Участники: " + item.participantsCount
    }

    fun setOnRemoveBtnClickListener(onRemove: (View) -> Unit) {
        deleteButton.setOnClickListener(onRemove)
    }

    fun setOnClickListener(onClick: (View) -> Unit) {
        openMapBtn.setOnClickListener(onClick)
    }
}