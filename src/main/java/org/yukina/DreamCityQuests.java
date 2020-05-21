package org.yukina;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.yukina.DataBase.DB;
import org.yukina.Events.QuestsListener;
import org.yukina.IO.FileUtils;
import org.yukina.Quests.QuestsUtils;
import org.yukina.UI.QuestsChestUI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DreamCityQuests extends JavaPlugin {
    public static Plugin plugin;
    public static File dataFolder;
    public static final String PREFIX = "§b[§6梦想之都任务§b] ";

    public void checkDateThread() {
        new Thread(() -> {
            try {
                while (true) {
                    Date date = new Date();
                    String now = new SimpleDateFormat("MMdd").format(date);
                    if (!FileUtils.settings.getString("Date").equalsIgnoreCase(now)) {
                        FileUtils.settings.getString("Date",now);
                        FileUtils.settings.save(FileUtils.config);
                    }
                    Thread.sleep(1000L);
                }
            } catch (Exception e) {

            }
        }).start();
    }

    @Override
    public void onEnable() {
        plugin = this;
        dataFolder = getDataFolder();
        QuestsListener questsListener = new QuestsListener(getDataFolder());
        getServer().getPluginManager().registerEvents(questsListener,this);
        FileUtils.checkConfig(this,getDataFolder());
        getLogger().info(PREFIX+"加载了 "+ QuestsUtils.loadQuests()+" 个任务!");
        try {
            FileUtils.settings.set("Date", new SimpleDateFormat("MMdd").format(new Date()));
            FileUtils.settings.save(FileUtils.config);
        }catch (IOException e){}
        DB.initDataBase();
        checkDateThread();
        questsListener.onInventoryChange();
        QuestsUtils.loadItems();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String []args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if ("DreamCityQuests".equalsIgnoreCase(lable)) {

                if ("getDailyMission".equalsIgnoreCase(args[0])) {
                    QuestsChestUI.InitQuestsChestUI(player);
                    return true;
                }
            }
            return true;
        }
        return true;
    }
}
