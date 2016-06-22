package com.gibraltar.iberia.proxy;

import com.gibraltar.iberia.init.HardStoneFeature;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event)
    {
    	HardStoneFeature.init();
    }

	public void init(FMLInitializationEvent event)
    {
    }
}
