package com.espressif.espblufi.task

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.util.Log
import com.espressif.espblufi.util.DeviceUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object NwManager {
    private var executor: ExecutorService? = null
    private val runningTask = ArrayList<NwTask>()

    fun init() {
        executor = Executors.newFixedThreadPool(1)
    }

    private fun executor(device: BluetoothDevice) {
        if (isContains(device)) {
            log("${device.name}已在配网, 无需重复配网")
            return
        }
        val nwTask = NwTask(device)
        executor?.execute(nwTask)
        runningTask.add(nwTask)
    }

    fun executor(list: List<ScanResult>) {
        for (scanResult in list) {
            executor(scanResult.device)
        }
    }

    fun removeTask(task: NwTask) {
        runningTask.remove(task)
    }

    fun destroy() {
        log("销毁所有任务")
        executor?.shutdownNow()
        runningTask.clear()
    }

    private fun isContains(device: BluetoothDevice): Boolean {
        for (task in runningTask) {
            if (task.getUid() == DeviceUtils.getUid(device)) {
                return true
            }
        }
        return false
    }

    private fun log(msg: String) {
        Log.d("NwManager", msg)
    }

}