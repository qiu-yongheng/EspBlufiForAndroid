package com.espressif.espblufi.util

import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.blankj.utilcode.util.ToastUtils
import com.espressif.espblufi.constants.BlufiConstants
import com.espressif.espblufi.db.RecordProvider

object DialogUtils {

    fun showDeleteDialog(context: Context, success:()->Unit) {
        MaterialDialog(context).show {
            title(text = "确定清空数据吗, 清空后不可恢复")
            input(
                hint = "密码",
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
                maxLength = 7
            ) { _, text ->
                if (text.toString() == BlufiConstants.DELETE_PWD) {
                    RecordProvider.deleteAllRecord()
                    ToastUtils.showLong("删除成功")
                    success()
                } else {
                    ToastUtils.showLong("删除失败, 密码错误!")
                }
            }
            positiveButton(text = "删除")
            negativeButton(text = "取消")
        }
    }

    fun showOutputExcelDialog(context: Context, call:()->Unit) {
        MaterialDialog(context).show {
            title(text = "数据导出")
            message(text = "是否将配网数据导出成Excel表格?")
            positiveButton(text = "导出") {
                call()
            }
            negativeButton(text = "取消") {
            }
        }
    }
}
