/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    
    private static final String[] DEFAULT_RESOURCE_PACKS = new String[] { "aB", "field_110449_ao", "defaultResourcePacks" };
    
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

    @Override
    public void addResourceOverride(String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, dir, file, ext);
    }

}
