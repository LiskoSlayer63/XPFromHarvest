package com.realgecko.xpfromharvest.config.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.realgecko.xpfromharvest.HarvestUtils;
import com.realgecko.xpfromharvest.config.CropConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CropData 
{
    protected Map<String, List<CropCondition>> map = new HashMap<String, List<CropCondition>>();
    
    
    // SETTERS
    
    public CropData setCondition(Block crop, CropCondition... conditions)
    {
    	return setCondition(crop, Arrays.asList(conditions));
    }
    
    public CropData setCondition(ResourceLocation rLoc, CropCondition... conditions)
    {
    	return setCondition(rLoc, Arrays.asList(conditions));
    }
    
    public CropData setCondition(Block crop, Iterable<CropCondition> conditions)
    {
    	return setCondition(HarvestUtils.getRegistryName(crop), conditions);
    }
    
    public CropData setCondition(ResourceLocation rLoc, Iterable<CropCondition> conditions)
    {
    	List<CropCondition> list = new ArrayList<CropCondition>();
    	for (CropCondition condition : conditions)
    		list.add(condition);
    	map.put(rLoc.toString(), list);
    	return this;
    }
    
    
    // GETTERS
    
    public int size()
    {
    	return map.size();
    }
    
    public boolean hasCrop(Block crop)
    {
    	return hasCrop(HarvestUtils.getRegistryName(crop));
    }
    
    public boolean hasCrop(ResourceLocation rLoc)
    {
    	return map.containsKey(rLoc.toString());
    }
    
    public boolean hasConditions(Block crop)
    {
    	return hasConditions(HarvestUtils.getRegistryName(crop));
    }
    
    public boolean hasConditions(ResourceLocation rLoc)
    {
    	return map.containsKey(rLoc.toString()) && map.get(rLoc.toString()).size() > 0;
    }
    
    public List<CropCondition> getConditions(Block crop)
    {
    	return getConditions(HarvestUtils.getRegistryName(crop));
    }
    
    public List<CropCondition> getConditions(ResourceLocation rLoc)
    {
    	return map.get(rLoc.toString());
    }
    
    
    // DATA HANDLING
    
    public JsonElement toJsonTree() 
    {
		Map<String, JsonElement> tmpMap = new HashMap<String, JsonElement>();
		
		for (String key : map.keySet())
		{
			List<CropCondition> value = map.get(key);
			
			if (value.size() == 1)
				tmpMap.put(key, CropConfig.GSON.toJsonTree(value.get(0)));
			else
				tmpMap.put(key, CropConfig.GSON.toJsonTree(value));
		}
		
		return CropConfig.GSON.toJsonTree(tmpMap);
    }
    
    public static CropData fromJsonTree(JsonElement json)
    {
		Map<String, JsonElement> tmpMap = new HashMap<String, JsonElement>();
    	Type type = new TypeToken<HashMap<String, JsonElement>>() {}.getType();
    	Type conditionType = new TypeToken<List<CropCondition>>(){}.getType();
    	CropData data = new CropData();
    	
    	tmpMap = CropConfig.GSON.fromJson(json, type);
    	
		for (String key : tmpMap.keySet())
		{
			JsonElement value = tmpMap.get(key);
			if (value.isJsonArray())
				data.map.put(key, CropConfig.GSON.fromJson(value, conditionType));
			else
				data.map.put(key, Arrays.asList(CropConfig.GSON.fromJson(value, CropCondition.class)));
		}
		
		return data;
    }
    
    public static CropData create()
    {
    	return new CropData();
    }
}
