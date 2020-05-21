package org.yukina.Quests;

public enum QUESTSTYPE {
    KILL("击杀","KILL"),
    ITEM("获取物品","ITEM"),
    DIG("挖掘方块","DIG");

    private String name ;
    private String value ;

    QUESTSTYPE(String name,String value){
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return this.name;
    }

    public static QUESTSTYPE getCode(String value){
        for(QUESTSTYPE queststype : QUESTSTYPE.values()){
            if(queststype.value.equalsIgnoreCase(value)){
                return queststype;
            }
        }
        return null;
    }
}
