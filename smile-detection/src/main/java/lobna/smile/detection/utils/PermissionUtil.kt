package lobna.smile.detection.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * A singleton class used to ask for required permissions
 * */
object PermissionUtil {

    /**
     * List of needed permissions
     * */
    val permissions = listOf(Manifest.permission.CAMERA)

    /**
     * Request required permissions
     *
     * @param activity the running activity that asks for the permissions
     * */
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, permissions.toTypedArray(), CameraHelper.CAMERA_PERMISSION_CODE
        )
    }

    /**
     * Check if permissions are granted
     *
     * @param context running context
     * @param permissions list of permissions to check for
     * */
    fun ifPermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}