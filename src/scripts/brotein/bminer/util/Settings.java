package scripts.brotein.bminer.util;

import org.powerbot.script.AbstractScript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by noemailgmail on 4/17/2017.
 */
public class Settings{

    private String[] keys = {"LOCATION", "ORE", "BANKING", "DROPMODE", "SHIFTCLICK"};
    private String[] values = new String[5];

    public Settings(){

    }

    public boolean fileExists(){

        return false;
    }

    public void setProperty(String key, String value){
        Properties prop = new Properties();
        prop.setProperty(key, value);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream("settings.ini");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.store(stream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String[] key, String[] value){
        Properties prop = new Properties();
        for(int i = 0; i < key.length; i++){
            prop.setProperty(key[i], value[i]);
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream("settings.ini");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.store(stream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String[] key, String[] value, File file){
        Properties prop = new Properties();
        for(int i = 0; i < key.length; i++){
            prop.setProperty(key[i], value[i]);
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.store(stream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public static String parseBoolean(boolean b){
        if(b){
            return "true";
        } else {
            return "false";
        }
    }

    public static void main(String Args[]){
        Settings settings = new Settings();
        settings.setProperty(settings.getKeys(), new String[]{"1", "2", "3", "4", "5"});
    }
}
