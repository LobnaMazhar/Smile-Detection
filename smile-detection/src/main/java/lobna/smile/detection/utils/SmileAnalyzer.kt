package lobna.smile.detection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class SmileAnalyzer(val context: Context, val cameraHelper: CameraHelper) : ImageAnalysis.Analyzer {

    private val TAG = SmileAnalyzer::class.simpleName

    lateinit var faceDetector: FaceDetector

    init {
        setupFaceDetector()
    }

    private fun setupFaceDetector() {
        val faceDetectorOptions = getFaceDetectionOptions()
        faceDetector = FaceDetection.getClient(faceDetectorOptions)
    }

    private fun getFaceDetectionOptions(): FaceDetectorOptions {
        return FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            faceDetector.process(InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees))
                .addOnSuccessListener { results ->
                    results.forEach { it ->
                        if ((it?.smilingProbability ?: 0f) > 0.5) {
                            cameraHelper.takePicture(context)
                            return@addOnSuccessListener
                        }
                    }
                    image.close()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Face Detector failed.$e")
                    image.close()
                }
        }
    }
}