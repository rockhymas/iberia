/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class DeathWithConsequencesChallenge extends Challenge {
    private int distanceToNewSpawnHard = 1500;
    private int distanceToNewSpawnNormal = 1000;
    private int distanceToNewSpawnEasy = 500;

    @Override
	public boolean hasSubscriptions() {
		return true;
	}

    @Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "distanceToNewSpawnHard", 1500);
        distanceToNewSpawnHard = prop.getInt(1500);
        prop = config.get(name, "distanceToNewSpawnNormal", 1000);
        distanceToNewSpawnNormal = prop.getInt(1000);
        prop = config.get(name, "distanceToNewSpawnEasy", 500);
        distanceToNewSpawnEasy = prop.getInt(500);
	}

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("keepInventory", "false");
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();

            if (player.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
                return;
            }

            WorldProvider world = player.worldObj.provider;
            BlockPos playerPos = new BlockPos(player);
            BlockPos spawnPoint = world.getSpawnPoint();
            if (!world.isSurfaceWorld()) {
                world = DimensionManager.getProvider(world.getRespawnDimension(player));
                spawnPoint = world.getSpawnPoint();
                playerPos = spawnPoint;
            }

            // Reset player spawn
            player.setSpawnChunk(null, false, world.getRespawnDimension(player));

            // Calculate new spawn point
            // Find angle to current spawn, from player location. Choose random angle
            // 180 degrees away from that angle. Go distanceToNewSpawn blocks out.
            // Rerun spawn point algorithm to find a good spot.

            // prevent division by 0
            if (spawnPoint.getX() - playerPos.getX() == 0) {
                playerPos = playerPos.west();
            }

            double angle = Math.atan((double)(playerPos.getZ() - spawnPoint.getZ()) / (double)(playerPos.getX() - spawnPoint.getX()));

            // mirror angle across Y axis if needed
            if (spawnPoint.getX() - playerPos.getX() < 0) {
                angle = Math.PI - angle;
            }

            // reverse angle 180 degrees to point from spawn away from player
            angle = angle + Math.PI;

            // randomize angle +/- 90 degrees
            Random random = new Random((int)(angle * 180 / Math.PI));
            angle = angle + (random.nextDouble() - 0.5) * Math.PI;

            int distanceToNewSpawn = 0;
            switch (player.worldObj.getDifficulty()) {
                case HARD:
                    distanceToNewSpawn = distanceToNewSpawnHard;
                    break;
                case NORMAL:
                    distanceToNewSpawn = distanceToNewSpawnNormal;
                    break;
                case EASY:
                    distanceToNewSpawn = distanceToNewSpawnEasy;
                    break;
            }

            // use angle to find new point from which to search for a spawn
            BlockPos newSpawn = spawnPoint.west((int)(Math.cos(angle) * distanceToNewSpawn));
            newSpawn = newSpawn.north((int)(Math.sin(angle) * distanceToNewSpawn));

            newSpawn = findSpawnPointNear(newSpawn, world);

            world.setSpawnPoint(newSpawn);
        }
    }

    // Same algorithm used at game start to pick a location near 0, 0
    private BlockPos findSpawnPointNear(BlockPos position, WorldProvider world) {
        BiomeProvider biomeprovider = world.getBiomeProvider();
        List<Biome> list = biomeprovider.getBiomesToSpawnIn();
        Random random = new Random(world.getSeed());
        BlockPos blockpos = biomeprovider.findBiomePosition(position.getX(), position.getZ(), 256, list, random);
        int i = position.getX();
        int j = world.getAverageGroundLevel();
        int k = position.getZ();

        if (blockpos != null)
        {
            i = blockpos.getX();
            k = blockpos.getZ();
        }
        else
        {
            System.out.println("Unable to find spawn biome");
        }

        int l = 0;

        while (!world.canCoordinateBeSpawn(i, k))
        {
            i += random.nextInt(64) - random.nextInt(64);
            k += random.nextInt(64) - random.nextInt(64);
            ++l;

            if (l == 1000)
            {
                break;
            }
        }

        return new BlockPos(i, j, k);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        MinecraftServer server = event.player.getServer();
        long i = server.worldServers[0].getWorldInfo().getWorldTime() + 24000L;
        setAllWorldTimes(server, i - i % 24000L);
        FMLLog.info("on player respawn");
    }

    private void setAllWorldTimes(MinecraftServer server, long time)
    {
        for (int i = 0; i < server.worldServers.length; ++i)
        {
            server.worldServers[i].setWorldTime((long)time);
        }
    }
}