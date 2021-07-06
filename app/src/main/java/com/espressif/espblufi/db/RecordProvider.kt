package com.espressif.espblufi.db

import com.espressif.espblufi.app.BlufiApp

object RecordProvider {
    private val dao: RecordDao by lazy { AppDataBase.instance(BlufiApp.getInstance()).recordDao() }

    fun addRecord(entity: RecordEntity) {
        dao.addRecord(entity)
    }

    fun deleteRecord(entity: RecordEntity) {
        dao.deleteRecord(entity)
    }

    fun deleteAllRecord() {
        dao.deleteAll()
    }

    fun getAllRecord(): List<RecordEntity> {
        return dao.getAllRecord()
    }

    fun getRecordSize(): Int {
        return getAllRecord().size
    }
}