package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MultiInvProperties {
	
	 public static String loadFromProperties(String file, String key){
		 return loadFromProperties(file, key, null);
	 }
	
	 public static String loadFromProperties(String file, String key, String defaultValue){
    	File FileP = new File(file);
    	Properties prop = new Properties();
    	String value = defaultValue;
    	File dir = new File(FileP.getParent());
    	if (!dir.exists()){
            dir.mkdirs();
        }
        if(!FileP.exists()){
            try {
            	FileP.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            if (prop.containsKey(key)) {
            	value = prop.getProperty(key);
            	in.close();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		return value;
    }
	 
	public static void saveToProperties(String file, String key, String value){
		saveToProperties(file, key, value, "No Comment");
	}
	
	public static void saveToProperties(String file, String key, String value, String comment){
    	File FileP = new File(file);
    	Properties prop = new Properties();
    	File dir = new File(FileP.getParent());
    	if (!dir.exists()){
            dir.mkdirs();
        }
        if(!FileP.exists()){
            try {
            	FileP.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            prop.put(key, value);
            prop.store(new FileOutputStream(FileP), comment);
            in.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}

