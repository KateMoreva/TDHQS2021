package map.together.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.possible_categories_dialog.view.*
import map.together.R
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.utils.recycler.adapters.CategoriesAdapter
import map.together.viewholders.CategoryViewHolder

class CategoryChoosingDialog(
    private var item: CategoryItem,
    private val existing: MutableList<CategoryItem>,
    private val listener: CategoryColorDialog.CategoryDialogListener
) : DialogFragment() {
    var layoutItems: RecyclerView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.possible_categories_dialog, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            layoutItems = view.all_categories_list

            var categoriesList = ItemsList(existing)
            val adapter = CategoriesAdapter(
                holderType = CategoryViewHolder::class,
                layoutId = R.layout.item_category_short,
                dataSource = categoriesList,
                onClick = { category ->
                    item = category
                    listener.onSaveParameterClick(item)
                    dialog.dismiss()
                },
            )

            view.all_categories_list.adapter = adapter
            val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            view.all_categories_list.layoutManager = layoutManager


            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface CategoryDialogListener {
        fun onSaveParameterClick(item: CategoryItem)
    }

}