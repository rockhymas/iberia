/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

import com.gibraltar.iberia.items.ItemPersonalCompass;

public class MessageGetPlayerSpawn implements IMessage, IMessageHandler<MessageGetPlayerSpawn, IMessage> {
    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(MessageGetPlayerSpawn message, MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final WorldServer world = (WorldServer) player.world;
        world.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ItemStack compass = getCompassWithoutSpawn(player);
                if (compass != null) {
                    BlockPos pos = player.getBedLocation(0);
                    if (pos != null) {
                        ItemPersonalCompass.setCompassSpawn(compass, pos.getX(), pos.getZ());
                    }
                }
            }
        });

        return null;
    }

	public static ItemStack getCompassWithoutSpawn(EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack != null && stack.getItem() instanceof ItemPersonalCompass && ItemPersonalCompass.getCompassSpawn(stack) == null) {
                return stack;
            }
        }

		return null;
	}

}