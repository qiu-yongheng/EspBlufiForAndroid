package com.espressif.espblufi.util

import android.content.Context
import android.content.SharedPreferences

/**
 * @author 邱永恒
 *
 * @playDuration 2018/2/26  9:00
 *
 * @desc SharedPreferences工具类
 *
 */
object SPUtils {
    private lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = context.getSharedPreferences("morning", Context.MODE_PRIVATE)
    }

    fun put(key: String, value: Any) {
        when (value) {
            is String -> {
                sp.edit().putString(key, value).apply()
            }
            is Int -> {
                sp.edit().putInt(key, value).apply()
            }
            is Float -> {
                sp.edit().putFloat(key, value).apply()
            }
            is Boolean -> {
                sp.edit().putBoolean(key, value).apply()
            }
        }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sp.getString(key, defaultValue) ?: ""
    }

    fun getInt(key: String, defaultValue: Int = -1): Int {
        return sp.getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return sp.getLong(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return sp.getFloat(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * SP中获取所有键值对

     * @return Map对象
     */
    val all: Map<String, *>
        get() = sp.all

    /**
     * SP中移除该key

     * @param key 键
     */
    fun remove(key: String) {
        sp.edit().remove(key).apply()
    }

    /**
     * SP中是否存在该key

     * @param key 键
     * *
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * SP中清除所有数据
     */
    fun clearAll() {
        sp.edit().clear().apply()
    }
}