package lobna.smile.detection.utils

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

class CameraHelper {

    private val TAG = CameraHelper::class.simpleName

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    fun setupCamera(context: Context, lifecycleOwner: LifecycleOwner, cameraView: PreviewView) {
        val processCameraProvider = ProcessCameraProvider.getInstance(context)
        processCameraProvider.addListener({
            val cameraProvider = processCameraProvider.get()
            try {
                cameraProvider.unbindAll()

                val selector = getCameraSelector()
                val analyzer =
                    getImageAnalyzer().apply { setAnalyzer(cameraExecutor, SmileAnalyzer(context)) }
                val preview = getCameraPreview(cameraView)
                val imageCapture = getImageCapture()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, analyzer, preview)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun getImageCapture(): ImageCapture {
        return ImageCapture.Builder().build()
    }

    private fun getImageAnalyzer(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    }

    private fun getCameraPreview(cameraView: PreviewView): Preview {
        return Preview.Builder().build().also { it.setSurfaceProvider(cameraView.surfaceProvider) }
    }

    private fun getCameraSelector(lens: Int = CameraSelector.LENS_FACING_FRONT): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(lens).build()
    }
}