package com.gibraltar.iberia.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SleepToHealWatcher {
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("naturalRegeneration", "false");
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!event.wakeImmediately()) {
            EntityPlayer player = event.getEntityPlayer();
            if (player.getHealth() < player.getMaxHealth() && !player.getFoodStats().needFood()) {
                player.heal(2.0F);
            }
        }
    }
}