package com.realgecko.xpfromharvest.config;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.ToNumberPolicy;
import com.realgecko.xpfromharvest.XPFromHarvest;
import com.realgecko.xpfromharvest.config.data.CropCondition;
import com.realgecko.xpfromharvest.config.data.CropData;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

public class CropConfig 
{
	public static final Path PATH;
	public static final Gson GSON;
	
	private static final CropData vanilla;
	
	protected static Map<String, CropData> map = new HashMap<String, CropData>();
	
	static
	{
		PATH = FMLPaths.CONFIGDIR.get().resolve(XPFromHarvest.MODID);
		GSON = new GsonBuilder()
				.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.setPrettyPrinting()
				.create();
		
		vanilla = CropData.create()
				.setCondition(Blocks.POTATOES, CropCondition.create().setTag("age", 7))
				.setCondition(Blocks.CARROTS, CropCondition.create().setTag("age", 7))
				.setCondition(Blocks.WHEAT, CropCondition.create().setTag("age", 7))
				.setCondition(Blocks.BEETROOTS, CropCondition.create().setTag("age", 3))
				.setCondition(Blocks.NETHER_WART, CropCondition.create().setTag("age", 3))
				.setCondition(Blocks.SWEET_BERRY_BUSH, 
						CropCondition.create()
							.setTag("age", 3)
							.setRightClickable(true),
						CropCondition.create()
							.setTag("age", 2)
							.setRightClickable(true)
							.setToolBlacklist(Items.BONE_MEAL))
				.setCondition(Blocks.BEEHIVE, 
						CropCondition.create()
							.setTag("honey_level", 5)
							.setRightClickable(true)
							.setToolItem(Items.SHEARS, Items.GLASS_BOTTLE))
				.setCondition(Blocks.BEE_NEST, 
						CropCondition.create()
							.setTag("honey_level", 5)
							.setRightClickable(true)
							.setToolItem(Items.SHEARS, Items.GLASS_BOTTLE));
	}
	
	
	// SETTERS
	
	public static void setCropData(String namespace, CropData data)
	{
		map.put(namespace, data);
	}

    public static boolean hasCropData(ResourceLocation rLoc)
    {
    	return map.containsKey(rLoc.getNamespace()) ? map.get(rLoc.getNamespace()).hasCrop(rLoc) : false;
    }
    
    
    // GETTERS
    
    public static CropData getCropData(String namespace) 
    {
    	return map.get(namespace);
    }
    
    public static List<CropCondition> getCropConditions(ResourceLocation rLoc)
    {
    	return map.containsKey(rLoc.getNamespace()) ? map.get(rLoc.getNamespace()).getConditions(rLoc) : new ArrayList<CropCondition>();
    }
    
    
    // DATA HANDLING
	
	public static void loadCrops() 
    {
    	File mcFile = PATH.resolve("minecraft.json").toFile();
    	if (!mcFile.exists())
    		writeData(mcFile, vanilla);
		
    	// Scan for all json files
    	File[] allFiles = PATH.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

    	for (File file : allFiles)
    	{
    		String modId = Files.getNameWithoutExtension(file.getName());
    		if (ModList.get().isLoaded(modId))
    		{
    			Optional<? extends ModContainer> opt = ModList.get().getModContainerById(modId);
    			String modName = modId;
    			String namespace = modId;
    			
    			if (opt.isPresent())
    			{
    				IModInfo info = opt.get().getModInfo();
    				modName = info.getDisplayName();
    				namespace = info.getNamespace();
    			}
    			
    			CropData data = readData(file);
    			map.put(namespace, data);
    			
    			XPFromHarvest.LOGGER.info("Loaded " + data.size() + " crops from " + modName);
    		}
    	}
    }
	
	private static CropData readData(File file) 
    {
    	CropData data = new CropData();
    	
    	try 
		{
	    	Reader reader = Files.newReader(file, Charset.defaultCharset());
			data = CropData.fromJsonTree(GSON.fromJson(reader, JsonElement.class));
		}
		catch (Exception e) 
		{
	    	XPFromHarvest.LOGGER.error("Error while loading crop configuration: " + e.getMessage());
	    	e.printStackTrace();
		}
    	
    	return data;
    }
	
    private static void writeData(File file, CropData data) 
    {
    	try 
    	{
    		File parent = file.getParentFile();
			
			if (parent != null && !parent.exists() && !parent.mkdirs())
			    throw new IllegalStateException("Couldn't create directory: " + parent);
			
	    	if (file.createNewFile()) 
	    	{
    			FileWriter writer = new FileWriter(file);
    			
    			writer.write(GSON.toJson(data.toJsonTree()));
    			writer.close();
	    	}
    	}
    	catch (Exception e)
    	{
	    	XPFromHarvest.LOGGER.error("Error while creating crop configuration: " + e.getMessage());
	    	e.printStackTrace();
    	}
    }
}
