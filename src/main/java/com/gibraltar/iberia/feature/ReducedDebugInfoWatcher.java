package com.gibraltar.iberia.feature;

import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReducedDebugInfoWatcher {
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("reducedDebugInfo", "true");
    }
}