/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gibraltar.iberia.items.ItemPersonalCompass;
import com.gibraltar.iberia.network.MessageRegistry;
import com.gibraltar.iberia.network.MessageGetPlayerSpawn;

public class FindYourWayChallenge extends Challenge {	
    public static Item compassPersonal;

    @Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		compassPersonal = new ItemPersonalCompass();
	}

    @Override
    public boolean hasSubscriptions() {
		return true;
	}

	@SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("reducedDebugInfo", "true");
    }

    @SubscribeEvent
    public void onPlayerSpawnSet(PlayerSetSpawnEvent event) {
        if (!event.isForced()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() instanceof ItemPersonalCompass) {
            BlockPos pos = event.player.getBedLocation(0);
            ItemPersonalCompass.setCompassSpawn(event.crafting, pos.getX(), pos.getZ());
        }
    }
}