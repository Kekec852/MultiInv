package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MultiInvReader {
    public final MultiInv plugin;

    public MultiInvReader(MultiInv instance) {
        plugin = instance;
    }
    
    private ArrayList<String> createShares() {
        ArrayList<String> lines = new ArrayList<String>();
        File file = new File("plugins" + File.separator + "MultiInv" + File.separator + "shares.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
          BufferedReader input =  new BufferedReader(new FileReader(file));
          try {
            String line = null; //not declared within while loop
            while (( line = input.readLine()) != null){
              lines.add(line);
            }
          }
          finally {
            input.close();
          }
        }
        catch (IOException ex){
          ex.printStackTrace();
        }
        return lines;
      }
    public boolean parseShares() {
        ArrayList<String> lines = createShares();
        ArrayList<String> worldList = new ArrayList<String>();
        for (String line : lines){
            String[] content = line.split("#");
            if ((content[0].matches("[^(\\s*)]"))){
                String[] worlds = content[0].split(", ");
                if (plugin.getServer().getWorld(worlds[0]) == null){
                    MultiInv.log.info("["+ MultiInv.pluginName + "] shares.txt contains major non-existant world " + worlds[0]);
                    return false;
                }else{
                    if (worldList.contains(worlds[0])){
                        MultiInv.log.info("["+ MultiInv.pluginName + "] shares.txt contains multiple instances of " + worlds[0]);
                        return false;
                    }
                    worldList.add(worlds[0]);
                    int i = 1;
                    while (i < worlds.length){
                        if (plugin.getServer().getWorld(worlds[i]) == null){
                            MultiInv.log.info("["+ MultiInv.pluginName + "] shares.txt contains minor non-existant world " + worlds[i]);
                        }else{
                            if (worldList.contains(worlds[i])){
                                MultiInv.log.info("["+ MultiInv.pluginName + "] shares.txt contains multiple instances of " + worlds[i]);
                                return false;
                            }else{
                                plugin.sharesMap.put(worlds[i], worlds[0]);
                            }
                        }
                        i++;
                    }    
                }
            }
        }
        return true;
    }
    public ArrayList<String> readLines(File file) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
          BufferedReader input =  new BufferedReader(new FileReader(file));
          try {
            String line = null;
            while (( line = input.readLine()) != null){
              lines.add(line);
            }
          }
          finally {
            input.close();
          }
        }
        catch (IOException ex){
          ex.printStackTrace();
        }
        return lines;
      }
    
}
