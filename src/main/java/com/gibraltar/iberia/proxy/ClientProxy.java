/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.proxy;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.gibraltar.iberia.client.ResourceProxy;

public class ClientProxy extends CommonProxy {
    
    private static final String[] DEFAULT_RESOURCE_PACKS = new String[] { "aB", "field_110449_ao", "defaultResourcePacks" };
    ResourceProxy resourceProxy;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        forEachChallenge(challenge -> {
            if (challenge.enabled) {
                challenge.preInitClient(event);
            }
        });
    }

    @Override
    public void hookResourceProxy() {
        List<IResourcePack> packs = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), DEFAULT_RESOURCE_PACKS);
        resourceProxy = new ResourceProxy();
        packs.add(resourceProxy);
    }
}
