package com.gibraltar.iberia.challenge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SleepToHealChallenge extends Challenge {
    private float healAmount;

    public boolean hasSubscriptions() {
        return true;
    }

	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "HealAmount", 2.0D);
        healAmount = (float)prop.getDouble(2.0D);
    }

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
                player.heal(healAmount);
            }
        }
    }
}