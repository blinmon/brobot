package scripts.brotein.bminer.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by noemailgmail on 4/17/2017.
 */
public class Settings {

    private String[] keys = {"LOCATION", "ORE", "BANKING", "DROPMODE", "SHIFTCLICK"};
    private String[] values = new String[5];

    public Settings() {

    }

    public static String parseBoolean(boolean b) {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    public String getProperty(String key) {
        String s;
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream("settings.ini");
            prop.load(stream);
            s = prop.getProperty(key);
            stream.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProperty(String key, File file) {
        String s;
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            prop.load(stream);
            s = prop.getProperty(key);
            stream.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getProperty(String[] key) {
        String[] s = new String[keys.length];
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream("settings.ini");
            prop.load(stream);
            for (int i = 0; i < key.length; i++) {
                s[i] = prop.getProperty(key[i]);
            }
            stream.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getProperty(String[] key, File file) {
        String[] s = new String[keys.length];
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            prop.load(stream);
            for (int i = 0; i < key.length; i++) {
                s[i] = prop.getProperty(key[i]);
            }
            stream.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setProperty(String key, String value) {
        Properties prop = new Properties();
        prop.setProperty(key, value);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream("settings.ini");
            prop.store(stream, null);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String[] key, String[] value) {
        Properties prop = new Properties();
        for (int i = 0; i < key.length; i++) {
            prop.setProperty(key[i], value[i]);
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream("settings.ini");
            prop.store(stream, null);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String[] key, String[] value, File file) {
        Properties prop = new Properties();
        for (int i = 0; i < key.length; i++) {
            prop.setProperty(key[i], value[i]);
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            prop.store(stream, null);
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
}
