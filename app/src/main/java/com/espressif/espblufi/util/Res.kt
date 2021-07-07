package com.espressif.espblufi.util

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * @author: 邱永恒
 * @time  : 2021/5/24 13:43
 * @desc  :
 */
object Res {
    private var context: Application? = null

    fun init(context: Application) {
        this.context = context
    }

    private fun getContext() = context ?: throw RuntimeException("Res没有初始化!")

    fun getString(@StringRes resId: Int): String {
        return getContext().getString(resId)
    }

    fun getColor(@ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(getContext(), colorResId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ResourcesCompat.getDrawable(getContext().resources, resId, null)
    }

    fun getPackName(): String {
        return getContext().packageName
    }
}