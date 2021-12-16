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
import kotlinx.android.synthetic.main.fragment_maps_list.*
import kotlinx.android.synthetic.main.fragment_tags_list.tags_list
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import map.together.R
import map.together.db.AppDatabase
import map.together.db.entity.MapEntity
import map.together.repository.CurrentUserRepository


class MapsListActivity : BaseActivity() {

    data class MapInfo(val id: Long, val name:String, val isOwner: Boolean, val members: String)

    val mapsAdapter = MapsAdapter()
    class MapsAdapter() :
        RecyclerView.Adapter<MapsAdapter.MapsViewHolder>() {
        private val mapsInfo = ArrayList<MapInfo>()

        fun updateMaps(database: AppDatabase)
        {
            val maps = CurrentUserRepository.currentUser.value?.let {
                database.mapDao().getByUserHasAccess(
                    it.id)
            }
            mapsInfo.clear()
            if (maps != null) {
                maps.forEach({
                    var users: Long = 0
                    var ownerId:Long = it.ownerId
                    try {
                        users = database.mapDao().countMembersById(it.id)
                    } catch (ex: Exception) {}
                    mapsInfo.add(MapInfo(it.id, it.name, ownerId == CurrentUserRepository.currentUser.value?.id, users.toString()))
                })
            }
        }

        class MapsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameTextView: TextView
            var infoTextView: TextView
            var iconView: SimpleDraweeView
            var deleteButton: Button

            init {
                nameTextView = itemView.findViewById(R.id.map_name)
                infoTextView = itemView.findViewById(R.id.map_inf0)
                iconView = itemView.findViewById(R.id.map_icon)
                deleteButton = itemView.findViewById(R.id.map_delete)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.map_list_item, parent, false)
            return MapsViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
            val map = mapsInfo[position]
            holder.nameTextView.text = map.name
            var text = ""
            // val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
            // searchManager.submit(Point(tag.latitude.toDouble(),tag.longitude.toDouble()), 1,
            //     SearchOptions().setSearchTypes(SearchType.GEO.value),
            if (map.isOwner) text = get_instance()?.getText(R.string.owner) as String
            else text = get_instance()?.getText(R.string.member) as String
            holder.infoTextView.text = text + ", " + map.members + get_instance()?.getText(R.string.members)
            holder.deleteButton.setOnClickListener {
                get_instance()?.showDeletaAlertDialog(map)
            }
        }

        override fun getItemCount(): Int {
            return mapsInfo.size
        }
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.IO) {
            maps_list.layoutManager = LinearLayoutManager(applicationContext);
            mapsAdapter.updateMaps(database!!)
            maps_list.adapter = mapsAdapter

        }
    }

    fun showDeletaAlertDialog(map: MapsListActivity.MapInfo) {
        if (map.isOwner) {
            AlertDialog.Builder(this).setMessage(getText(R.string.map_remove_alert).toString()
                    .replace("%s", map.name))
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> delete_map(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }).create().show()
        }
        else
        {
            AlertDialog.Builder(this).setMessage(getText(R.string.map_leave_alert).toString()
                    .replace("%s", map.name))
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> leave_map(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }).create().show()
        }
    }

    fun delete_map(map: MapsListActivity.MapInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.mapDao()?.delete(database!!.mapDao().getById(map.id))
            mapsAdapter.updateMaps(database!!)
            runOnUiThread({
                mapsAdapter.notifyDataSetChanged()})
        }
    }

    fun leave_map(map: MapsListActivity.MapInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.userMapDao()?.delete(
                database?.userMapDao()!!.getByMapIdAndUserId(
                    map.id,
                    CurrentUserRepository.currentUser.value!!.id
                )
            )
            mapsAdapter.updateMaps(database!!)
            runOnUiThread({
            mapsAdapter.notifyDataSetChanged()})
        }
    }

    fun add_map(map: MapEntity) {

    }

    override fun onStop() {
        super.onStop()
        instance = null;
    }

    override fun onResume() {
        super.onResume()
        instance = this;
    }

    companion object {
        private var instance: MapsListActivity? = null
        fun get_instance(): MapsListActivity?
        {
            return instance;
        }
    }

    override fun getActivityLayoutId() = R.layout.fragment_maps_list
}