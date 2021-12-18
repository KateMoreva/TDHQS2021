package map.together.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.disposables.Disposable
import map.together.R
import map.together.activities.BaseActivity
import map.together.items.ItemsList
import map.together.items.MediaItem
import map.together.utils.recycler.adapters.LinearHorizontalSpacingDecoration
import map.together.utils.recycler.adapters.MediaAdapter
import map.together.viewholders.MediaViewHolder

open class MediaViewerWrapper(
    activity: BaseActivity,
    mediaRecycler: RecyclerView,
    editContentBtn: FloatingActionButton,
    private val noMediaContent: TextView,
    private var mediaItems: ItemsList<MediaItem> = ItemsList(mutableListOf())
) {
    protected var mediaPosition: Int = 0
    protected var mediaCount: Int = 0
    private val subscribe: Disposable

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    init {
        subscribe = mediaItems.sizeChangedSubject().subscribe { size ->
            if (size == 0) {
                noMediaContent.visibility = View.VISIBLE
            } else {
                noMediaContent.visibility = View.INVISIBLE
            }
        }
        editContentBtn.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activity.startActivityForResult(intent, MediaItem.PICK_IMAGE_REQUEST)
        }
        verifyStoragePermissions(activity)
        mediaRecycler.adapter = MediaAdapter(
            MediaViewHolder::class,
            R.layout.item_media,
            mediaItems,
            onImageClick = { mediaItem, _ ->
                println("${mediaItem.id} clicked")
            }
        )
        val layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        mediaRecycler.layoutManager = layoutManager
        val spacing = activity.resources.getDimensionPixelSize(R.dimen.dimens_16dp)
        mediaRecycler.addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(mediaRecycler)
    }

}