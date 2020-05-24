package org.yukina.Quests;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.SpawnEgg;
import org.yaml.snakeyaml.Yaml;
import org.yukina.DataBase.DB;
import org.yukina.DreamCityQuests;
import org.yukina.IO.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuestsUtils {
    public static HashMap<Player,List<Quests>> playerMissions;
    public static List<Quests> AllMission;
    private static List<ItemStack> itemStacks;

    public static int loadQuests(){
        AllMission = new ArrayList<>();
        playerMissions = new HashMap<>();

        for(int i = 1;i<100;i++) {
            Quests quests = null;
            if(FileUtils.settings.getString("Quests."+i+".Name") == null) break;

            String questsName = FileUtils.settings.getString("Quests."+i+".Name");
            String description = FileUtils.settings.getString("Quests."+i+".Description");
            QUESTSTYPE questsType = QUESTSTYPE.getCode(FileUtils.settings.getString("Quests."+i+".Type"));

            if(questsType == QUESTSTYPE.KILL) {
                EntityType entityType = EntityType.valueOf(FileUtils.settings.getString("Quests." + i + ".Entity"));
                int amount = FileUtils.settings.getInt("Quests."+i+".Amount");
                quests = new Quests(questsName,questsType,entityType,amount,description);
            }else if(questsType == QUESTSTYPE.ITEM){
                int itemId = 0,value = 0;
                ItemStack item = null;
                if(FileUtils.settings.getString("Quests."+i+".Item").contains(":")) {
                    itemId = Integer.parseInt(FileUtils.settings.getString("Quests." + i + ".Item").split(":")[0]);
                    value = Integer.parseInt(FileUtils.settings.getString("Quests." + i + ".Item").split(":")[1]);
                    item = new ItemStack(itemId,1,(short) 1,(byte)value);
                }else{
                    itemId = Integer.parseInt(FileUtils.settings.getString("Quests." + i + ".Item"));
                    item = new ItemStack(itemId);
                }
                int amount = FileUtils.settings.getInt("Quests."+i+".Amount");
                quests = new Quests(questsName,questsType,item,amount,description);
            }else if(questsType == QUESTSTYPE.DIG){
                int blockType = FileUtils.settings.getInt("Quests."+i+".BlockType");
                int amount = FileUtils.settings.getInt("Quests."+i+".Amount");
                quests = new Quests(questsName,questsType,blockType,amount,description);
            }
            AllMission.add(quests);
        }
        return AllMission.size();
    }

    public static EntityType getEntity(String name){
        switch (name){
            case "BAT":return EntityType.BAT;
            case "BLAZE":return EntityType.BLAZE;
            case "CAVE_SPIDER":return EntityType.CAVE_SPIDER;
            case "CHICKEN":return EntityType.CHICKEN;
            case "COW":return EntityType.COW;
            case "CREEPER":return EntityType.CREEPER;
            case "DONKEY":return EntityType.DONKEY;
            case "ELDER_GUARDIAN":return EntityType.ELDER_GUARDIAN;
            case "ENDER_DRAGON":return EntityType.ENDER_DRAGON ;
            case "ENDERMITE":return EntityType.ENDERMITE;
            case "EVOKER":return EntityType.EVOKER;
            case "EVOKER_FANGS":return EntityType.EVOKER_FANGS;
            case "GHAST":return EntityType.GHAST;
            case "GIANT":return EntityType.GIANT;
            case "GUARDIAN ":return EntityType.GUARDIAN;
            case "HORSE":return EntityType.HORSE;
            case "HUSK":return EntityType.HUSK;
            case "IRON_GOLEM ":return EntityType.IRON_GOLEM ;
            case "LLAMA":return EntityType.LLAMA;
            case "MAGMA_CUBE":return EntityType.MAGMA_CUBE;
            case "MULE":return EntityType.MULE;
            case "MUSHROOM_COW":return EntityType.MUSHROOM_COW;
            case "OCELOT":return EntityType.OCELOT;
            case "PAINTING":return EntityType.PAINTING;
            case "PARROT  ":return EntityType.PARROT;
            case "PIG":return EntityType.PIG;
            case "PIG_ZOMBIE ":return EntityType.PIG_ZOMBIE;
            case "PLAYER":return EntityType.PLAYER;
            case "POLAR_BEAR":return EntityType.POLAR_BEAR;
            case "RABBIT":return EntityType.RABBIT;
            case "SHEEP":return EntityType.SHEEP;
            case "SHULKER ":return EntityType.SHULKER;
            case "SHULKER_BULLET":return EntityType.SHULKER_BULLET;
            case "SILVERFISH":return EntityType.SILVERFISH;
            case "SKELETON":return EntityType.SKELETON;
            case "SLIME":return EntityType.SLIME;
            case "SNOWMAN":return EntityType.SNOWMAN;
            case "SPIDER":return EntityType.SPIDER;
            case "SPLASH_POTION":return EntityType.SPLASH_POTION;
            case "SQUID":return EntityType.SQUID;
            case "STRAY":return EntityType.STRAY;
            case "UNKNOWN":return EntityType.UNKNOWN;
            case "VEX ":return EntityType.VEX;
            case "VILLAGER":return EntityType.VILLAGER;
            case "VINDICATOR":return EntityType.VINDICATOR;
            case "WITCH ":return EntityType.WITCH;
            case "WITHER":return EntityType.WITHER;
            case "WITHER_SKELETON":return EntityType.WITHER_SKELETON;
            case "WITHER_SKULL":return EntityType.WITHER_SKULL;
            case "WOLF":return EntityType.WOLF;
            case "ZOMBIE":return EntityType.ZOMBIE;
            case "ZOMBIE_HORSE ":return EntityType.ZOMBIE_HORSE ;
            case "ZOMBIE_VILLAGER":return EntityType.ZOMBIE_VILLAGER;
            default:return null;
        }
    }

    public static boolean checkPlayerMission(Player player,String missionName){
        File playerData = FileUtils.getPlayerData(player);
        if(playerData != null){
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerData);
            List<String > completeMission = yamlConfiguration.getStringList("CompleteMission");
            return completeMission.contains(missionName);
        }
        return false;
    }

    public static int checkPlayerMissionCompleteAmount(Player player){
        File playerData = FileUtils.getPlayerData(player);
        if(playerData != null){
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerData);
            return yamlConfiguration.getStringList("CompleteMission").size();
        }
        return 0;
    }

    public static void setPlayerMissionComplete(Player p , Quests q) {
        try {
            File file = FileUtils.getPlayerData(p);
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            List<String> l = new ArrayList<>();
            p.sendTitle("§b[§6每日任务§b]", "§a任务 §8> §b" + q.getName() + " §a已完成!", 0, 80, 40);
            if (data.getStringList("CompleteMission") != null) {
                l = data.getStringList("CompleteMission");
                l.add(q.getName());
                data.set("CompleteMission", l);
                data.save(file);
            } else {
                l.add(q.getName());
                data.set("CompleteMission", l);
                data.save(file);
            }

            if (checkPlayerMissionCompleteAmount(p) >= 3) {
                if(!data.getBoolean("ALL",false)){
                    data.set("ALL", true);
                    data.save(file);
                    new Thread(() -> {
                        try {
                            String day = String.valueOf(Integer.parseInt(new SimpleDateFormat("dd").format(new Date())));
                            Connection connection = DB.getConnection();
                            Statement statement = connection.createStatement();
                            statement.execute("use Mission");
                            statement.executeUpdate("UPDATE MissionTable SET Day" + day + " = 1 WHERE PlayerName = '" + p.getName() + "'");
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack getItem(int i){
        return itemStacks.get(i);
    }

    public static void loadItems(){
        itemStacks = new ArrayList<>();

        ItemStack one = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickaxe_meta = one.getItemMeta();

        pickaxe_meta.setDisplayName("§b[§6精锐稿§b]");
        pickaxe_meta.addEnchant(Enchantment.SILK_TOUCH,1,true);
        pickaxe_meta.addEnchant(Enchantment.DURABILITY,3,true);
        one.setItemMeta(pickaxe_meta);
        itemStacks.add(one);

        ItemStack two = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta axe_meta = two.getItemMeta();
        axe_meta.setDisplayName("§b[§6开山斧§b]");
        axe_meta.addEnchant(Enchantment.DIG_SPEED,5,true);
        axe_meta.addEnchant(Enchantment.DURABILITY,3,true);
        two.setItemMeta(axe_meta);
        itemStacks.add(two);

        ItemStack three = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmet_meta = three.getItemMeta();
        helmet_meta.setDisplayName("§b[§6海底行者§b]");
        helmet_meta.addEnchant(Enchantment.OXYGEN,10,true);
        three.setItemMeta(helmet_meta);
        itemStacks.add(three);

        ItemStack four = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta sword_meta = four.getItemMeta();
        sword_meta.setDisplayName("§b[§6收割者§b]");
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL,5,true);
        sword_meta.addEnchant(Enchantment.LOOT_BONUS_MOBS,3,true);
        sword_meta.addEnchant(Enchantment.DURABILITY,3,true);
        four.setItemMeta(sword_meta);
        itemStacks.add(four);

        ItemStack five = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmet2_meta = five.getItemMeta();
        helmet2_meta.setDisplayName("§b[§6烈焰行者§b]");
        helmet2_meta.addEnchant(Enchantment.PROTECTION_FIRE,10,true);
        five.setItemMeta(helmet2_meta);
        itemStacks.add(five);

        ItemStack six = new ItemStack(Material.IRON_BOOTS);
        ItemMeta boot_meta = six.getItemMeta();
        boot_meta.setDisplayName("§b[§高空行者§b]");
        boot_meta.addEnchant(Enchantment.PROTECTION_FALL,10,true);
        six.setItemMeta(boot_meta);
        itemStacks.add(six);

        ItemStack seven = new ItemStack(Material.MONSTER_EGG);
        SpawnEggMeta seven_meta = (SpawnEggMeta)seven.getItemMeta();
        seven_meta.setLocalizedName("生成 村民");
        seven_meta.setSpawnedType(EntityType.VILLAGER);
        seven.setItemMeta(seven_meta);
        itemStacks.add(seven);

        ItemStack eig = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta eig_meta = eig.getItemMeta();
        eig_meta.setLocalizedName("牛刷怪笼");
        List<String> eig_lore = new ArrayList<>();
        eig_lore.add("刷怪笼类型:COW:牛");
        eig_lore.add("§b> §c仅在放置时变成牛刷怪笼 §b<");
        eig_meta.setLore(eig_lore);
        eig.setItemMeta(eig_meta);
        itemStacks.add(eig);

        ItemStack nine = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta nine_meta = nine.getItemMeta();
        nine_meta.setLocalizedName("牛刷怪笼");
        List<String> nine_lore = new ArrayList<>();
        nine_lore.add("刷怪笼类型:MUSHROOM_COW:哞菇牛");
        nine_lore.add("§b> §c仅在放置时变成哞菇牛刷怪笼 §b<");
        nine_meta.setLore(nine_lore);
        nine.setItemMeta(nine_meta);
        itemStacks.add(nine);

        ItemStack eleven = new ItemStack(Material.FISHING_ROD);
        ItemMeta eleven_meta = eleven.getItemMeta();
        eleven_meta.setDisplayName("§b[§6钓竿§b]");

        eleven_meta.addEnchant(Enchantment.LUCK,3,true);
        eleven_meta.addEnchant(Enchantment.LURE,3,true);
        eleven_meta.addEnchant(Enchantment.DURABILITY,3,true);
        eleven.setItemMeta(eleven_meta);
        itemStacks.add(eleven);

        ItemStack twelve = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta twelve_meta = twelve.getItemMeta();
        twelve_meta.setLocalizedName("牛刷怪笼");
        List<String> twelve_lore = new ArrayList<>();
        twelve_lore.add("刷怪笼类型:PIG:猪");
        twelve_lore.add("§b> §c仅在放置时变成猪刷怪笼 §b<");
        twelve_meta.setLore(twelve_lore);
        twelve.setItemMeta(twelve_meta);
        itemStacks.add(twelve);

        ItemStack thirteen = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta thirteen_meta = thirteen.getItemMeta();
        thirteen_meta.setLocalizedName("守卫者刷怪笼");
        List<String> thirteen_lore = new ArrayList<>();
        thirteen_lore.add("刷怪笼类型:GUARDIAN:守卫者");
        thirteen_lore.add("§b> §c仅在放置时变成守卫者刷怪笼 §b<");
        thirteen_meta.setLore(thirteen_lore);
        thirteen.setItemMeta(thirteen_meta);
        itemStacks.add(thirteen);

        ItemStack fourteen = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta fourteen_meta = fourteen.getItemMeta();
        fourteen_meta.setLocalizedName("僵尸刷怪笼");
        List<String> fourteen_lore = new ArrayList<>();
        fourteen_lore.add("刷怪笼类型:ZOMBIE:僵尸");
        fourteen_lore.add("§b> §c仅在放置时变成僵尸刷怪笼 §b<");
        fourteen_meta.setLore(fourteen_lore);
        fourteen.setItemMeta(fourteen_meta);
        itemStacks.add(fourteen);

        ItemStack fifteen = new ItemStack(Material.BOOK);
        ItemMeta fifteen_meta = fifteen.getItemMeta();
        fifteen_meta.setDisplayName("§b[§6经验修补技能书 ★§b]");
        fifteen_meta.addEnchant(Enchantment.MENDING,5,true);
        fifteen.setItemMeta(fifteen_meta);
        itemStacks.add(fifteen);

        ItemStack sixteen = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta sixteen_meta = sixteen.getItemMeta();
        sixteen_meta.setLocalizedName("村民刷怪笼");
        List<String> sixteen_lore = new ArrayList<>();
        sixteen_lore.add("刷怪笼类型:VILLAGER:村民");
        sixteen_lore.add("§b> §c仅在放置时变成村民刷怪笼 §b<");
        sixteen_meta.setLore(sixteen_lore);
        sixteen.setItemMeta(sixteen_meta);
        itemStacks.add(sixteen);
    }

    public static void resetPlayerData(Player player, File playerData, String date) {
        try {
            YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerData);
            QuestsUtils.playerMissions.put(player, QuestsUtils.getRandomMission(3));
            playerYml.set("Date", "0");
            if (!playerYml.getString("Date").equalsIgnoreCase(date)) {
                playerYml.set("Date", date);
                playerYml.set("ALL", false);
                List<String> t = new ArrayList<>();
                for (Quests q : playerMissions.get(player)) {
                    t.add(q.getName());
                }
                playerYml.set("PlayerMissions", t);
                playerYml.set("CompleteMission", new ArrayList<>());
                playerYml.save(playerData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Quests> getRandomMission(int amount){
        List<Quests> missions = new ArrayList<>();
        int i = 0;
        while (i < amount) {
            Quests q = AllMission.get((int) (Math.random() * AllMission.size()));
            if (!missions.contains(q)) {
                missions.add(q);
                i++;
            }
        }
        return missions;
    }

    public static void refreshMission(Player player,int index){
        if(playerMissions.containsKey(player)){
            try {
                List<Quests> q = playerMissions.get(player);
                File yml = FileUtils.getPlayerData(player);
                YamlConfiguration playerData = YamlConfiguration.loadConfiguration(yml);

                List<String> missions = playerData.getStringList("PlayerMissions");

                missions.remove(index);
                q.remove(index);

                List<Quests> mis = getRandomMission(1);
                Quests quests = mis.get(0);
                q.add(quests);
                missions.add(quests.getName());
                playerMissions.remove(player);
                playerMissions.put(player,q);
                playerData.set("PlayerMissions", missions);
                playerData.save(yml);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void loadPlayerMission(Player player){
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(FileUtils.getPlayerData(player));
        List<Quests> quests = new ArrayList<>();
        for(String mission : yml.getStringList("PlayerMissions")){
            quests.add(getQuests(mission));
        }
        playerMissions.put(player,quests);
    }

    public static Quests getQuests(String name){
        for(Quests q : AllMission){
            if(q.getName().contains(name)) return q;
        }
        return null;
    }
}
