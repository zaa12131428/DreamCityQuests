package org.yukina.UI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yukina.DataBase.DB;
import org.yukina.IO.FileUtils;
import org.yukina.Quests.Quests;
import org.yukina.Quests.QuestsUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestsChestUI {
    private static Inventory chestUI;

    public static void InitQuestsChestUI(Player player) {
        new Thread(()-> {

            if (!DB.dataBaseHasPlayer(player)) {
                try {
                    Connection connection = DB.getConnection();
                    Statement statement = connection.createStatement();
                    statement.execute("use Mission");
                    statement.execute("INSERT INTO MissionTable (playername,Day1,Day2,Day3,Day4,Day5,Day6,Day7,Day8,Day9,Day10,Day11,Day12,Day13,Day14,Day15,Day16,Day17,Day18,Day19,Day20,Day21,Day22,Day23,Day24,Day25,Day26,Day27,Day28,Day29,Day30,Day31) VALUES ('" + player.getName() + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Date date = new Date();
            int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));

            chestUI = Bukkit.createInventory(null, 54, "§6梦想之都任务系统");

            short damage = 0;
            for (int i = 12; i < 15; i++) {
                Quests q = QuestsUtils.playerMissions.get(player).get(i - 12);
                ItemStack item = null;
                if (!QuestsUtils.checkPlayerMission(player, q.getName())) {
                    item = new ItemStack(Material.WOOL, 1, damage, new Integer(14).byteValue());
                } else {
                    item = new ItemStack(Material.WOOL, 1, damage, new Integer(5).byteValue());
                }
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName((i-12)+": §b[§6每日任务§b] §e" + q.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§a状态 §8> " + (QuestsUtils.checkPlayerMission(player, q.getName()) ? "§a已完成" : "§c未完成"));
                lore.add("§b任务描述 §8> §a" + q.getDesc());
                meta.setLore(lore);
                item.setItemMeta(meta);
                chestUI.setItem(i, item);
            }
            ItemStack item = new ItemStack(Material.WOOL, 1, damage, new Integer(5).byteValue());
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("§6< 上一页");
            item.setItemMeta(itemMeta);
            chestUI.setItem(48, item);
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("§6下一页 >");
            item.setItemMeta(itemMeta);
            chestUI.setItem(50, item);

            if (day >= 1 && day <= 9) {
                drawUI(player,1, 9);
            }

            if (day >= 10 && day <= 18) {
                drawUI(player,10, 18);
            }

            if (day >= 19 && day <= 27) {
                drawUI(player,19, 27);
            }

            if (day >= 27 && day <= 31) {
                drawUI(player,27, 31);
            }

            player.openInventory(chestUI);
        }).start();
    }

    public static void drawUI(Player player,int range1,int range2){

        int c = 0;
        for(int i = 36 ; i < 45 ; i++){
            ItemStack daily = null;

            if(FileUtils.settings.getStringList("v"+(range1+c)).get(1).equalsIgnoreCase("ICON")){
                c++;
                continue;
            }

            if(FileUtils.settings.getStringList("v"+(range1+c)).get(1).split(":").length == 2 ){
                int data = Integer.parseInt(FileUtils.settings.getStringList("v"+(range1+c)).get(1).split(":")[1]);
                daily = new ItemStack(Integer.parseInt(FileUtils.settings.getStringList("v"+(range1+c)).get(1).split(":")[0]),(byte)data);
            }else{
                daily = new ItemStack(Integer.parseInt(FileUtils.settings.getStringList("v"+(range1+c)).get(1).split(":")[0]));
            }

            ItemMeta meta = daily.getItemMeta();
            meta.setDisplayName("§6本月 §c -"+(range1+c)+"- §6日");

            List<String> lore = new ArrayList<>();
            List<String> reward = FileUtils.settings.getStringList("v"+(range1+c));
            lore.add("§c[§e -§6枫叶§e- §c]");
            lore.add("§6[§b奖励描述§6] §c: §e"+reward.get(0));
            lore.add("§8[§7是否可领取§8]"+ (( player.hasPermission("Maple.Maple") && (QuestsUtils.checkPlayerMissionCompleteAmount(player)==3) )?"§a是":"§c否"));
            lore.add("§c注意,领取奖励时背包必须空余一格空位,否则物品奖励无法获得！");
            meta.setLore(lore);

            daily.setItemMeta(meta);
            chestUI.setItem(i,daily);
            c++;
        }

        c = 0;
        for(int i = 27 ; i < 36 ; i++){
            ItemStack daily = null;

            if(FileUtils.settings.getStringList("n"+(range1+c)).get(1).equalsIgnoreCase("ICON")) {
                c++;
                continue;
            }

            if(FileUtils.settings.getStringList("n"+(range1+c)).get(1).split(":").length == 2 ){
                int data = Integer.parseInt(FileUtils.settings.getStringList("n"+(range1+c)).get(1).split(":")[1]);
                daily = new ItemStack(Integer.parseInt(FileUtils.settings.getStringList("n"+(range1+c)).get(1).split(":")[0]),(byte)data);
            }else{
                daily = new ItemStack(Integer.parseInt(FileUtils.settings.getStringList("n"+(range1+c)).get(1).split(":")[0]));
            }

            ItemMeta meta = daily.getItemMeta();
            meta.setDisplayName("§6本月 §b -"+(range1+c)+"- §6日");
            List<String> lore = new ArrayList<>();
            List<String> reward = FileUtils.settings.getStringList("n"+(range1+c));
            lore.add("§a[§b -§f普通§b- §a]");
            lore.add("§6[§b奖励描述§6] §c: §e"+reward.get(0));
            lore.add("§8[§7是否可领取§8]"+ ((QuestsUtils.checkPlayerMissionCompleteAmount(player)==3)?"§a是":"§c否"));
            lore.add("§c注意,领取奖励时背包必须空余一格空位,否则物品奖励无法获得！");
            meta.setLore(lore);
            daily.setItemMeta(meta);
            chestUI.setItem(i,daily);
            c++;
        }
    }
}
