package map.together.viewholders

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.item_user.view.*
import map.together.R
import map.together.fragments.BaseFragment
import map.together.items.UserItem

class UsersViewHolder(
    private val fragment: BaseFragment,
    itemView: View
) : BaseViewHolder<UserItem>(itemView) {

    private var name: TextView = itemView.user_name_text

    //    private var roles = UserRoles
    private var rolesList: List<String> = mutableListOf("FFF", "ggg")
    private var role: Spinner = itemView.user_role
    private var item: UserItem? = null
    private var currentLevel: Int = 0

    override fun bind(item: UserItem) {
        this.item = item
        name.setText(item.name)

        val arrayAdapter = ArrayAdapter(fragment.requireContext(), R.layout.spinner_item, rolesList)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        role.adapter = arrayAdapter
        role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentLevel = position + 1
            }
        }

        this.role.setSelection(0)
    }

    fun setOnItemClickListener(onClick: (View) -> Unit) {
        role.setOnClickListener {
            onClick(it)
        }
    }
}