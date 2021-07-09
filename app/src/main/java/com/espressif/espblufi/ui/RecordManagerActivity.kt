package com.espressif.espblufi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.espressif.espblufi.R
import kotlinx.android.synthetic.main.activity_record_manager.*

class RecordManagerActivity: AppCompatActivity() {
    private val adapter = RecordAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_manager)
        setSupportActionBar(toolbar)
        initView()
        initListener()
    }

    private fun initView() {
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    private fun initListener() {

    }
}