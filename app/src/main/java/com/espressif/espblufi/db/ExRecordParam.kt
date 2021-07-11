package com.espressif.espblufi.db

import com.google.gson.Gson

/**
 * @author 邱永恒
 *
 * @time 2021/7/10  10:01
 *
 * @desc
 *
 */

data class ExRecordParam(
    val status: Int,
    val msg: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}