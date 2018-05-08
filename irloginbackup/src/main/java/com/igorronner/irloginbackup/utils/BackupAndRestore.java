package com.igorronner.irloginbackup.utils;

import android.content.Context;
import android.os.Environment;

import com.igorronner.irloginbackup.init.ConfigUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class BackupAndRestore {
    public static void importDB(Context context) {
        try {
             File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(ConfigUtil.DATABASE_NAME);
                String backupDBPath = String.format("%s.bak", ConfigUtil.DATABASE_NAME);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak", ConfigUtil.DATABASE_NAME);
                File currentDB = context.getDatabasePath(ConfigUtil.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                return backupDB;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}