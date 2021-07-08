package com.espressif.espblufi.task

import android.bluetooth.*
import android.util.Log
import blufi.espressif.BlufiCallback
import blufi.espressif.BlufiClient
import blufi.espressif.params.BlufiConfigureParams
import blufi.espressif.response.BlufiStatusResponse
import com.espressif.espblufi.app.BlufiApp
import com.espressif.espblufi.constants.BlufiConstants
import com.espressif.espblufi.constants.MorningConfig
import com.espressif.espblufi.db.RecordProvider
import com.espressif.espblufi.util.DeviceUtils
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class NwTask(private val device: BluetoothDevice) : Runnable {
    private var client: BlufiClient? = null
    private var configure: BlufiConfigureParams? = null
    private val semaphore = Semaphore(0)

    var isConnect = false

    override fun run() {
        try {
            configure = MorningConfig.getConfigure()
            if (configure == null) {
                log("任务失败, 配网参数不存在!")
                return
            }

            client?.close()

            // 连接
            client = BlufiClient(BlufiApp.getInstance(), device)
            client?.setGattCallback(gattCallback)
            client?.setBlufiCallback(bluCallback)
            client?.connect()

            semaphore.tryAcquire(10, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            NwManager.removeTask(this)
            log("任务执行完毕: ${device.name}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            log(
                String.format(
                    Locale.ENGLISH, "onConnectionStateChange addr=%s, status=%d, newState=%d",
                    device.address, status, newState
                )
            )
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    isConnect = true
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isConnect = false
                    gatt?.close()
                }
            } else {
                isConnect = false
                gatt?.close()
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            log(String.format(Locale.ENGLISH, "onMtuChanged status=%d, mtu=%d", status, mtu))
            // 配网
            networking()
        }
    }

    private val bluCallback = object : BlufiCallback() {
        override fun onGattPrepared(
            client: BlufiClient?,
            gatt: BluetoothGatt?,
            service: BluetoothGattService?,
            writeChar: BluetoothGattCharacteristic?,
            notifyChar: BluetoothGattCharacteristic?
        ) {
            if (service == null || writeChar == null || notifyChar == null) {
                gatt?.disconnect()
            }
            val mtu = BlufiConstants.DEFAULT_MTU_LENGTH
            val requestMtu = gatt?.requestMtu(mtu) ?: false
            if (!requestMtu) {
                networking()
            }
        }

        override fun onPostConfigureParams(client: BlufiClient?, status: Int) {
            log("参数设置结果: ${if (status == 0) "成功" else "失败"}")
            client?.requestDeviceStatus()
        }

        override fun onDeviceStatusResponse(
            client: BlufiClient?,
            status: Int,
            response: BlufiStatusResponse?
        ) {
            log("配网结果: ${if (status == 0) "成功" else "失败"}")
            if (status == 0) {
                RecordProvider.addRecord(device)
            }
            client?.close()
        }
    }

    /**
     * 配网
     */
    private fun networking() {
        client?.configure(configure)
    }

    fun getUid(): String {
        return DeviceUtils.getUid(device)
    }

    private fun log(msg: String) {
        Log.d("NwTask", msg)
    }
}