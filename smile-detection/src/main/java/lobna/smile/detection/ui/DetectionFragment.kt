package lobna.smile.detection.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import lobna.smile.detection.databinding.FragmentDetectionBinding
import lobna.smile.detection.utils.CameraHelper
import lobna.smile.detection.utils.PermissionUtil

class DetectionFragment : Fragment() {

    private lateinit var fragmentDetectionBinding: FragmentDetectionBinding
    lateinit var cameraView: PreviewView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentDetectionBinding = FragmentDetectionBinding.inflate(inflater)
        cameraView = fragmentDetectionBinding.cameraView
        return fragmentDetectionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PermissionUtil.ifPermissionsGranted(requireContext(), PermissionUtil.permissions)) {
            startCamera()
        } else {
            PermissionUtil.requestPermissions(requireActivity())
        }
    }

    fun startCamera() {
        CameraHelper().setupCamera(requireContext(), this, cameraView)
    }
}