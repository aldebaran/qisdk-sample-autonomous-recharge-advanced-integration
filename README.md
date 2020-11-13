# Autonomous Recharge Companion Library

*v1.0.1 - Documentation last updated on 13/11/2020*

## Summary
This repository contains the companion library for the Autonomous Recharge application, and a sample application to demonstrate its usage.
The main objective of this library is to provide applications a convenient method to trigger recharge behaviors.
For example, using this library, one can make Pepper dock on its pod just by asking Pepper by voice.
In addition, it assists advanced applications when Pepper must navigate to the pod from a long distance. Note that this feature can only be used if your application already handles long range navigation, and is only needed if Pepper is placed away from the line of sight of the pod.
If these conditions do not apply to your use case, the Autonomous Recharge application should work out of the box without this library.

## Minimum Configuration
* Pepper 1.8 / 1.8A
* QiSDK API
* A real robot *(does not work on a virtual robot)*
* Autonomous Recharge application installed on the robot

## Setup
* In Android Studio, go to `File` --> `Settings` --> `Plugins`, and make sure the Kotlin plugin is installed.
* In your project's top-level `build.gradle` file, make sure that all the properties, dependencies and repositories declared in this project's [`build.gradle`](build.gradle) file are included. Note that the versions do not have to be identical.
* Add a dependency to AndroidX in your project's `gradle.properties` file:
``` groovy
    android.useAndroidX=true
    android.enableJetifier=true
```
* Go to `File` --> `New` --> `Import module...` --> add the source directory of the `autonomousrecharge` module and click `Finish`
* Add the dependency to your module's `build.gradle` file:
``` groovy
  implementation project(path: ':autonomousrecharge')
```
* Add the following permission to your module's `AndroidManifest.xml` file:
``` groovy
  <uses-permission android:name="com.softbankrobotics.permission.AUTO_RECHARGE" />
```

## Features
The library provides APIs for the following features:
- Start the dock / undock activities in the Autonomous Recharge application. Note that when starting the dock activity, there is an optional argument to recall the previous pod position or not (default is true). If it is false, Pepper will rescan the area to find the dock. If it is true, Pepper will trust its memory and more quickly get on the dock. This is useful if your application handles the navigation to the pod by itself, and the previous position is no longer precise enough to be reliable due to drift.
- Subscribe to the docking soon event, triggered by the Autonomous Recharge application. This event is triggered when the battery level is less than 3% above the specified low battery threshold, or when the time is 5 minutes before the specified docking alarm goes off.

## Sample Usage
``` kotlin
override fun onRobotFocusGained(qiContext: QiContext) {

    // register the broadcast receiver for autonomous recharge
    AutonomousRecharge.registerReceiver(qiContext)

    // start the docking activity upon a button click
    // assumes dockButton is a Button, and recallPod is a Switch, both in the activity layout
    dockButton.setOnClickListener {
        try {
            AutonomousRecharge.startDockingActivity(this, recallPod.isChecked)
        } catch (t: Throwable) {
            Log.e(TAG, "start docking error", t)
        }
    }
    
    // add a docking soon listener that displays a Toast
    AutonomousRecharge.addOnDockingSoonListener(
        object : AutonomousRechargeListeners.OnDockingSoonListener {
            override fun onDockingSoon(qiContext: QiContext) {
                Toast.makeText(this@MainActivity, "Pepper needs to dock soon!", Toast.LENGTH_LONG).show()
            }
        }
    )
}

override fun onRobotFocusLost() {
    // don't forget to unregister the receiver and remove the listeners!
    AutonomousRecharge.unregisterReceiver()
    AutonomousRecharge.removeAllOnDockingSoonListeners()
}
```

## License
See the [COPYING](COPYING.md) file for the license.