package com.espressif.espblufi.constants

import com.espressif.espblufi.util.SPUtils

object MorningConfig {
    private const val KEY_SSID = "KEY_SSID"
    private const val KEY_PWD = "KEY_PWD"

    fun setSSID(ssid: String) {
        SPUtils.put(KEY_SSID, ssid)
    }

    fun getSSID(): String {
        return SPUtils.getString(KEY_SSID, "")
    }

    fun setPwd(pwd: String) {
        SPUtils.put(KEY_PWD, pwd)
    }

    fun getPwd(): String {
        return SPUtils.getString(KEY_PWD, "")
    }
}