package com.espressif.espblufi.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "record")
data class RecordEntity(
    var date: Long,
    var uid: String,
    var mid: String,
    var other: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @Ignore
    var param: ExRecordParam? = null

    fun getStatus(): Int {
        if (param == null) {
            param = Gson().fromJson(other, ExRecordParam::class.java)
        }
        return param?.status ?: STATUS_SUCCESS
    }

    fun getMsg(): String {
        if (param == null) {
            param = Gson().fromJson(other, ExRecordParam::class.java)
        }
        return param?.msg ?: "成功"
    }


    companion object {
        fun build(date: Long, uid: String, mid: String, status: Int, msg: String): RecordEntity{
            val param = ExRecordParam(status, msg)
            return RecordEntity(date, uid, mid, param.toJson())
        }

    }
}