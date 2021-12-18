package map.together.items

import map.together.items.interfaces.WithId

data class MediaItem(
    override val id: String,
    val uri: String,
    val displayMode: DisplayMode
) : WithId {

    enum class DisplayMode {
        FIT_CENTER,
        CENTER_CROP
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 9998
        const val PICK_VIDEO_REQUEST = 9999
    }
}