/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import com.gibraltar.iberia.items.ItemPersonalCompass;
import com.gibraltar.iberia.challenge.SpawnChallenge;
import com.gibraltar.iberia.Reference;

public class NavigationChallenge extends Challenge {	
    private static Item compassPersonal;

    @Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		compassPersonal = new ItemPersonalCompass();
		RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(new ResourceLocation("minecraft:compass"));
	}

    @Override
	public void preInitClient(FMLPreInitializationEvent event) {
		super.preInitClient(event);
        ModelLoader.setCustomModelResourceLocation(compassPersonal, 0, new ModelResourceLocation("iberia:compass_personal", "inventory"));
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
    @SideOnly(Side.SERVER)
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() instanceof ItemPersonalCompass) {
            BlockPos pos = SpawnChallenge.getPlayerIberiaSpawn(event.player);
            ItemPersonalCompass.setCompassSpawn(event.crafting, pos.getX(), pos.getZ());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        FMLLog.info("interacting with entity");
        if (!(event.getTarget() instanceof EntityVillager)) {
            return;
        }

        FMLLog.info("interacting with villager");
        replaceCompassInVillagerTrades((EntityVillager)event.getTarget());
    }

    private void replaceCompassInVillagerTrades(EntityVillager villager) {
        // get NBT data
        NBTTagCompound data = new NBTTagCompound();
        villager.writeEntityToNBT(data);

        if (data.hasKey("Offers", 10))
        {
            FMLLog.info("checking villager trades");
            NBTTagCompound offers = data.getCompoundTag("Offers");
            MerchantRecipeList buyingList = new MerchantRecipeList(offers);

            boolean modified = false;
            for (MerchantRecipe r : buyingList) {
                MerchantRecipe newRecipe = replaceCompassInRecipe(r);
                if (newRecipe != null) {
                    r.readFromTags(newRecipe.writeToTags());
                    modified = true;
                }
            }

            if (modified) {
                FMLLog.info("replacing villager trades");
                data.setTag("Offers", buyingList.getRecipiesAsTags());
                villager.readEntityFromNBT(data);
            }
        }
    }

    private MerchantRecipe replaceCompassInRecipe(MerchantRecipe recipe) {
        ItemStack buy1 = recipe.getItemToBuy();
        ItemStack buy2 = recipe.getSecondItemToBuy();
        ItemStack sell = recipe.getItemToSell();
        boolean modified = false;

        if (buy1 != null && buy1.getItem() instanceof ItemCompass) {
            modified = true;
            buy1 = new ItemStack(compassPersonal);
        }
        if (buy2 != null && buy2.getItem() instanceof ItemCompass) {
            modified = true;
            buy2 = new ItemStack(compassPersonal);
        }
        if (sell != null && sell.getItem() instanceof ItemCompass) {
            modified = true;
            sell = new ItemStack(compassPersonal);
        }

        if (modified) {
            return new MerchantRecipe(buy1, buy2, sell);
        }

        return null;
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        // Replace compass in loot table
        LootTable loot = event.getTable();
        // Go through "main" and "pool<n>" till we get a null LootPool
        LootPool pool = loot.getPool("main");
        int i = 0;
        if (pool == null) {
            pool = loot.getPool("pool0");
        }
        for (; pool != null; pool = loot.getPool("pool" + i)) {
            LootEntry entry = pool.getEntry("minecraft:compass");
            if (entry != null) {
                FMLLog.info("found compass entry");

                pool.removeEntry("minecraft:compass");
                LootEntry newEntry = new LootEntryItem(compassPersonal, 1, 0, new LootFunction[0], new LootCondition[0], Reference.MODID + ":compass_personal");
                pool.addEntry(newEntry);
            }
            i++;
        }
    }
}