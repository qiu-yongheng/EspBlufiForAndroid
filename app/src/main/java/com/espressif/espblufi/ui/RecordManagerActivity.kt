package com.espressif.espblufi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.espressif.espblufi.R
import com.espressif.espblufi.app.BaseActivity
import com.espressif.espblufi.db.RecordProvider
import com.espressif.espblufi.util.DialogUtils
import kotlinx.android.synthetic.main.activity_record_manager.*

class RecordManagerActivity: BaseActivity() {
    private val adapter = RecordAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_manager)
        toolbar.title = "数据管理"
        setSupportActionBar(toolbar)
        setHomeAsUpEnable(true)

        initView()
        initListener()
    }

    private fun initView() {
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
        adapter.setList(RecordProvider.getAllRecord())
    }

    private fun initListener() {
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            DialogUtils.showDeleteRecordDialog(this, item.mid) {
                adapter.removeAt(position)
                RecordProvider.deleteRecord(item)
            }
        }
    }
}