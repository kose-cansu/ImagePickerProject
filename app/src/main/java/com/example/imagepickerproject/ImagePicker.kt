package com.example.imagepickerproject


import android.Manifest
import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File

class ImagePicker(
    private val activity: AppCompatActivity,
    private val onImagePickerResult: (Uri) -> Unit
) : DefaultLifecycleObserver {

    private val uniqueKey = System.currentTimeMillis().toString()

    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private lateinit var getImageContent: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private val cameraCachePath = File(activity.cacheDir, "camera")

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        requestCameraPermission = activity.activityResultRegistry.register("requestCameraPermission_$uniqueKey",
            owner, ActivityResultContracts.RequestPermission(),
            ActivityResultCallback<Boolean> { isGranted ->
                if (isGranted) {
                    takePicture()
                } else {
                    // TODO Permission not granted.
                }
            })

        getImageContent = activity.activityResultRegistry.register("getImageContent_$uniqueKey",
            owner, ActivityResultContracts.GetContent(),
            ActivityResultCallback<Uri?> { uri ->
                if (uri != null) {
                    onImagePickerResult(uri)
                }
            })

        takePicture = activity.activityResultRegistry.register("takePicture_$uniqueKey",
            owner, ActivityResultContracts.TakePicture(),
            ActivityResultCallback<Boolean> { success ->
                if (success) {
                    onImagePickerResult(getCameraCacheUri())
                }
            })
    }

    fun show() {
        val alertDialogBuilder = AlertDialog.Builder(activity)

        alertDialogBuilder.setTitle(R.string.image_picker)

        val options = arrayOf(
            activity.getString(R.string.select_image),
            activity.getString(R.string.take_picture)
        )

        alertDialogBuilder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImage()
                1 -> requestPermissionAndTakePicture()
            }
        }

        alertDialogBuilder.show()
    }

    private fun requestPermissionAndTakePicture() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun selectImage() {
        getImageContent.launch("image/*")
    }

    private fun takePicture() {
        takePicture.launch(getCameraCacheUri())
    }

    private fun getCameraCacheUri(): Uri {
        if (!cameraCachePath.exists()) {
            cameraCachePath.mkdirs()
        }

        val filename = "picture.jpg"
        val file = File(cameraCachePath, filename)

        return FileProvider.getUriForFile(activity,
            "com.example.imagepickerproject" + ".provider", file)
    }
}