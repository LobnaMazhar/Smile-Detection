package lobna.smile.detection.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {

    val permissions = listOf(Manifest.permission.CAMERA)

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            GlobalVariables.CAMERA_PERMISSION_CODE
        )
    }

    fun ifPermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}