package map.together.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.BaseActivity
import map.together.items.ItemsList
import map.together.items.MediaItem
import kotlin.random.Random

@InternalCoroutinesApi
class MediaLoaderWrapper(
    private val fragment: BaseActivity,
    private val mediaRecycler: RecyclerView,
    editContentBtn: FloatingActionButton,
    noMediaContent: TextView,
    private var mediaItems: ItemsList<MediaItem> = ItemsList(mutableListOf())
) : MediaViewerWrapper(fragment, mediaRecycler, editContentBtn, noMediaContent, mediaItems) {

    init {

        noMediaContent.text = fragment.resources?.getString(R.string.load_media)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == MediaItem.PICK_IMAGE_REQUEST) {
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    loadMedia(uri)
                }
            }
            if (data?.clipData == null) {
                data?.data?.let { uri ->
                    loadMedia(uri)
                }
            }
        }
    }

    private fun loadMedia(uri: Uri) {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = fragment.contentResolver?.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let {
            cursor.getString(it)
        }
        cursor?.close()
        filePath?.let {
            val item =
                MediaItem((Random.nextInt()).toString(), it, MediaItem.DisplayMode.CENTER_CROP)
            mediaItems.addLast(item)
            mediaRecycler.smoothScrollToPosition(mediaItems.size() - 1)
        }
    }

    fun getLoadedData(): List<String> {
        return mediaItems.items.map { mediaItem -> mediaItem.uri }
    }

}