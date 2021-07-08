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
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted {
                    jump()
                }
                .onDenied {
                    ToastUtils.showLong("配网需要定位权限!")
                    finish()
                }
                .start()


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