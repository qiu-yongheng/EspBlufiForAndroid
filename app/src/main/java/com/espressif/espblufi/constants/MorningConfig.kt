package com.espressif.espblufi.constants

import blufi.espressif.params.BlufiConfigureParams
import com.espressif.espblufi.util.SPUtils
import com.google.gson.Gson

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

    fun setConfigure(params: BlufiConfigureParams) {
        SPUtils.put(BlufiConstants.KEY_CONFIGURE_PARAM, Gson().toJson(params))
    }

    fun getConfigure(): BlufiConfigureParams? {
        val json = SPUtils.getString(BlufiConstants.KEY_CONFIGURE_PARAM, "")
        return Gson().fromJson(json, BlufiConfigureParams::class.java)
    }
}