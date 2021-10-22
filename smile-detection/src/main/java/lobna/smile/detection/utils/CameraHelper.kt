package lobna.smile.detection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import lobna.smile.detection.interfaces.CaptureImageInterface
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * A helper class responsible for camera functions (setup, taking pictures)
 * */
class CameraHelper(val captureImageInterface: CaptureImageInterface) {

    private val TAG = CameraHelper::class.simpleName

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    companion object {
        val CAMERA_PERMISSION_CODE = 101
    }

    /**
     * setting up camera method
     *
     * @param context running context
     * @param lifecycleOwner lifecycle of the calling class
     * @param cameraView previewview view of the camera
     * */
    fun setupCamera(context: Context, lifecycleOwner: LifecycleOwner, cameraView: PreviewView) {
        val processCameraProvider = ProcessCameraProvider.getInstance(context)
        processCameraProvider.addListener({
            val cameraProvider = processCameraProvider.get()
            try {
                cameraProvider.unbindAll()

                val selector = getCameraSelector()
                val analyzer = getImageAnalyzer(context)
                val preview = getCameraPreview(cameraView)
                val imageCapture = getImageCapture
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, selector, analyzer, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Image Capture Builder
     * */
    private val getImageCapture = ImageCapture.Builder().build()

    /**
     * Method to create image analyzer instance using
     * [ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST] strategy that analyzes latest image
     * and [SmileAnalyzer] our own analyzer logic
     * */
    private fun getImageAnalyzer(context: Context): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            .apply { setAnalyzer(cameraExecutor, SmileAnalyzer(context, this@CameraHelper)) }
    }

    /**
     * Method to create camera preview instance
     *
     * @param cameraView previewView acts as a surface to show the camera through it
     * */
    private fun getCameraPreview(cameraView: PreviewView): Preview {
        return Preview.Builder().build()
            .also { it.setSurfaceProvider(cameraView.surfaceProvider) }
    }

    /**
     * Method to create camera selector
     *
     * @param lens required lens to use for the camera, set to front camera by default
     * */
    private fun getCameraSelector(lens: Int = CameraSelector.LENS_FACING_FRONT): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(lens).build()
    }

    /**
     * Method to take picture using [getImageCapture]
     * then converting the taken image to a bitmap to view it in results screen
     *
     * @param context a running context
     * */
    fun takePicture(context: Context) {
        getImageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)
                    val bitmap = imageProxyToBitmap(imageProxy)
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    imageProxy.close()
                    bitmap?.let { captureImageInterface.imageCaptured(it, rotationDegrees) }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e(TAG, "Failed to capture image", exception)
                }
            })
    }

    /**
     * Method used to convert image to bitmap
     *
     * @param image the image to be converted
     * @return the resulted bitmap
     * */
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}