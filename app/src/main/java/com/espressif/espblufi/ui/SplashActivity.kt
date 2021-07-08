package com.espressif.espblufi.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.espressif.espblufi.R
import com.espressif.espblufi.constants.BlufiConstants
import com.espressif.espblufi.util.SPUtils

class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler.postDelayed({ jump() }, 1000)
    }

    private fun jump() {
        val paramJson = SPUtils.getString(BlufiConstants.KEY_CONFIGURE_PARAM, "")
        if (paramJson.isEmpty()) {
            ToastUtils.showLong("请先设置配网信息!")
            val intent = Intent(this, ConfigureOptionsActivity::class.java)
            intent.putExtra(ConfigureOptionsActivity.KEY_JUMP_MAIN, true)
            startActivity(intent)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}