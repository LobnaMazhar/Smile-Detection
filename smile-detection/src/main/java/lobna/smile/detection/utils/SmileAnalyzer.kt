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

/**
 * An [ImageAnalysis.Analyzer] subclass to utilize camera analysis and face detection features required
 *
 * @param context a running context
 * @param cameraHelper an instance of the camera class where the [SmileAnalyzer] will be used
 *
 * [setupFaceDetector] is called after creating an instance of the class to initialize [faceDetector]
 * with the need options from [getFaceDetectionOptions]
 * [faceDetector] is used to process camera frames, check [analyze]
 * */
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

    /**
     * Options for the face detector to classify the results to get probability of smiling
     * */
    private fun getFaceDetectionOptions(): FaceDetectorOptions {
        return FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()
    }

    /**
     * A method uses the [faceDetector] initialized before to process camera frames and selecting
     * an image with a good probability of smiling to capture it.
     * */
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