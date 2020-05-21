package org.yukina.IO;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static File dataFolder;
    public static File playerData;
    public static File config;
    public static YamlConfiguration settings;

    public static void checkConfig(Plugin plugin , File Folder) {
        config = new File(Folder, "config.yml");
        dataFolder = Folder;
        playerData = new File(dataFolder+"/playerData");

        if(!playerData.exists()){
            playerData.mkdir();
        }
        if (!config.exists()) {
            plugin.saveDefaultConfig();
        }
        settings = YamlConfiguration.loadConfiguration(config);
    }

    public static boolean playerDataCotainsPlayer(Player player){
        String uuid = player.getUniqueId().toString();
        if(playerData.exists()) {
            for (File f : playerData.listFiles()) {
                if (f.getName().equalsIgnoreCase(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createPlayerData(Player player) {
        if (playerData.exists()) {
            try {
                String uuid = player.getUniqueId().toString();
                File file = new File(dataFolder + "/playerData", uuid + ".yml");
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetPlayerMission(){
        for(File f : playerData.listFiles()){
            try {
                FileWriter fw = new FileWriter(f);
                fw.write("");
                fw.flush();
                fw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static File getPlayerData(Player player){
        for(File f : playerData.listFiles()){
            if(f.getName().isEmpty()) return null;
            if(f.getName().replace(".yml","").equalsIgnoreCase(player.getUniqueId().toString())){
                return f;
            }
        }
        return null;
    }

}
