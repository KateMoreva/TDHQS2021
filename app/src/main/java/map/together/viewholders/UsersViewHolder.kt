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
    private var item: UserItem? = null
    private var userRole: Spinner = itemView.user_role
    val roles = listOf("Модерация", "Изменение", "Просмотр")

    override fun bind(item: UserItem) {
        this.item = item
        name.text = item.name

        if (item.canDelete)
            remove.visibility = View.VISIBLE
        else
            remove.visibility = View.INVISIBLE

        userRole.isEnabled = item.canEdit

        var index = 0

        when (item.roleName) {
            "Модерация" -> {
                index = 0
                this.item!!.role = 7
            }
            "Изменение" -> {
                index = 1
                this.item!!.role = 3
            }
            "Просмотр" -> {
                index = 2
                this.item!!.role = 1
            }
        }

        val categoryAdapter = ArrayAdapter<String>(itemView.context, R.layout.spinner_item, roles)
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        userRole.adapter = categoryAdapter

        userRole.setSelection(index, true)

    }

    fun setOnRemoveItemClickListener(onRemove: (View) -> Unit) {
        remove.setOnClickListener(onRemove)
    }

    fun setOnChangeRoleListener(onChange: () -> Unit) {
        userRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                this@UsersViewHolder.item!!.roleName = roles[position]
                when (this@UsersViewHolder.item!!.roleName) {
                    "Модерация" -> {
                        this@UsersViewHolder.item!!.role = 7
                    }
                    "Изменение" -> {
                        this@UsersViewHolder.item!!.role = 3
                    }
                    "Просмотр" -> {
                        this@UsersViewHolder.item!!.role = 1
                    }
                }
                onChange.invoke()
            }
        }
    }
}