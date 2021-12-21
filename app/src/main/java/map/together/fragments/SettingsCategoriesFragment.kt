package map.together.fragments


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_setting_categories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.db.entity.CategoryEntity
import map.together.db.entity.PlaceEntity
import map.together.fragments.dialogs.CategoryColorDialog
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.utils.recycler.adapters.CategoriesAdapter
import map.together.viewholders.CategoryViewHolder

class SettingsCategoriesFragment : BaseFragment(), CategoryColorDialog.CategoryDialogListener {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_setting_categories
    val categories = mutableListOf(
        CategoryItem("1", "Категория 1", R.color.red),
        CategoryItem("2", "Категория 2", R.color.green),
        CategoryItem("3", "Категория 3", R.color.blue),
    )
    val categoriesList = ItemsList(categories)

//    fun getCategories(
//        layerId: Long, actionsAfter: (
//            List<CategoryEntity>
//        ) -> Unit
//    ) {
//        GlobalScope.launch(Dispatchers.IO) {
//            database?.let {
//                val placesDao = it.placeDao().getByLayerId(layerId)
//                withContext(Dispatchers.Main) {
//                    actionsAfter.invoke(placesDao)
//                }
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: load categories from the server or get them from the local DB

        val adapter = CategoriesAdapter(
            holderType = CategoryViewHolder::class,
            layoutId = R.layout.item_category,
            dataSource = categoriesList,
            onClick = {
                CategoryColorDialog(it, categories, this).show(
                    requireActivity().supportFragmentManager,
                    "CategoryColorDialog"
                )

            },
                onEdit = { category ->
                    print("Category $category onChange")
                    CategoryColorDialog(
                        category, categories,
                        this
                    ).show(requireActivity().supportFragmentManager, "CategoryColorDialog")
                },
                onRemove = { category ->
                    print("Category $category deleted")
                    categoriesList.remove(category)
                    checkCategoriesList(categoriesList)
                },
        )
        categories_list.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        categories_list.layoutManager = layoutManager
        checkCategoriesList(categoriesList)

        add_category_btn.setOnClickListener {
            val newCategory = CategoryItem(
                categoriesList.size().toString(),
                "Новая категория " + categoriesList.size(), R.color.grey
            )
            CategoryColorDialog(newCategory, categories, this).show(
                requireActivity().supportFragmentManager,
                "CategoryColorDialog"
            )
            categoriesList.addLast(newCategory)
            categories_list.smoothScrollToPosition(categoriesList.size() - 1)
            checkCategoriesList(categoriesList)
        }
    }

    private fun checkCategoriesList(categoriesList: ItemsList<CategoryItem>) {
        if (categoriesList.items.isEmpty()) {
            category_hint_arrow.visibility = View.VISIBLE
            category_hint_text.visibility = View.VISIBLE
        } else {
            category_hint_arrow.visibility = View.INVISIBLE
            category_hint_text.visibility = View.INVISIBLE
        }
    }

    override fun getAppbarTitle(): Int = R.string.change_categories

    override fun onSaveParameterClick(item: CategoryItem) {
        println(item.toString())
        categoriesList.let {
            val indexOf = it.indexOf(item)
            if (indexOf == -1) {
                it.addLast(item)
            } else {
                it.update(indexOf, item)
            }
        }
    }

}