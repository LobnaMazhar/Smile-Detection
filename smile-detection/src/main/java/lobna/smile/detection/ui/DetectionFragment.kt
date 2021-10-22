package lobna.smile.detection.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import lobna.smile.detection.databinding.FragmentDetectionBinding
import lobna.smile.detection.interfaces.CaptureImageInterface
import lobna.smile.detection.utils.CameraHelper
import lobna.smile.detection.utils.PermissionUtil

/**
 * A simple [Fragment] subclass
 * used to detect User's smile through Camera
 *
 * use the [startCamera] method to begin initializing camera to detect user's smile
 * */
class DetectionFragment : Fragment() {

    private lateinit var fragmentDetectionBinding: FragmentDetectionBinding
    lateinit var cameraView: PreviewView

    private val captureImageInterface = object : CaptureImageInterface {
        override fun imageCaptured(bitmap: Bitmap, rotationDegrees: Int) {
            navigateToResult(bitmap, rotationDegrees)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentDetectionBinding = FragmentDetectionBinding.inflate(inflater)
        cameraView = fragmentDetectionBinding.cameraView
        return fragmentDetectionBinding.root
    }

    /**
     * Check if the camera permissions was granted before the proceed with starting the camera
     * if else, ask for user's permissions to access camera
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PermissionUtil.ifPermissionsGranted(requireContext(), PermissionUtil.permissions)) {
            startCamera()
        } else {
            PermissionUtil.requestPermissions(requireActivity())
        }
    }

    /**
     * [CameraHelper] class is responsible for initializing camera
     * it needs a running Context, LifecycleOwner, and PreviewView of the camera
     * */
    fun startCamera() {
        CameraHelper(captureImageInterface).setupCamera(requireContext(), this, cameraView)
    }

    /**
     * use [navigateToResult] method to switch to results screen
     *
     * @param bitmap is the captured image that will be viewed in result screen
     * @param rotationDegrees is an integer indicating rotation degrees of the captured image
     * */
    fun navigateToResult(bitmap: Bitmap, rotationDegrees: Int) {
        findNavController().navigate(
            DetectionFragmentDirections.detectionToResult(bitmap, rotationDegrees)
        )
    }
}