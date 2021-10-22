package lobna.smile.detection.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import lobna.smile.detection.R
import lobna.smile.detection.utils.CameraHelper
import lobna.smile.detection.utils.PermissionUtil

/**
 * Main Screen of the application,
 * [DetectionFragment] is shown initially
 * and upon capturing an image it navigate automatically to [ResultFragment]
 * */
class SmileActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val navHostFragment: Fragment? by lazy { supportFragmentManager.primaryNavigationFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smile)

        navController = Navigation.findNavController(this, R.id.fragment_container_view)
    }

    /**
     * Results of requesting camera permission
     * if [grantResults] wasn't empty and has a value of [PackageManager.PERMISSION_GRANTED]
     * then camera permissions were granted and user can continue with the logic
     * if else, user should grant the permissions otherwise he won't be able to use the library functionalities
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CameraHelper.CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navHostFragment?.childFragmentManager?.fragments?.get(0)?.apply {
                        if (this is DetectionFragment) startCamera()
                    }
                } else {
                    Toast.makeText(
                        this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT
                    ).show()

                    PermissionUtil.requestPermissions(this)
                }
            }
        }
    }
}