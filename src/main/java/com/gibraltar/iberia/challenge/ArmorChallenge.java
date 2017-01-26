/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.Reference;

public class ArmorChallenge extends Challenge {
	private static final UUID ARMOR_SPEED_SLOWDOWN_ID = UUID.fromString("26265dd9-6ebf-4b88-8876-81f338f4eaa5");
    private static final AttributeModifier ARMOR_SPEED_SLOWDOWN = (new AttributeModifier(ARMOR_SPEED_SLOWDOWN_ID, "Armor speed slowdown", -0.30000001192092896D, 2)).setSaved(false);

	private long timeGuiOpened;
	private long armorDelayMs;
	private int leatherDelay;
	private int ironDelay;
	private int chainDelay;
	private int goldDelay;
	private int diamondDelay;
	private boolean quickArmorSwapEnabled;

	private int trampleCropsAtLevel;
	private int disableJumpBlockPlacementAtLevel;
	private int eachGoldUnlocksEnchantmentLevels;
	private int slowAxesAtLevel;
	private int slowPickaxesAtLevel;
	private int axeSlowdown;
	private int pickaxeSlowdown;
	private int disableShovelsAtLevel;

    private final EntityEquipmentSlot[] slotsToSwap;

    public ArmorChallenge() {
        slotsToSwap = new EntityEquipmentSlot[5];
        slotsToSwap[0] = EntityEquipmentSlot.OFFHAND;
        slotsToSwap[1] = EntityEquipmentSlot.HEAD;
        slotsToSwap[2] = EntityEquipmentSlot.CHEST;
        slotsToSwap[3] = EntityEquipmentSlot.LEGS;
        slotsToSwap[4] = EntityEquipmentSlot.FEET;
    }

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "LeatherDelay", 0);
        leatherDelay = prop.getInt(0);
		prop = config.get(name, "IronDelay", 500);
        ironDelay = prop.getInt(500);
		prop = config.get(name, "ChainDelay", 0);
        chainDelay = prop.getInt(0);
		prop = config.get(name, "GoldDelay", 0);
        goldDelay = prop.getInt(0);
		prop = config.get(name, "DiamondDelay", 1000);
        diamondDelay = prop.getInt(1000);

		prop = config.get(name, "QuickArmorSwap", true);
        quickArmorSwapEnabled = prop.getBoolean(true);

		prop = config.get(name, "trampleCropsAtLevel", 13);
        trampleCropsAtLevel = prop.getInt(13);

		prop = config.get(name, "disableJumpBlockPlacementAtLevel", 13);
        disableJumpBlockPlacementAtLevel = prop.getInt(13);

		prop = config.get(name, "eachGoldUnlocksEnchantmentLevels", 2);
        eachGoldUnlocksEnchantmentLevels = prop.getInt(2);

		prop = config.get(name, "slowAxesAtLevel", 15);
        slowAxesAtLevel = prop.getInt(15);

		prop = config.get(name, "axeSlowdown", 10);
        axeSlowdown = prop.getInt(10);

		prop = config.get(name, "slowPickaxesAtLevel", 20);
        slowPickaxesAtLevel = prop.getInt(20);

		prop = config.get(name, "pickaxeSlowdown", 5);
        pickaxeSlowdown = prop.getInt(5);

		prop = config.get(name, "disableShovelsAtLevel", 20);
        disableShovelsAtLevel = prop.getInt(20);
    }


	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
	    if (!isSlowGui(event.getGui())) {
			return;
		}

		timeGuiOpened = Minecraft.getSystemTime();
		Iterable<ItemStack> armorInventory = Minecraft.getMinecraft().player.getArmorInventoryList();
		armorDelayMs = 0;

		for (Object item : armorInventory) {
			ItemStack stack = (ItemStack) item;
			if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
				continue;
			}

			ItemArmor armor = (ItemArmor)stack.getItem();
			switch (armor.getArmorMaterial()) {
				case LEATHER:
					armorDelayMs += leatherDelay;
					break;
				case IRON:
					armorDelayMs += ironDelay;
					break;
				case CHAIN:
					armorDelayMs += chainDelay;
					break;
				case GOLD:
					armorDelayMs += goldDelay;
					break;
				case DIAMOND:
					armorDelayMs += diamondDelay;
					break;
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
	    if (!isSlowGui(event.getGui())) {
			return;
		}

		if (timeGuiOpened + armorDelayMs <= Minecraft.getSystemTime()) {
			return;
		}

		double visibility = (Minecraft.getSystemTime() - timeGuiOpened) / (double)armorDelayMs;
		int alpha = (int)Math.round(64.0D - visibility * 64.0D);

		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		int guiWidth = event.getGui().width;
		int guiHeight = event.getGui().height;
		int guiDrawnHeight = guiDrawnHeight((GuiContainer)event.getGui());
		int guiDrawnWidth = guiDrawnWidth(event.getGui());
		int guiLeft = (guiWidth - guiDrawnWidth) / 2;
		int guiTop = (guiHeight - guiDrawnHeight) / 2;
		this.draw(vertexbuffer, guiLeft, guiTop, guiDrawnWidth, guiDrawnHeight, 255, 255, 255, 128);
		int guiProgressLeft = guiLeft + (guiDrawnWidth - 16) / 2;
		int guiProgressTop = guiTop + (guiDrawnHeight - 86);
		this.draw(vertexbuffer, guiProgressLeft, guiProgressTop, 16, 2, 0, 0, 0, 128);
		this.draw(vertexbuffer, guiProgressLeft, guiProgressTop, (int)Math.round(16.0D * visibility), 2, 255, 255, 255, 255);

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();				
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
	    if (!isSlowGui(event.getGui())) {
			return;
		}

		if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
	    if (!isSlowGui(event.getGui())) {
			return;
		}

		if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime() && (Keyboard.getEventKey() != 1)) {
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	private boolean isSlowGui(GuiScreen gui) {
		return gui instanceof GuiContainer &&
			!(gui instanceof InventoryEffectRenderer) &&
			!(gui instanceof GuiMerchant) &&
			!(gui instanceof GuiScreenHorseInventory) &&
			!(gui instanceof GuiChest) &&
			!(gui instanceof GuiShulkerBox) && 
			!gui.mc.player.isCreative();
	}

	@SideOnly(Side.CLIENT)
	private int guiDrawnHeight(GuiContainer gui) {
		if (gui instanceof GuiHopper) {
			return 133;
		}
		if (gui instanceof GuiBeacon) {
			return 219;
		}
		if (gui instanceof GuiChest && ((ContainerChest)gui.inventorySlots).getLowerChestInventory().getSizeInventory() / 9 > 3) {
			return 222;
		}

		return 166;
	}

	@SideOnly(Side.CLIENT)
	private int guiDrawnWidth(GuiScreen gui) {
		if (gui instanceof GuiBeacon) {
			return 230;
		}

		return 176;
	}

	@SideOnly(Side.CLIENT)
	private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

	@SubscribeEvent
    @SideOnly(Side.CLIENT)
	public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		if (!quickArmorSwapEnabled || event.getTarget().world.isRemote) {
			return;
		}

		if (event.getEntityPlayer().isSpectator() || event.getEntityPlayer().isCreative()) {
			return;
		}

        if (!(event.getTarget() instanceof EntityArmorStand)) {
			return;
		}

		EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();
		// Shift-right click to swap armor
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			event.setCanceled(true);
			EntityPlayer player = event.getEntityPlayer();
			for (EntityEquipmentSlot slot : slotsToSwap) {
				ItemStack playerItem = player.getItemStackFromSlot(slot);
				ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
				player.setItemStackToSlot(slot, armorStandItem);
				armorStand.setItemStackToSlot(slot, playerItem);
			}
		}
		// or right click to place/remove elytra
		else {
			boolean isSmall = armorStand.isSmall();
			Vec3d vec = event.getLocalPos();
			double d4 = isSmall ? vec.yCoord * 2.0D : vec.yCoord;
			ItemStack stack = event.getItemStack();

			if (stack == null || !(stack.getItem() instanceof ItemElytra) || event.getHand() != EnumHand.MAIN_HAND) {
				return;
			}

			if (d4 >= 0.9D + (isSmall ? 0.3D : 0.0D) && d4 < 0.9D + (isSmall ? 1.0D : 0.7D)) {
				if (armorStand.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null) {
					return;
				}

				event.setCanceled(true);
				armorStand.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
				event.getEntityPlayer().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
			}
        }
    }

	private boolean isWearingGoldArmor(Iterable<ItemStack> armorInventory) {
		boolean wearingGoldArmor = false;
		for (Object item : armorInventory) {
			ItemStack stack = (ItemStack) item;
			if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
				continue;
			}

			ItemArmor armor = (ItemArmor)stack.getItem();
			if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD) {
				wearingGoldArmor = true;
				break;
			}
	    }

		return wearingGoldArmor;
	}

	@SubscribeEvent
 	@SideOnly(Side.CLIENT)
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}

		EntityPlayer player = event.player;

		boolean wearingGoldArmor = isWearingGoldArmor(player.getArmorInventoryList());
		IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        if (iattributeinstance.getModifier(ARMOR_SPEED_SLOWDOWN_ID) != null)
        {
            iattributeinstance.removeModifier(ARMOR_SPEED_SLOWDOWN);
        }

        if (wearingGoldArmor)
        {
            iattributeinstance.applyModifier(ARMOR_SPEED_SLOWDOWN);
        }

		if (player.getTotalArmorValue() >= trampleCropsAtLevel && player.onGround) {
			// If we're standing on farmland, change it to dirt
			int x = MathHelper.floor(player.posX);
            int y = MathHelper.floor(player.posY - 0.20000000298023224D);
            int z = MathHelper.floor(player.posZ);
            BlockPos blockpos = new BlockPos(x, y, z);
            IBlockState iblockstate = player.world.getBlockState(blockpos);

			if (iblockstate.getBlock() instanceof BlockFarmland) {
				player.move(MoverType.SELF, 0D, 0.1D, 0D);
				player.world.setBlockState(blockpos, Blocks.DIRT.getDefaultState());
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
		if (event.getLevel() <= 30 - eachGoldUnlocksEnchantmentLevels * 4) {
			return;
		}

		int goldArmorItems = 0;
		for (Object item : Minecraft.getMinecraft().player.getArmorInventoryList()) {
			ItemStack stack = (ItemStack) item;
			if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
				continue;
			}

			ItemArmor armor = (ItemArmor)stack.getItem();
			if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD) {
				goldArmorItems++;
			}
	    }

		event.setLevel(30 - (eachGoldUnlocksEnchantmentLevels * (4 - goldArmorItems)));
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		boolean itemIsBlock = event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemBlock;
		if (event.getEntityPlayer().onGround || !itemIsBlock) {
			return;
		}

		if (event.getEntityPlayer().getTotalArmorValue() >= disableJumpBlockPlacementAtLevel) {
			event.setUseItem(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		ItemStack heldItemStack = event.getEntityPlayer().getHeldItemMainhand();
		
		if (event.getState().getBlock().isToolEffective("axe", event.getState())) {
			if (event.getEntityPlayer().getTotalArmorValue() >= slowAxesAtLevel) {
				if (heldItemStack == null || !(heldItemStack.getItem() instanceof ItemAxe)) {
					return;
				}

				event.setNewSpeed(event.getOriginalSpeed() / axeSlowdown);
			}
		}

		if (event.getState().getBlock().isToolEffective("shovel", event.getState())) {
			if (event.getEntityPlayer().getTotalArmorValue() >= disableShovelsAtLevel) {

				if (heldItemStack == null || !(heldItemStack.getItem() instanceof ItemSpade)) {
					return;
				}

				event.setNewSpeed(event.getOriginalSpeed() / heldItemStack.getStrVsBlock(event.getState()));
			}
		}

		if (event.getState().getBlock().isToolEffective("pickaxe", event.getState())) {
			if (event.getEntityPlayer().getTotalArmorValue() >= slowPickaxesAtLevel) {
				event.setNewSpeed(event.getOriginalSpeed() / pickaxeSlowdown);
			}
		}
	}
}