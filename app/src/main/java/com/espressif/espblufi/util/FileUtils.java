package com.espressif.espblufi.util;

import android.content.Context;

import com.espressif.espblufi.db.RecordEntity;
import com.espressif.espblufi.db.RecordProvider;

import java.io.File;
import java.util.List;

public class FileUtils {
    public static final String outputPath = "/sdcard/MorningExcel";

    public static void exportExcel(Context context) {
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String date = DateUtils.INSTANCE.formatDate(System.currentTimeMillis(), "yyyyMMdd");
        String time = DateUtils.INSTANCE.formatDate(System.currentTimeMillis(), "HHmm");
        String excelFileName = "/Morning_" + date + "_" + time + ".xls";


        String[] title = {"SN", "Date", "Time", "MachineID"};
        String sheetName = "morning";

        List<RecordEntity> list = RecordProvider.INSTANCE.getAllRecord();

        String filePath = outputPath + excelFileName;

        ExcelUtil.initExcel(filePath, sheetName, title);
        ExcelUtil.writeObjListToExcel(list, filePath, context);
    }
}
