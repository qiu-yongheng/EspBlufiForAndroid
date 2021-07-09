package com.espressif.espblufi.util;

import android.content.Context;
import android.util.Log;

import com.espressif.espblufi.db.RecordEntity;
import com.espressif.espblufi.db.RecordProvider;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler;

public class FileUtils {
    public static final String outputPath = "/sdcard/MorningExcel";
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void exportExcel(Context context, OnExportListener listener) {
        executor.execute(() -> {

            File file = new File(outputPath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                log("创建文件夹" + outputPath + ": " + mkdirs);
            }

            String date = DateUtils.INSTANCE.formatDate(System.currentTimeMillis(), "yyyyMMdd");
            String time = DateUtils.INSTANCE.formatDate(System.currentTimeMillis(), "HHmm");
            String excelFileName = "/Morning_" + date + "_" + time + ".xls";


            String[] title = {"SN", "Date", "Time", "MachineID"};
            String sheetName = "morning";

            List<RecordEntity> list = RecordProvider.INSTANCE.getAllRecord();

            String filePath = outputPath + excelFileName;

            ExcelUtil.initExcel(filePath, sheetName, title);
            boolean isSuccess = ExcelUtil.writeObjListToExcel(list, filePath, context);
            if (listener != null) {
                listener.onComplete(isSuccess);
            }
        });
    }

    private static void log(String msg) {
        Log.d("FileUtils", msg);
    }

    public interface OnExportListener {
        void onComplete(boolean isSuccess);
    }
}
