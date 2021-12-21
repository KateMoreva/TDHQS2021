package map.together.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.fragment_tags_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import map.together.R
import map.together.db.AppDatabase
import map.together.db.entity.MapEntity
import map.together.repository.CurrentUserRepository


class TagsListActivity : BaseActivity() {
    //val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
    var tagsAdapter = TagsAdapter()

    data class TagInfo(
        val id: Long, val name: String, val address: String, val layer_name: String,
        val ownerId: Long
    )

    class TagsAdapter() :
        RecyclerView.Adapter<TagsAdapter.TagsViewHolder>() {
        private val tags = ArrayList<TagInfo>()

        fun updateTags(database: AppDatabase) {
            val tags_ = database.placeDao().getByMapId(MapActivity.get_last_map())
            tags.clear()
            tags_.forEach({
                var layer_name = ""
                try {
                    layer_name = database.layerDao().getById(
                        database
                            .placeLayerDao().getByPlaceId(it.id)[0].layerId
                    ).name
                } catch (ex: Exception) {
                }
                tags.add(TagInfo(it.id, it.name, "", layer_name, it.ownerId))
            })
        }

        class TagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameTextView: TextView
            var addressTextView: TextView
            var layerTextView: TextView
            var iconView: SimpleDraweeView
            var deleteButton: Button

            init {
                nameTextView = itemView.findViewById(R.id.tag_name)
                addressTextView = itemView.findViewById(R.id.tag_address)
                layerTextView = itemView.findViewById(R.id.tag_layer)
                iconView = itemView.findViewById(R.id.tag_icon)
                deleteButton = itemView.findViewById(R.id.tag_delete)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tag_list_item, parent, false)
            return TagsViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: TagsViewHolder, position: Int) {
            val tag = tags[position]
            holder.nameTextView.text = tag.name
            // val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
            // searchManager.submit(Point(tag.latitude.toDouble(),tag.longitude.toDouble()), 1,
            //     SearchOptions().setSearchTypes(SearchType.GEO.value), )
            holder.addressTextView.text = ""
            holder.layerTextView.text = tag.layer_name
            if (tag.ownerId == CurrentUserRepository.currentUser.value?.id ?: 0) {
                holder.deleteButton.visibility = View.VISIBLE
                holder.deleteButton.setOnClickListener {
                    get_instance()?.showDeletaAlertDialog(tag)
                }
            } else {
                holder.deleteButton.visibility = View.INVISIBLE
                holder.deleteButton.setOnClickListener {
                }
            }
        }

        override fun getItemCount(): Int {
            return tags.size
        }
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.IO) {
            tags_list.layoutManager = LinearLayoutManager(applicationContext)
            tagsAdapter.updateTags(database!!)
            tags_list.adapter = tagsAdapter

        }
    }


    fun showDeletaAlertDialog(tag: TagInfo) {
        AlertDialog.Builder(this).setMessage(
            getText(R.string.tag_remove_alert).toString()
                .replace("%s", tag.name)
        )
            .setPositiveButton(
                R.string.remove,
                { dialogInterface: DialogInterface, _: Int -> delete_tag(tag) })
            .setNegativeButton(
                R.string.cancel,
                { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }).create()
            .show()
    }

    fun delete_tag(tag: TagInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.placeDao()?.delete(database?.placeDao()!!.getById(tag.id))
            tagsAdapter.updateTags(database!!)
            runOnUiThread({
                tagsAdapter.notifyDataSetChanged()
            })
        }
    }

    fun add_map(map: MapEntity) {

    }

    override fun getActivityLayoutId() = R.layout.fragment_tags_list

    override fun onStop() {
        super.onStop()
        instance = null
    }

    override fun onResume() {
        super.onResume()
        instance = this
    }

    companion object {
        private var instance: TagsListActivity? = null
        fun get_instance(): TagsListActivity? {
            return instance
        }
    }
}