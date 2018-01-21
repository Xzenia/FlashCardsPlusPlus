package com.treskie.conrad.flashcardsplus;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipClass {
    private final int BUFFER = 2048;
    private static final String TAG = "ZipClass";
    public String outputLocation = Environment.getExternalStorageDirectory()+"/flashcardsplusplus/backup/";
    public void zip (String[] files, String zipName){
        try {
            BufferedInputStream origin = null;
            FileOutputStream destination = new FileOutputStream(outputLocation+zipName+".zip");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destination));
            byte data[] = new byte[BUFFER];
            for (int counter = 0; counter < files.length; counter++) {
                Log.d(TAG, "Adding " + files[counter] + " to zip file " + zipName);
                FileInputStream inputStream = new FileInputStream(files[counter]);
                origin = new BufferedInputStream(inputStream, BUFFER);
                ZipEntry entry = new ZipEntry(files[counter].substring(files[counter].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unzip (String zipFile){
        try {
            FileInputStream inputStream = new FileInputStream(outputLocation+zipFile);
            ZipInputStream zis = new ZipInputStream(inputStream);
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null){
                FileOutputStream outputStream = new FileOutputStream(outputLocation+ze.getName());
                for (int c = zis.read(); c != -1; c = zis.read()) {
                    outputStream.write(c);
                }
                zis.closeEntry();
                outputStream.close();
                Log.d(TAG, "Unzipping successful.");
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unzipping did not occur!!!");

        }
    }

    public void deleteFiles(){
        try {
            File cardFile = new File(outputLocation+"card");
            File deckFile = new File(outputLocation+"deck");
            Boolean cardFileDeleted = cardFile.delete();
            Boolean deckFileDeleted = deckFile.delete();
        } catch (Exception FileNotFoundException){
            Log.e(TAG, FileNotFoundException.getMessage());
        }

    }

    public void outputFolder() {
        File outputDir = new File(outputLocation);
        if (outputDir.exists()){
            Log.d(TAG, "Directory already exists.");
        } else {
            outputDir.mkdirs();
        }

    }
}
