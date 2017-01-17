/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gibraltar.iberia.Reference;

public class SleepToHealChallenge extends Challenge {
    private float healAmountHard;
    private float healAmountNormal;
    private float healAmountEasy;

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "HealAmountHard", 2.0D);
        healAmountHard = (float)prop.getDouble(2.0D);
		prop = config.get(name, "HealAmountNormal", 4.0D);
        healAmountNormal = (float)prop.getDouble(4.0D);
		prop = config.get(name, "HealAmountEasy", 6.0D);
        healAmountEasy = (float)prop.getDouble(6.0D);
    }

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("naturalRegeneration", "false");
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) {
            return;
        }

        if (player.isPlayerFullyAsleep()) {
            // mark last time player slept
            MinecraftServer server = player.getServer();
            long time = server.worlds[0].getWorldInfo().getWorldTime();
            setPlayerSleepTime(player, time);
            if (player.getHealth() < player.getMaxHealth() && !player.getFoodStats().needFood()) {
                switch (player.world.getDifficulty()) {
                    case HARD:
                        player.heal(healAmountHard);
                        break;
                    case NORMAL:
                        player.heal(healAmountNormal);
                        break;
                    case EASY:
                        player.heal(healAmountEasy);
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) {
            return;
        }

        MinecraftServer server = player.getServer();
        long time = server.worlds[0].getWorldInfo().getWorldTime();

        if (!player.world.isDaytime() && getPlayerSleepTime(player) > time - 12000L) {
            player.sendStatusMessage(new TextComponentTranslation("iberia.tile.bed.alreadySlept", new Object[0]), true);
            event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.END) {
            return;
        }

        if (event.world.getTotalWorldTime() % 20 != 0 || event.world.isDaytime()) { // check once a second
            return;
        }

        WorldServer world = (WorldServer) event.world;
        long time = world.getWorldTime();

        if (!world.playerEntities.isEmpty())
        {
            int spectators = 0;
            int slept = 0;

            for (EntityPlayer entityplayer : world.playerEntities)
            {
                if (entityplayer.isSpectator())
                {
                    ++spectators;
                }
                else if (hasPlayerSleptTonight(entityplayer, time))
                {
                    ++slept;
                }
            }

            boolean allPlayersSlept = slept > 0 && slept >= world.playerEntities.size() - spectators;
            FMLLog.info("all players slept: " + allPlayersSlept);

            if (allPlayersSlept) {
                if (world.getGameRules().getBoolean("doDaylightCycle"))
                {
                    long i = world.getWorldTime() + 24000L;
                    world.setWorldTime(i - i % 24000L);
                }

                for (EntityPlayer entityplayer : world.playerEntities)
                {
                    if (entityplayer.isPlayerSleeping())
                    {
                        entityplayer.wakeUpPlayer(false, false, true);
                    }
                }

                if (world.getGameRules().getBoolean("doWeatherCycle"))
                {
                    world.provider.resetRainAndThunder();
                }
            }
        }
    }

    private boolean hasPlayerSleptTonight(EntityPlayer player, long time) {
        FMLLog.info("player slept time: " + getPlayerSleepTime(player) + " time: " + time);
        return getPlayerSleepTime(player) > time - 12000L || player.isPlayerFullyAsleep();
    }

    private void setPlayerSleepTime(EntityPlayer player, long time) {
        NBTTagCompound iberiaData = getPlayerIberiaNPData(player);
        
        iberiaData.setLong("SleptAt", time);
    }

    private long getPlayerSleepTime(EntityPlayer player) {
        NBTTagCompound iberiaData = getPlayerIberiaNPData(player);

        if (!iberiaData.hasKey("SleptAt", 99)) {
            iberiaData.setLong("SleptAt", 0);
            return 0;
        }

        return iberiaData.getLong("SleptAt");
    }

    public NBTTagCompound getPlayerIberiaNPData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        NBTTagCompound iberiaData;
        if (!entityData.hasKey(Reference.MODID)) {
            entityData.setTag(Reference.MODID, new NBTTagCompound());
        }

        iberiaData = entityData.getCompoundTag(Reference.MODID);

        return iberiaData;
    }
}