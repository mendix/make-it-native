package com.mendix.developerapp.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

fun getSensorManager(context: Context): SensorManager {
    return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
}

fun supportsAccelerometer(context: Context): Boolean {
    return getSensorManager(context).getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
}

fun supportsGyroscope(context: Context): Boolean {
    return getSensorManager(context).getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
}

fun supportsAR(context: Context): Boolean {
    return supportsGyroscope(context) && supportsAccelerometer(context)
}
