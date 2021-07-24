package com.example.simpleflashlight

import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {

    //ON, Le flash est allumé
    //OFF, Le flash est éteint
    private enum class FlashLightState { ON, OFF }

    private var mFlashLightState = FlashLightState.OFF

    private val mCameraManager: CameraManager by lazy {
        getSystemService(CAMERA_SERVICE) as CameraManager
    }

    // Verifie si l'appareil ou est installee cette application
    // detient un " front facing camera"
    private val isFlashAvailable by lazy {
        applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }

    // what camera [front/back] (la camera qui doit etre utiliser pour allumer la flash)
    private var mCameraId: String? = null

    private lateinit var mFlashStateTv: TextView // texte qui montre si la torche est allumee ou non

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFlashStateTv = findViewById(R.id.mFlashStateTv)
        findViewById<FloatingActionButton>(R.id.mPowerButton).setOnClickListener {
            if (canFlashBeUsed()) toggleTorchState() else Toast.makeText(
                applicationContext,
                "Cet appareil n'a pas de torche",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Verifie si on peut utiliser une flash dans cet appareil
    // si on peut ca retourne true sinon ca retourne false
    private fun canFlashBeUsed(): Boolean {
        return if (isFlashAvailable) {
            mCameraId = mCameraManager.cameraIdList[0]
            true
        } else false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleTorchState() {
        mCameraId?.let {
            if (mFlashLightState == FlashLightState.ON) {
                mCameraManager.setTorchMode(it, false)
                mFlashLightState = FlashLightState.OFF
                mFlashStateTv.text = getString(R.string.flash_is_off)
            } else {
                mCameraManager.setTorchMode(it, true)
                mFlashLightState = FlashLightState.ON
                mFlashStateTv.text = getString(R.string.flash_is_on)
            }
        }
    }
}