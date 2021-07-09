package com.espressif.espblufi.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.greenrobot.eventbus.EventBus

/**
 * @author 邱永恒
 *
 * @time 2021/7/9  23:46
 *
 * @desc
 *
 */

object LooperManager {
    private val handler = Handler(Looper.getMainLooper())


    fun start(interval: Long) {
        log("启动looper")
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(LooperTask(interval), interval)
    }

    fun stop() {
        log("停止looper")
        handler.removeCallbacksAndMessages(null)
    }

    private fun log(msg: String) {
        Log.d("LooperManager", msg)
    }

    private class LooperTask(private val interval: Long) : Runnable {
        override fun run() {
            EventBus.getDefault().post(LooperEvent())
            handler.postDelayed(this, interval)
        }
    }
}

class LooperEvent