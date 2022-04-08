package map.together.fragments


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_setting_categories.*
import map.together.R
import map.together.activities.BaseFragmentActivity
import map.together.api.Api
import map.together.fragments.dialogs.CategoryColorDialog
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.repository.CurrentUserRepository
import map.together.utils.ResponseActions
import map.together.utils.recycler.adapters.CategoriesAdapter
import map.together.viewholders.CategoryViewHolder
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import javax.net.ssl.HttpsURLConnection

class SettingsCategoriesFragment : BaseFragment(), CategoryColorDialog.CategoryDialogListener {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_setting_categories
    var categories = mutableListOf<CategoryItem>()
    val categoriesList = ItemsList(categories)

    var token: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: load categories from the server or get them from the local DB
        token = CurrentUserRepository.getCurrentUserToken(requireContext())!!

        (activity as BaseFragmentActivity).taskContainer.add(
                Api.getMyCategories(token!!).subscribe(
                        { ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HTTP_BAD_REQUEST) { categoriesDtos ->
                            categories = categoriesDtos!!.map {
                                dto -> CategoryItem(dto.id.toString(), dto.name, dto.color, dto.ownerId)
                            }.toMutableList()
                            categoriesList.setData(categories)
                            checkCategoriesList(categoriesList)
                        } },
                        { ResponseActions.onFail(it, requireContext()) }
                )
        )

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

        add_category_btn.setOnClickListener {
            val categoryName = "Новая категория " + categoriesList.size()
            (activity as BaseFragmentActivity).taskContainer.add(
                    Api.createCategory(token!!, categoryName).subscribe(
                            { ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HTTP_BAD_REQUEST) { categoryDto ->
                                val newCategory = CategoryItem(
                                        categoryDto!!.id.toString(),
                                        categoryDto.name, categoryDto.color
                                )
                                CategoryColorDialog(newCategory, categories, this).show(
                                        requireActivity().supportFragmentManager,
                                        "CategoryColorDialog"
                                )
                                categoriesList.addLast(newCategory)
                                categories_list.smoothScrollToPosition(categoriesList.size() - 1)
                                checkCategoriesList(categoriesList)
                            } },
                            { ResponseActions.onFail(it, requireContext()) }
                    )
            )
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
        (activity as BaseFragmentActivity).taskContainer.add(
                Api.changeCategory(token!!, item.id.toLong(), item.name, item.colorRecourse).subscribe(
                        { ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HTTP_BAD_REQUEST) { categoriesDtos ->
                            println(item.toString())
                            categoriesList.let {
                                val indexOf = it.indexOf(item)
                                if (indexOf == -1) {
                                    it.addLast(item)
                                } else {
                                    it.update(indexOf, item)
                                }
                            }
                        } },
                        { ResponseActions.onFail(it, requireContext()) }
                )
        )
    }

}