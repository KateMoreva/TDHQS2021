package map.together.utils.recycler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import map.together.items.ItemsList
import map.together.viewholders.BaseViewHolder
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class BaseListAdapter<Item>(
        private val holderType: KClass<out BaseViewHolder<Item>>,
        @LayoutRes private val layoutId: Int,
        private val dataSource: ItemsList<Item>,
        private val onClick: (Item) -> Unit = {}
) : RecyclerView.Adapter<BaseViewHolder<Item>>() {

    internal var data = listOf<Item>()

    private var onAddDataSourceSubscription: Disposable? = null
    private var onRemoveDataSourceSubscription: Disposable? = null
    private var onUpdateDataSourceSubscription: Disposable? = null
    private var onRangeUpdateDataSourceSubscription: Disposable? = null
    private var onSetDataSourceSubscription: Disposable? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        data = dataSource.items
        onAddDataSourceSubscription = dataSource.addingSubject().subscribe {
            notifyItemInserted(it.second)
        }
        onRemoveDataSourceSubscription = dataSource.removingSubject().subscribe {
            notifyItemRemoved(it)
        }
        onUpdateDataSourceSubscription = dataSource.updatingSubject().subscribe {
            notifyItemChanged(it)
        }

        onRangeUpdateDataSourceSubscription = dataSource.updatingRangeSubject().subscribe {
            notifyItemRangeChanged(it.first, it.second)
        }

        onSetDataSourceSubscription = dataSource.replacingSubject().subscribe {
            println("data set changed: " + it.size)
            data = it
            notifyDataSetChanged()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        onAddDataSourceSubscription?.dispose()
        onRemoveDataSourceSubscription?.dispose()
        onUpdateDataSourceSubscription?.dispose()
        onSetDataSourceSubscription?.dispose()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(
                layoutId,
                parent,
                false
            )
        return holderType.primaryConstructor?.call(view)
            ?: throw NoSuchMethodException("No constructor with parameter of type View")
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onClick.invoke(item) }
    }
}
