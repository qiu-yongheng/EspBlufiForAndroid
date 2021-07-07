package com.espressif.espblufi.ui

import android.bluetooth.le.ScanResult
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.espressif.espblufi.R
import com.espressif.espblufi.util.Res

/**
 * @author 邱永恒
 *
 * @time 2021/7/8  01:11
 *
 * @desc
 *
 */

class DeviceAdapter: BaseQuickAdapter<ScanResult, BaseViewHolder>(R.layout.item_device) {
    override fun convert(holder: BaseViewHolder, item: ScanResult) {
        holder.setText(R.id.tv_machine_id, getName(item))
            .setText(R.id.tv_mac, getMac(item))
    }

    private fun getName(scanResult: ScanResult): String {
        val device = scanResult.device
        return if (device.name == null) Res.getString(R.string.string_unknown) else device.name
    }

    private fun getMac(scanResult: ScanResult): SpannableStringBuilder {
        val device = scanResult.device
        val info = SpannableStringBuilder()
        info.append("Mac:").append(device.address)
            .append(" RSSI:").append(scanResult.rssi.toString())
        info.setSpan(ForegroundColorSpan(-0x616162), 0, 21, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        info.setSpan(ForegroundColorSpan(-0x72919d), 21, info.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return info
    }
}