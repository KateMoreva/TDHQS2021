package map.together.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.android.material.floatingactionbutton.FloatingActionButton
import map.together.fragments.BaseFragment
import map.together.items.MediaItem
import kotlin.random.Random


class MediaLoaderWrapper(
        private val fragment: BaseFragment,
        editContentBtn: FloatingActionButton,
        removeContentBtn: FloatingActionButton,
        var mediaItem: MediaItem?,
        private val onRemoveCallback: () -> Unit
) {

    init {
        editContentBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            fragment.startActivityForResult(intent, MediaItem.PICK_IMAGE_REQUEST)
            removeContentBtn.visibility = VISIBLE
        }
        removeContentBtn.setOnClickListener {
            mediaItem = null
            removeContentBtn.visibility = INVISIBLE
            onRemoveCallback.invoke()
        }
        if (mediaItem == null) {
            removeContentBtn.visibility = INVISIBLE
        } else {
            removeContentBtn.visibility = VISIBLE
        }
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
        val cursor = fragment.context?.contentResolver?.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let {
            cursor.getString(it)
        }
        cursor?.close()
        filePath?.let {
            mediaItem = MediaItem((Random.nextInt()).toString(), it, MediaItem.DisplayMode.FIT_CENTER)
        }
    }

}