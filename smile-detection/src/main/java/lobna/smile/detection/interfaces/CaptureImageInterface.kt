package lobna.smile.detection.interfaces

import android.graphics.Bitmap

interface CaptureImageInterface {
    fun imageCaptured(bitmap: Bitmap, rotationDegrees: Int)
}