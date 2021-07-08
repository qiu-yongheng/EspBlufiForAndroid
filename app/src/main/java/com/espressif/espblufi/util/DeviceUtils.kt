package com.espressif.espblufi.util

import android.bluetooth.BluetoothDevice

object DeviceUtils {
    fun getMid(device: BluetoothDevice): String {
        val name = device.name
        if (name.isNullOrEmpty()) {
            return ""
        }
        val split = name.split("-")
        return split[split.lastIndex]

    }

    fun getUid(device: BluetoothDevice): String {
        val mid = getMid(device)
        return mid.substring(0, mid.length - 2)
    }
}