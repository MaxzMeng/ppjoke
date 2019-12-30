package me.maxandroid.ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import me.maxandroid.ppjoke.model.Destination;

public class AppConfig {
    private static HashMap<String, Destination> sDestConfig;

    public static HashMap<String, Destination> getsDestConfig() {
        if (sDestConfig == null) {
            String content = parseFile("destination.json");

            sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }
        return sDestConfig;
    }

    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();
        InputStream stream = null;
        BufferedReader reader = null;
        StringBuffer builder = new StringBuffer();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
