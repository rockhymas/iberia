package com.gibraltar.iberia.client;	

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.FMLLog;

import com.gibraltar.iberia.iberia;

public class ResourceProxy extends AbstractResourcePack {

	private static final String IBERIA = "iberia";
	private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of(IBERIA);

	private static final String BARE_FORMAT = "assets/%s/%s/%s/%s.%s";
	private static final String OVERRIDE_FORMAT = "/assets/%s/%s/%s/overrides/%s.%s";

	private static final Map<String, String> overrides = new HashMap();

	public ResourceProxy() {
		super(Loader.instance().activeModContainer().getSource());
		overrides.put("pack.mcmeta", "/proxypack.mcmeta");
	}

	@Override
	public Set<String> getResourceDomains() {
		return RESOURCE_DOMAINS;
	}

	@Override
	protected InputStream getInputStreamByName(String name) throws IOException {
		if(name == null)
			return null;
		
        FMLLog.info(name);
        if (name.startsWith("assets/iberia/blockstates/") && name.endsWith(".json")) {
            FMLLog.info("prefix match");
            String originalName = name.substring(26, name.length() - 5);
            FMLLog.info(originalName);
            String rl = "blockstates/" + originalName + ".json";
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            FMLLog.info("manager " + manager);
            ResourceLocation location = new ResourceLocation(rl);
            FMLLog.info("location: " + location);
            IResource resource = null;
            try {
                resource = manager.getResource(location);
            }
            catch (Exception e) {
                FMLLog.info("exception: " + e);
            }
            FMLLog.info("resource: " + resource);
            InputStream stream = resource.getInputStream();
            FMLLog.info("stream: " + stream);
            FMLLog.info("available: " + stream.available());
            return stream;
        }
        else if (name.startsWith("assets/iberia/lang/en_us.lang")) {
            return iberia.class.getResourceAsStream("assets/iberia/lang/en_US.lang");
        }
        else if (name.startsWith("assets/iberia/models/block/") && name.endsWith(".json")) {
            FMLLog.info("prefix match");
            String originalName = name.substring(27, name.length() - 5);
            FMLLog.info(originalName);
            String rl = "models/block/" + originalName + ".json";
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            FMLLog.info("manager " + manager);
            ResourceLocation location = new ResourceLocation(rl);
            FMLLog.info("location: " + location);
            IResource resource = null;
            try {
                resource = manager.getResource(location);
            }
            catch (Exception e) {
                FMLLog.info("exception: " + e);
            }
            FMLLog.info("resource: " + resource);
            InputStream stream = resource.getInputStream();
            FMLLog.info("stream: " + stream);
            FMLLog.info("available: " + stream.available());
            return stream;
        }

        return null;
	}

	@Override
	protected boolean hasResourceName(String name) {
        FMLLog.info(name);
        if (name.startsWith("assets/iberia") && !name.startsWith("assets/iberia/lang/")) {
            return true;
        }
		return false;
	}

	@Override
	protected void logNameNotLowercase(String name) {
		// NO-OP
	}

	@Override
	public String getPackName() {
		return "iberia-hard-stone";
	}

}