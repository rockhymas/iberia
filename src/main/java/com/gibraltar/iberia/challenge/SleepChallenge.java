/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.gibraltar.iberia.world.IberiaWorldData;
import com.gibraltar.iberia.Reference;

public class SleepChallenge extends Challenge {
    private float probabilityToWakeUnprotected;
    private float probabilityToWakeProtected;

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);
    
        Property prop = config.get(name, "probabilityToWakeUnprotected", 0.5D);
        probabilityToWakeUnprotected = (float)prop.getDouble(0.5D);
        prop = config.get(name, "probabilityToWakeProtected", 0.01D);
        probabilityToWakeProtected = (float)prop.getDouble(0.01D);
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

        if (!player.world.isDaytime() && hasPlayerSleptTonight(player, time)) {
            player.sendStatusMessage(new TextComponentTranslation("iberia.tile.bed.alreadySlept", 0), true);
            event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
        }
        setPlayerSleptWell(player, checkIfPlayerSleptWell(player, event.getPos()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.END || event.world.isDaytime()) {
            return;
        }

        WorldServer world = (WorldServer) event.world;
        long time = world.getWorldTime();

        long lastWorldSleep = IberiaWorldData.get(world).getLastSleepTime();
        if (lastWorldSleep < time && lastWorldSleep > time - 12000) {
            // Everyone has slept tonight alreadySlept
            return;
        }

        if (!world.playerEntities.isEmpty())
        {
            int spectators = 0;
            int slept = 0;
            boolean allSleptWell = true;

            for (EntityPlayer entityplayer : world.playerEntities)
            {
                if (entityplayer.isSpectator())
                {
                    ++spectators;
                }
                else if (hasPlayerSleptTonight(entityplayer, time))
                {
                    if (entityplayer.isPlayerFullyAsleep()) {
                        setPlayerSleptWell(entityplayer, checkIfPlayerSleptWell(entityplayer, new BlockPos(entityplayer)));
                    }
                    allSleptWell = allSleptWell && getPlayerSleptWell(entityplayer);
                    ++slept;
                }
            }

            boolean allPlayersSlept = slept > 0 && slept >= world.playerEntities.size() - spectators;

            if (allPlayersSlept) {
                if (world.getGameRules().getBoolean("doDaylightCycle"))
                {
                    long i = world.getWorldTime() + 24000L;
                    long nextDayBreak = i - i % 24000L;
                    long wakeTime = allSleptWell ? nextDayBreak : (nextDayBreak - world.getWorldTime()) / 2 + world.getWorldTime();
                    world.setWorldTime(wakeTime);
                    IberiaWorldData.get(world).setLastSleepTime(wakeTime);
                }

                for (EntityPlayer entityplayer : world.playerEntities)
                {
                    if (entityplayer.isPlayerSleeping())
                    {
                        entityplayer.wakeUpPlayer(false, false, true);
                        if (!allSleptWell) {
                            if (getPlayerSleptWell(entityplayer)) {
                                entityplayer.sendStatusMessage(new TextComponentTranslation("iberia.tile.bed.badDream", 0), true);
                            }
                            else {
                                entityplayer.sendStatusMessage(new TextComponentTranslation("iberia.tile.bed.wokenByZombies", 0), true);
                            }
                        }
                    }
                }

                if (world.getGameRules().getBoolean("doWeatherCycle") && allSleptWell)
                {
                    world.provider.resetRainAndThunder();
                }
            }
        }
    }

    private boolean checkIfPlayerSleptWell(EntityPlayer player, BlockPos pos) {
        boolean bedCovered = true;
        for (int i = -1; i <= 1 && bedCovered; i++)
            for (int k = -1; k <= 1 && bedCovered; k++)
                if (player.world.canSeeSky(pos.add(i, 2, k)))
                    bedCovered = false;

        boolean spawnableBlock = false;
        for (int i = -5; i <= 5 && !spawnableBlock; i++)
            for (int j = -3; j <= 3 && !spawnableBlock; j++)
                for (int k = -5; k <= 5 && !spawnableBlock; k++) {
                    BlockPos checkPos = pos.add(i, j, k);
                    BlockPos belowPos = checkPos.down();
                    IBlockState stateBelow = player.world.getBlockState(belowPos);
                    if (player.world.getLightFor(EnumSkyBlock.BLOCK, checkPos) < 8 &&
                        stateBelow.getBlock().canCreatureSpawn(stateBelow, player.world, belowPos, EntityLiving.SpawnPlacementType.ON_GROUND) &&
                        WorldEntitySpawner.isValidEmptySpawnBlock(player.world.getBlockState(checkPos)) &&
                        WorldEntitySpawner.isValidEmptySpawnBlock(player.world.getBlockState(checkPos.up()))) {
                            spawnableBlock = true;
                        }
                }

        float probabilityToWake = bedCovered && !spawnableBlock ? probabilityToWakeProtected : probabilityToWakeUnprotected;
        double rand = new Random().nextDouble();
        return rand > probabilityToWake;
    }

    private boolean hasPlayerSleptTonight(EntityPlayer player, long time) {
        long playerSleepTime = getPlayerSleepTime(player);
        return (playerSleepTime <= time && playerSleepTime > time - 12000L) || player.isPlayerFullyAsleep();
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

    private void setPlayerSleptWell(EntityPlayer player, boolean sleptWell) {
        NBTTagCompound iberiaData = getPlayerIberiaNPData(player);
        
        iberiaData.setBoolean("SleptWell", sleptWell);
    }

    private boolean getPlayerSleptWell(EntityPlayer player) {
        NBTTagCompound iberiaData = getPlayerIberiaNPData(player);

        if (!iberiaData.hasKey("SleptWell", 99)) {
            iberiaData.setBoolean("SleptWell", true);
            return true;
        }

        return iberiaData.getBoolean("SleptWell");
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