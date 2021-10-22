package lobna.smile.detection.interfaces

import android.graphics.Bitmap

/**
 * An interface used to notify capturing an image
 * */
interface CaptureImageInterface {
    fun imageCaptured(bitmap: Bitmap, rotationDegrees: Int)
}