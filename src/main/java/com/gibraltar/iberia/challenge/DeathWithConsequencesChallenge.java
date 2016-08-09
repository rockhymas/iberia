package com.gibraltar.iberia.challenge;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class DeathWithConsequencesChallenge extends Challenge {
    private int distanceToNewSpawn = 1000;

    @Override
	public boolean hasSubscriptions() {
		return true;
	}

    @Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "distanceToNewSpawn", 1000);
        distanceToNewSpawn = prop.getInt(1000);
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

            // Calculate new spawn point
            // Find angle to current spawn, from player location. Choose random angle
            // 180 degrees away from that angle. Go distanceToNewSpawn blocks out.
            // Rerun spawn point algorithm to find a good spot.
            WorldProvider world = player.worldObj.provider;
            BlockPos spawnPoint = world.getSpawnPoint();
            BlockPos playerPos = new BlockPos(player);
            if (!world.isSurfaceWorld()) {
                world = DimensionManager.getProvider(world.getRespawnDimension(player));
                spawnPoint = world.getSpawnPoint();
                playerPos = spawnPoint;
            }

            player.setSpawnChunk(null, false, world.getRespawnDimension(player));

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

            // use angle to find new point from which to search for a spawn
            BlockPos newSpawn = spawnPoint.west((int)(Math.cos(angle) * distanceToNewSpawn));
            newSpawn = newSpawn.north((int)(Math.sin(angle) * distanceToNewSpawn));

            newSpawn = findSpawnPointNear(newSpawn, world);

            world.setSpawnPoint(newSpawn);
        }
    }

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
        setAllWorldTimes(server, 0);
    }

    private void setAllWorldTimes(MinecraftServer server, int time)
    {
        for (int i = 0; i < server.worldServers.length; ++i)
        {
            server.worldServers[i].setWorldTime((long)time);
        }
    }
}