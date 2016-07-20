package com.gibraltar.iberia.proxy;

import com.gibraltar.iberia.feature.HardStoneFeature;
import com.gibraltar.iberia.feature.ReducedDebugInfoFeature;
import com.gibraltar.iberia.feature.SleepToHealFeature;
import com.gibraltar.iberia.feature.SlowGuiAccessFeature;
import com.gibraltar.iberia.feature.QuickArmorSwapFeature;
import com.gibraltar.iberia.feature.ArmorStandElytraRenderFeature;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event)
    {
    	HardStoneFeature.init();
        ReducedDebugInfoFeature.init();
        SleepToHealFeature.init();
        SlowGuiAccessFeature.init();
        QuickArmorSwapFeature.init();
        ArmorStandElytraRenderFeature.init();
    }

	public void init(FMLInitializationEvent event)
    {
    }
}
