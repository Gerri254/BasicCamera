package com.gerald.cameraapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_CODE = 20000
    private val GALLERY_PERMISSION_CODE = 33000
    private val GALLERY_IMAGE_CODE = 33400
    private val IMAGE_CAPTURE_CODE = 24000
    private var imageUri : Uri? = null
    private var imgView : ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgView = findViewById(R.id.imgCaptured)
        val btnOpenCamera : Button = findViewById(R.id.btnOpenCamera)
        val btnOpenGallery : Button = findViewById(R.id.btnOpenGallery)

        requestGalleryPermission()
        requestCameraPermission()


        val permissionGranted = requestCameraPermission()
        btnOpenCamera.setOnClickListener {

           // if (permissionGranted){
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE,"My Image")
                values.put(MediaStore.Images.Media.DESCRIPTION,"Taken from my app")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

                //Intents
                val intentOpenCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intentOpenCamera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                startActivityForResult(intentOpenCamera,IMAGE_CAPTURE_CODE)

           // }
        }

        btnOpenGallery.setOnClickListener {
        val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY_IMAGE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            imgView?.setImageURI(imageUri)
        }else if (resultCode == RESULT_OK && requestCode == GALLERY_IMAGE_CODE){
            imgView?.setImageURI(data?.data)
        }
        else{
            showAlert("An error occured")
        }
    }

    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = checkSelfPermission( Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.CAMERA)

                // Display permission dialog
                requestPermissions(permission, CAMERA_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }
    private fun requestGalleryPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                // Display permission dialog
                requestPermissions(permission, GALLERY_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }

    // Handle Allow or Deny response from the permission dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)//granted permission upon requesting
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === GALLERY_PERMISSION_CODE) {
            if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                // openCameraInterface()
            }
            else{

                finish()//close an app
                // Permission was denied
               showAlert("Camera permission was denied. Unable to take a picture.");
            }
        }
    }

    private fun showAlert(Message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("DANGER")
        builder.setMessage(Message)
        builder.setPositiveButton("OK",null)
        builder.create().show()

    }
}