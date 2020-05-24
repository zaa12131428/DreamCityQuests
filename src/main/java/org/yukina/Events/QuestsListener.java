package org.yukina.Events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.yukina.DreamCityQuests;
import org.yukina.IO.FileUtils;
import org.yukina.Quests.QUESTSTYPE;
import org.yukina.Quests.Quests;
import org.yukina.Quests.QuestsUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuestsListener implements Listener {

    private File dataFolder;

    public QuestsListener(File dataFolder){
        this.dataFolder = dataFolder;
    }

    public void onInventoryChange(){
        new Thread(()-> {
            while (true) {
                try {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if(QuestsUtils.playerMissions.get(p)==null) continue;
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(FileUtils.config);
                        if(!yml.getString("Date").equalsIgnoreCase(new SimpleDateFormat("MMdd").format(new Date()))){
                            QuestsUtils.refreshMission(p,3);
                        }

                        for (Quests q : QuestsUtils.playerMissions.get(p)) {
                            if (q.getType() == QUESTSTYPE.ITEM) {
                                if(p.getInventory().containsAtLeast(q.getItem(),q.getAmount())){
                                    if(!QuestsUtils.checkPlayerMission(p,q.getName())){
                                        QuestsUtils.setPlayerMissionComplete(p,q);
                                    }else{
                                        break;
                                    }
                                }
                            }
                        }


                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getClickedInventory() == null)return;
        if(event.getClickedInventory().getTitle() != null){
            String title = event.getClickedInventory().getTitle();
            if("§6梦想之都任务系统".equalsIgnoreCase(title)) {
                event.setCancelled(true);
                Player p = (Player) event.getWhoClicked();
                ItemStack i = event.getCurrentItem();
                if (i != null && i.getType() != Material.AIR) {

                    if(i.getItemMeta().getDisplayName().contains("每日任务")) {
                        if (event.getAction() == InventoryAction.PICKUP_HALF) {
                            int index = Integer.parseInt(i.getItemMeta().getDisplayName().split(":")[0]);
                            if (!QuestsUtils.checkPlayerMission((Player) event.getWhoClicked(), i.getItemMeta().getDisplayName().split("e")[1])) {
                                QuestsUtils.refreshMission((Player) event.getWhoClicked(), index);
                            }else {
                                System.out.println("NO");
                            }
                        }
                    }

                    if (i.getItemMeta().getDisplayName().contains("§6日")) {
                        String currentDay = new SimpleDateFormat("dd").format(new Date());
                        String day = i.getItemMeta().getDisplayName().split("-")[1];
                        if (currentDay.equalsIgnoreCase(day)) {
                            File file = FileUtils.getPlayerData(p);
                            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                            if (data.getBoolean("ALL")) {

                                if(i.getItemMeta().getLore().get(0).contains("枫叶")) {
                                    if (p.hasPermission("Maple.Maple")) {
                                        for(int t = 2 ; t < FileUtils.settings.getStringList("v"+day).size(); t++) {
                                            String cmd = FileUtils.settings.getStringList("v" + day).get(t);
                                            if (cmd.contains("ITEM")) {
                                                String itemName = cmd.split(":")[1];
                                                for(int amount = 0 ; amount < Integer.parseInt(cmd.split(":")[2]) ; amount++) {
                                                    p.getInventory().addItem(QuestsUtils.getItem(Integer.parseInt(itemName) - 1));
                                                }
                                            } else {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
                                            }
                                        }
                                    }else{
                                        p.sendMessage(DreamCityQuests.PREFIX+" §c你不是枫叶通行证玩家,仅可以领取普通玩家奖励哦！");
                                    }
                                }
                                if(i.getItemMeta().getLore().get(0).contains("普通")) {
                                    for (int t = 2 ; t < FileUtils.settings.getStringList("n" + day).size(); t++) {
                                        String cmd = FileUtils.settings.getStringList("n" + day).get(t);
                                        if(cmd.contains("ITEM")){
                                            String itemName = cmd.split(":")[1];
                                            for(int amount = 0 ; amount < Integer.parseInt(cmd.split(":")[2]) ; amount++) {
                                                p.getInventory().addItem(QuestsUtils.getItem(Integer.parseInt(itemName) - 1));
                                            }
                                        }else {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        for (Quests q : QuestsUtils.playerMissions.get(event.getPlayer())) {
            if (q.getType() == QUESTSTYPE.DIG) {
                Player player = event.getPlayer();
                Block b = event.getBlock();

                if (b.getTypeId() != q.getBlockType()) break;

                try {
                    File file = FileUtils.getPlayerData(player);
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

                    if (!QuestsUtils.checkPlayerMission(player, q.getName())) {
                        if (data.get(q.getType().getName() + q.getBlockType()) != null) {
                            int amount = data.getInt(q.getType().getName() + q.getBlockType());
                            amount += 1;
                            data.set(q.getType().getName() + q.getBlockType(), amount);
                            data.save(file);
                            if (amount >= q.getAmount()) {
                                QuestsUtils.setPlayerMissionComplete(player, q);
                            }
                        } else {
                            data.set(q.getType().getName() + q.getBlockType(), 1);
                            data.save(file);
                        }
                    }
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKillEntity(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        for (Quests q : QuestsUtils.playerMissions.get(event.getEntity().getKiller())) {

            if (q.getType() == QUESTSTYPE.KILL) {

                Entity entity = event.getEntity();
                Player player = event.getEntity().getKiller();

                if (entity.getType() != q.getEntity()) break;
                try {
                    File file = FileUtils.getPlayerData(player);
                    YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

                    if (!QuestsUtils.checkPlayerMission(player, q.getName())) {
                        if (data.get(q.getType().getName() + q.getEntity().toString()) != null) {
                            int amount = data.getInt(q.getType().getName() + q.getEntity().toString());
                            amount += 1;
                            data.set(q.getType().getName() + q.getEntity().toString(), amount);
                            data.save(file);
                            if (amount >= q.getAmount()) {
                                QuestsUtils.setPlayerMissionComplete(player, q);
                            }
                        } else {
                            data.set(q.getType().getName() + q.getEntity().toString(), 1);
                            data.save(file);
                        }
                    }
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        new Thread(()->{
            if(!FileUtils.playerDataCotainsPlayer(player)){
                FileUtils.createPlayerData(player);
                if(YamlConfiguration.loadConfiguration(FileUtils.getPlayerData(player)).getString("Date") == null){
                    QuestsUtils.resetPlayerData(player, FileUtils.getPlayerData(player), new SimpleDateFormat("dd").format(new Date()));
                    return;
                }

                if(!YamlConfiguration.loadConfiguration(FileUtils.getPlayerData(player)).getString("Date").equalsIgnoreCase(new SimpleDateFormat("dd").format(new Date()))) {
                    QuestsUtils.resetPlayerData(player, FileUtils.getPlayerData(player), new SimpleDateFormat("dd").format(new Date()));
                }else {
                    QuestsUtils.loadPlayerMission(player);
                }
            }
        }).start();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.getBlock().getType() != Material.MOB_SPAWNER)return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemOnCursor();
        BlockState placed = event.getBlockPlaced().getState();
        if(!itemStack.getItemMeta().hasLore())return;

        EntityType type = EntityType.valueOf(itemStack.getItemMeta().getLore().get(0).split(":")[1]);
        CreatureSpawner spawner = (CreatureSpawner) placed;
        spawner.setSpawnedType(type);
        placed.update();
    }
}
