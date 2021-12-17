package map.together.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.reactivex.disposables.Disposable
import map.together.R
import map.together.items.ItemsList
import map.together.items.MediaItem
import map.together.utils.recycler.adapters.LinearHorizontalSpacingDecoration
import map.together.utils.recycler.adapters.MediaAdapter
import map.together.viewholders.MediaViewHolder

open class MediaViewerWrapper(
    fragment: BaseFragment,
    mediaRecycler: RecyclerView,
    private val noMediaContent: TextView,
    private val posValue: TextView,
    private val posCard: MaterialCardView,
    private val mediaList: ItemsList<MediaItem>
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
        verifyStoragePermissions(fragment.activity)
        posCard.visibility = View.INVISIBLE
        posValue.text = ""
        subscribe = mediaList.sizeChangedSubject().subscribe { size ->
            if (size == 0) {
                noMediaContent.visibility = View.VISIBLE
                posCard.visibility = View.INVISIBLE
            } else {
                noMediaContent.visibility = View.INVISIBLE
                mediaCount = size
                posValue.text = "${mediaPosition + 1}/$mediaCount"
                posCard.visibility = View.VISIBLE
            }
        }
        mediaRecycler.adapter = MediaAdapter(
            MediaViewHolder::class,
            R.layout.item_media,
            mediaList,
            onImageClick = { mediaItem, _ ->
                println("${mediaItem.id} clicked")
            }
        )
        val layoutManager = LinearLayoutManager(fragment.context, RecyclerView.HORIZONTAL, false)
        mediaRecycler.layoutManager = layoutManager
        val spacing = fragment.resources.getDimensionPixelSize(R.dimen.dimens_16dp)
        mediaRecycler.addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(mediaRecycler)

        mediaRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mediaList.size() > 0) {
                        mediaPosition = layoutManager.findFirstVisibleItemPosition()
                        posValue.text = "${mediaPosition + 1}/$mediaCount"
                        posCard.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun onDetach() {
        subscribe.dispose()
    }

    fun setMediaData(data: List<String>) {
        mediaList.setData(
            data.mapIndexed { index, uri ->
                MediaItem(index.toString(), uri, MediaItem.DisplayMode.FIT_CENTER)
            }.toMutableList()
        )
    }

}