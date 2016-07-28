package com.gibraltar.iberia.challenge;

import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FindYourWayChallenge extends Challenge {	
	public boolean hasSubscriptions() {
		return true;
	}

	@SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("reducedDebugInfo", "true");
    }
}