package com.gibraltar.iberia.proxy;

import com.gibraltar.iberia.feature.HardStoneFeature;
import com.gibraltar.iberia.feature.ReducedDebugInfoFeature;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event)
    {
    	HardStoneFeature.init();
        ReducedDebugInfoFeature.init();
    }

	public void init(FMLInitializationEvent event)
    {
    }
}
