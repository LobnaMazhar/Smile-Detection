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


class CameraHelper(val captureImageInterface: CaptureImageInterface) {

    private val TAG = CameraHelper::class.simpleName

    private val cameraExecutor = Executors.newSingleThreadExecutor()

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

    private val getImageCapture = ImageCapture.Builder().build()

    private fun getImageAnalyzer(context: Context): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            .apply { setAnalyzer(cameraExecutor, SmileAnalyzer(context, this@CameraHelper)) }
    }

    private fun getCameraPreview(cameraView: PreviewView): Preview {
        return Preview.Builder().build()
            .also { it.setSurfaceProvider(cameraView.surfaceProvider) }
    }

    private fun getCameraSelector(lens: Int = CameraSelector.LENS_FACING_FRONT): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(lens).build()
    }

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

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}