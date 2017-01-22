/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gibraltar.iberia.world.IberiaWorldData;
import com.gibraltar.iberia.Reference;

public class DeathWithConsequencesChallenge extends Challenge {
    private int distanceToNewSpawnHard = 1500;
    private int distanceToNewSpawnNormal = 1000;
    private int distanceToNewSpawnEasy = 500;
    private int spawnBorderRadius = 10000;

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
        prop = config.get(name, "spawnBorderRadius", 10000);
        spawnBorderRadius = prop.getInt(10000);
	}

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("keepInventory", "false");
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();
        BlockPos playerPos = new BlockPos(player);
        World world = player.world;
        WorldProvider worldProvider = world.provider;
        int respawnDimension = worldProvider.getRespawnDimension(player);

        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return;
        }

        if (!worldProvider.isSurfaceWorld()) {
            worldProvider = DimensionManager.getProvider(respawnDimension);
            playerPos = player.getBedLocation(respawnDimension);
        }

        BlockPos spawnPoint = getPlayerIberiaSpawn(player);
        int distanceToNewSpawn = getDistanceToNextSpawn(world);

        BlockPos newSpawn = findNextSpawnPoint(worldProvider, world, spawnPoint, playerPos, distanceToNewSpawn);

        // Reset player spawn forced
        FMLLog.info("new player spawn: " + newSpawn);
        player.setSpawnChunk(newSpawn, true, respawnDimension);
        setPlayerIberiaSpawn(player, newSpawn);
    }

    private int getDistanceToNextSpawn(World world) {
        int distanceToNewSpawn = 0;
        switch (world.getDifficulty()) {
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

        return distanceToNewSpawn;
    }

    private BlockPos findNextSpawnPoint(WorldProvider worldProvider, World world, BlockPos from, BlockPos awayFrom, int distance) {
        // Calculate new spawn point
        // Find angle to current spawn, from player location. Choose random angle
        // 180 degrees away from that angle. Go distance blocks out.
        // Rerun spawn point algorithm to find a good spot.

        // prevent division by 0
        float degreeRange = 180;
        if (awayFrom == null) {
            awayFrom = from;
            degreeRange = 360;
        }

        if (from.getX() - awayFrom.getX() == 0) {
            awayFrom = awayFrom.west();
        }

        double angle = Math.atan((double)(awayFrom.getZ() - from.getZ()) / (double)(awayFrom.getX() - from.getX()));

        // mirror angle across Y axis if needed
        if (from.getX() - awayFrom.getX() < 0) {
            angle = Math.PI - angle;
        }

        // reverse angle 180 degrees to point from spawn away from player
        angle = angle + Math.PI;

        // randomize angle +/- 90 degrees
        Random random = new Random(from.hashCode());
        angle = angle + (random.nextDouble() - 0.5) * Math.PI * (degreeRange / 180);

        // use angle to find new point from which to search for a spawn
        BlockPos newSpawn = from.west((int)(Math.cos(angle) * distance));
        newSpawn = newSpawn.north((int)(Math.sin(angle) * distance));
        // bound the new spawn within the spawn border radius
        FMLLog.info("newSpawn: " + newSpawn);
        int newX = Math.floorMod(newSpawn.getX() + spawnBorderRadius, spawnBorderRadius * 2) - spawnBorderRadius;
        int newZ = Math.floorMod(newSpawn.getZ() + spawnBorderRadius, spawnBorderRadius * 2) - spawnBorderRadius;
        newSpawn = new BlockPos(newX, newSpawn.getY(), newZ);
        FMLLog.info("newSpawn bounded: " + newSpawn);

        return findSpawnPointNear(newSpawn, worldProvider, world);
    }

    // Same algorithm used at game start to pick a location near 0, 0
    private BlockPos findSpawnPointNear(BlockPos position, WorldProvider worldProvider, World world) {
        BiomeProvider biomeprovider = worldProvider.getBiomeProvider();
        List<Biome> list = biomeprovider.getBiomesToSpawnIn();
        Random random = new Random(worldProvider.getSeed());
        BlockPos blockpos = biomeprovider.findBiomePosition(position.getX(), position.getZ(), 256, list, random);
        int i = position.getX();
        int j = worldProvider.getAverageGroundLevel();
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

        while (!worldProvider.canCoordinateBeSpawn(i, k))
        {
            i += random.nextInt(64) - random.nextInt(64);
            k += random.nextInt(64) - random.nextInt(64);
            ++l;

            if (l == 1000)
            {
                break;
            }
        }

        BlockPos spawn = new BlockPos(i, j, k);
        Chunk chunk = world.getChunkFromBlockCoords(spawn);
        spawn = new BlockPos(spawn.getX(), chunk.getHeight(spawn), spawn.getZ());

        return spawn;
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // TODO: may need to get rid of this
        MinecraftServer server = event.player.getServer();
        long i = server.worlds[0].getWorldInfo().getWorldTime() + 24000L;
        setAllWorldTimes(server, i - i % 24000L);
    }

    private void setAllWorldTimes(MinecraftServer server, long time)
    {
        for (int i = 0; i < server.worlds.length; ++i)
        {
            server.worlds[i].setWorldTime((long)time);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Ensure that player iberia spawn is set to world iberia spawn
        EntityPlayer player = event.player;
        World world = player.world;

        NBTTagCompound iberiaData = getPlayerIberiaData(player);
        if (iberiaData.hasKey("SpawnX")) {
            FMLLog.info("player iberia spawn already set");
            return;
        }

        FMLLog.info("empty: " + player.inventory.mainInventory.get(0).isEmpty());

        int respawnDimension = world.provider.getRespawnDimension((EntityPlayerMP)player);
        BlockPos spawn = getPlayerIberiaSpawn(player); // will get world iberia spawn as default
        Chunk chunk = world.getChunkFromBlockCoords(spawn);
        spawn = new BlockPos(spawn.getX(), chunk.getHeight(spawn), spawn.getZ());
        setPlayerIberiaSpawn(player, spawn);
        FMLLog.info("player iberia spawn set to: " + spawn);
        player.setSpawnChunk(spawn, true, respawnDimension);

        // Teleport player to their spawn
        Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
        ((EntityPlayerMP)player).connection.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), player.rotationYaw, player.rotationPitch, set);

        // Move world iberia spawn
        int distanceToNewSpawn = getDistanceToNextSpawn(world);
        spawn = findNextSpawnPoint(world.provider, world, spawn, null, distanceToNewSpawn);
        FMLLog.info("new iberia world spawn: " + spawn);
        IberiaWorldData.get(world).setSpawn(spawn);
    }

    private void setPlayerIberiaSpawn(EntityPlayer player, BlockPos spawn) {
        NBTTagCompound iberiaData = getPlayerIberiaData(player);
        
        iberiaData.setInteger("SpawnX", spawn.getX());
        iberiaData.setInteger("SpawnY", spawn.getY());
        iberiaData.setInteger("SpawnZ", spawn.getZ());
    }

    public static BlockPos getPlayerIberiaSpawn(EntityPlayer player) {
        NBTTagCompound iberiaData = getPlayerIberiaData(player);

        if (!iberiaData.hasKey("SpawnX", 99) || !iberiaData.hasKey("SpawnY", 99) || !iberiaData.hasKey("SpawnZ", 99)) {
            BlockPos spawn = IberiaWorldData.get(player.world).getSpawn();
            iberiaData.setInteger("SpawnX", spawn.getX());
            iberiaData.setInteger("SpawnY", spawn.getY());
            iberiaData.setInteger("SpawnZ", spawn.getZ());
            return spawn;
        }

        return new BlockPos(iberiaData.getInteger("SpawnX"), iberiaData.getInteger("SpawnY"), iberiaData.getInteger("SpawnZ"));
    }

    public static NBTTagCompound getPlayerIberiaData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        NBTTagCompound modData;
        NBTTagCompound iberiaData;
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }

        modData = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (!modData.hasKey(Reference.MODID)) {
            modData.setTag(Reference.MODID, new NBTTagCompound());
        }

        iberiaData = modData.getCompoundTag(Reference.MODID);

        return iberiaData;
    }
}