package com.shevelev.music_visualization.demo

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.shevelev.music_visualization.R
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 4837
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startDemoButton.setOnClickListener {
            if (checkAudioPermission()) {
                startActivity(Intent(this@MainActivity, DemoActivity::class.java))
            }
            else {
                requestAudioPermission()
            }
        }
    }

    /**
     *
     */
    private fun checkAudioPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    /**
     *
     */
    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(this, Array(1) {Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CODE)
    }
}
