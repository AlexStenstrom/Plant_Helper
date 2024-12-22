package se.gu.planter

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class for handling camera operations and permissions.
 */
class CameraHelper(private val activity: AppCompatActivity) {
    private lateinit var imageUri: Uri // URI of the captured image

    /**
     * Takes a picture using the device's camera.
     * Params:
     * launcher - ActivityResultLauncher for handling the camera activity result.
     * callback - Callback function to be invoked with the captured image URI.
     */
    fun takePicture(launcher: ActivityResultLauncher<Intent>, callback: (Uri?) -> Unit) {
        if (checkCameraPermission()) {
            openCamera(launcher, callback)
        } else {
            requestCameraPermission()
        }
    }

    /**
     * Checks if the app has permission to use the camera.
     * Returns:
     * True if permission is granted, false otherwise.
     */
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests camera permission from the user.
     */
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    /**
     * Opens the camera app to take a picture.
     * Params:
     * launcher - ActivityResultLauncher for handling the camera activity result.
     * callback - Callback function to be invoked with the captured image URI.
     */
    private fun openCamera(launcher: ActivityResultLauncher<Intent>, callback: (Uri?) -> Unit) {
        imageUri = createURI()

        launcher.launch(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        )

    }

    /**
     * Creates a URI for the image file using FileProvider.
     * Returns:
     * The URI for the image file.
     */
    private fun createURI(): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timestamp + "_.jpg"


        val imageFile : File = File(activity.filesDir, imageFileName)


        return FileProvider.getUriForFile(activity,
            "se.gu.planter.camerapermission.fileProvider",
            imageFile)
    }

    /**
     * Handles the result of the camera activity.
     * Params:
     * data - Intent containing the result data.
     * callback - Callback function to be invoked with the captured image URI.
     */
    fun handleActivityResult(data: Intent?, callback: (Uri?) -> Unit) {
        callback(imageUri)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1  // Request code for camera permission
    }
}