package org.yukina.Quests;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Quests{
    private String name;
    private QUESTSTYPE type;
    private EntityType entity;
    private int blockType;
    private ItemStack item;
    private int amount;
    private String desc;

    public Quests(String name, QUESTSTYPE type, Object object, int amount, String desc){
        if(object instanceof EntityType){
            this.entity = (EntityType) object;
        }else if(object instanceof ItemStack){
            this.item = (ItemStack) object;
        }else if(object instanceof Integer){
            this.blockType = (Integer) object;
        }
        this.desc = desc;
        this.name = name;
        this.type = type;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public QUESTSTYPE getType() {
        return type;
    }

    public EntityType getEntity() {
        return entity;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public String getDesc(){
        return desc;
    }

    public int getBlockType(){return blockType;}
}
