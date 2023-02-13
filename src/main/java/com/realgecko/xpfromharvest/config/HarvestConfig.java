package com.realgecko.xpfromharvest.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class HarvestConfig 
{
    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.DoubleValue xpChance;
    public static ForgeConfigSpec.IntValue xpAmount;
    public static ForgeConfigSpec.BooleanValue useSimpleHarvest;
    public static ForgeConfigSpec.BooleanValue useCuriosity;

    static 
    {
    	ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
    	
        xpChance = configBuilder
                .comment("Chance in % of XP orb spawning after harvest done")
                .defineInRange("Chance", 1.0, 0.0, 1.0);
        xpAmount = configBuilder
                .comment("Amount of XP given")
                .defineInRange("XP Amount", 1, 1, Integer.MAX_VALUE);
        useSimpleHarvest = configBuilder
                .comment("Enable simple harvesting and replanting with right click")
                .define("Simple Harvest", false);
        useCuriosity = configBuilder
                .comment("Curiosity Mode: sneak + right click with on block to get info in chat")
                .define("Curiosity", false);
        COMMON_CONFIG = configBuilder.build();
    }
}
