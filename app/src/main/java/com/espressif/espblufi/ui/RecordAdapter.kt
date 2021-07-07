package com.espressif.espblufi.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.espressif.espblufi.R
import com.espressif.espblufi.db.RecordEntity
import com.espressif.espblufi.util.DateUtils
import com.espressif.espblufi.util.Res

class RecordAdapter : BaseQuickAdapter<RecordEntity, BaseViewHolder>(R.layout.item_record) {
    override fun convert(holder: BaseViewHolder, item: RecordEntity) {
        holder.setText(R.id.tv_sn, item.id.toString())
            .setText(R.id.tv_time, DateUtils.formatDate(item.date, DateUtils.HH_MM_SS))
            .setText(R.id.tv_machine_id, item.machineID)
            .setBackgroundColor(
                R.id.ll_item,
                if (holder.adapterPosition % 2 == 0) Res.getColor(R.color.color_record_bg_gray)
                else Res.getColor(R.color.color_record_bg_white)
            )
    }
}