package com.realgecko.xpfromharvest;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.realgecko.xpfromharvest.config.CropConfig;
import com.realgecko.xpfromharvest.config.HarvestConfig;
import com.realgecko.xpfromharvest.handlers.BlockBreakHandler;
import com.realgecko.xpfromharvest.handlers.BlockRightClickHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(XPFromHarvest.MODID)
public class XPFromHarvest 
{
    public static final String MODID = "xpfromharvest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XPFromHarvest() 
    {
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HarvestConfig.COMMON_CONFIG);
    	
        MinecraftForge.EVENT_BUS.register(BlockBreakHandler.class);
        MinecraftForge.EVENT_BUS.register(BlockRightClickHandler.class);
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadConfiguration);
    }

    public void loadConfiguration(ModConfigEvent.Loading event) 
    {
        CropConfig.loadCrops();
    }
}
