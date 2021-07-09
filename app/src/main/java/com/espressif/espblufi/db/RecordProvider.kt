package com.espressif.espblufi.db

import android.bluetooth.BluetoothDevice
import com.espressif.espblufi.app.BlufiApp

object RecordProvider {
    private val dao: RecordDao by lazy { AppDataBase.instance(BlufiApp.getInstance()).recordDao() }

    fun addRecord(entity: RecordEntity) {
        val record = dao.getRecordForUid(entity.uid)
        if (record == null) {
            dao.addRecord(entity)
        } else {
            record.date = entity.date
            record.mid = entity.mid
            record.other = entity.other
            dao.addRecord(record)
        }
    }

    fun addRecord(device: BluetoothDevice) {
        val name = device.name
        if (name.isNullOrEmpty()) {
            return
        }
        val split = name.split("-")
        val mid: String = split[split.lastIndex]
        val entity = RecordEntity(System.currentTimeMillis(), mid, mid, "")
        addRecord(entity)
    }

    fun deleteRecord(entity: RecordEntity) {
        dao.deleteRecord(entity)
    }

    fun deleteAllRecord() {
        dao.deleteAll()
        dao.resetIndex()
    }

    fun getAllRecord(): List<RecordEntity> {
        return dao.getAllRecord()
    }

    fun getRecordSize(): Int {
        return getAllRecord().size
    }
}