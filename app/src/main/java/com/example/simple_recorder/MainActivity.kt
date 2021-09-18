package com.example.simple_recorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.simple_recorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.button.isEnabled = false


        // Check Permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 111)
        else
            binding.button.isEnabled = true

        val previewRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val uri: Uri = Uri.parse(it.data?.data.toString())
                    binding.videoView.setVideoURI(uri)
                    val mediaController = MediaController(this)
                    mediaController.show()
                    binding.videoView.setMediaController(mediaController)
                    mediaController.setAnchorView(binding.videoView)
                    handler.postDelayed(
                        { mediaController.show(0) },
                        100
                    )
                }
            }

        // Start video recording when clicked
        binding.button.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            previewRequest.launch(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.button.isEnabled = true
        }
    }
}
