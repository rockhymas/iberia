/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.gibraltar.iberia.Reference;

public class MessageRegistry {
	
	private MessageRegistry() {
	    throw new IllegalStateException("Utility class");
	  }
	
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

	private static int i = 0;

	private static void register(Class clazz, Side handlerSide) {
		network.registerMessage(clazz, clazz, i++, handlerSide);
	}

    public static void init() {
        register(MessageGetPlayerSpawn.class, Side.SERVER);
    }
}