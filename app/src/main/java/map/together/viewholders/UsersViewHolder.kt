package map.together.viewholders

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_user.view.*
import map.together.R
import map.together.items.UserItem

class UsersViewHolder(
    itemView: View
) : BaseViewHolder<UserItem>(itemView) {

    private var name: TextView = itemView.user_name_text
    private var remove: FloatingActionButton = itemView.remove_user

    //    private var roles = UserRoles
    private var rolesList: List<String> = mutableListOf("FFF", "ggg")
//    private var role: Spinner = itemView.user_role
    private var item: UserItem? = null
    private var currentLevel: Int = 0

    override fun bind(item: UserItem) {
        this.item = item
        name.setText(item.name)

//        val arrayAdapter = ArrayAdapter(itemView.context, R.layout.spinner_item, rolesList)
//        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//        role.adapter = arrayAdapter
//        role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                currentLevel = position + 1
//            }
//        }
//
//        this.role.setSelection(0)
    }
//
//    fun setOnItemClickListener(onClick: (View) -> Unit) {
//        role.setOnClickListener {
//            onClick(it)
//        }
//    }

    fun setOnRemoveItemClickListener(onRemove: (View) -> Unit) {
        remove.setOnClickListener(onRemove)
    }
}