package com.aldebaran.qi.sdk.autonomousrechargeapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.autonomousrecharge.AutonomousRecharge
import com.aldebaran.qi.sdk.autonomousrecharge.AutonomousRecharge.RECHARGE_PERMISSION
import com.aldebaran.qi.sdk.autonomousrecharge.AutonomousRechargeListeners
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    // initialize listener
    private val onDockingSoonListener =
        object : AutonomousRechargeListeners.OnDockingSoonListener {
            override fun onDockingSoon(qiContext: QiContext) {
                Toast.makeText(this@MainActivity, "Pepper needs to dock soon!", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // give auto recharge permission
        if (ContextCompat.checkSelfPermission(this, RECHARGE_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(RECHARGE_PERMISSION),
                MY_PERMISSIONS_REQUEST_RECHARGE
            )
        }
        QiSDK.register(this, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECHARGE -> {
                val feedback =
                    if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        getString(R.string.permission_granted)
                    } else {
                        getString(R.string.permission_denied)
                    }
                runOnUiThread {
                    Toast.makeText(this, feedback, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.d(TAG, "onRobotFocusGained")
        AutonomousRecharge.registerReceiver(qiContext)
        AutonomousRecharge.addOnDockingSoonListener(onDockingSoonListener)

        dockButton.setOnClickListener {
            try {
                AutonomousRecharge.startDockingActivity(this, recallButton.isChecked)
            } catch (t: Throwable) {
                Log.e(TAG, "start docking error", t)
            }
        }

        undockButton.setOnClickListener {
            try {
                AutonomousRecharge.startUndockingActivity(this)
            } catch (t: Throwable) {
                Log.e(TAG, "start undocking error", t)
            }
        }
    }

    override fun onRobotFocusLost() {
        Log.d(TAG, "onRobotFocusLost")
        AutonomousRecharge.unregisterReceiver()
        AutonomousRecharge.removeOnDockingSoonListener(onDockingSoonListener)
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.e(TAG, "onRobotFocusRefused: $reason")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val MY_PERMISSIONS_REQUEST_RECHARGE = 101
    }
}
