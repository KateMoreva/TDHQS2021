package map.together.activities

import android.os.Bundle
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.utils.recycler.adapters.CategoryAdapter
import map.together.viewholders.CategoryViewHolder


class CategoryActivity : BaseFragmentActivity() {
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categories =
            mutableListOf(CategoryItem("1", "не нада", "true"), CategoryItem("2", "ой", "000"))
        val layersList = ItemsList(categories)
        val adapter = CategoryAdapter(
            holderType = CategoryViewHolder::class,
            layoutId = R.layout.item_layer,
            dataSource = layersList,
            onClick = {
                //todo:
            },
            onRemove = {
                // todo: check that user can delete this layer and delete it
            },
        )

    }
}