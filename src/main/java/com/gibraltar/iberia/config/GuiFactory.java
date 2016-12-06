package com.gibraltar.iberia.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.gibraltar.iberia.Reference;
import com.gibraltar.iberia.proxy.CommonProxy;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft instance) {
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return IberiaGuiConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class IberiaGuiConfig extends GuiConfig {

		public IberiaGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, getAllElements(), Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(CommonProxy.config.toString()));
		}

		public static List<IConfigElement> getAllElements() {
			List<IConfigElement> list = new ArrayList();

			Set<String> categories = CommonProxy.config.getCategoryNames();
			for(String s : categories)
				if(!s.contains("."))
					list.add(new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(CommonProxy.config.getCategory(s)).getChildElements()));

			return list;
		}

	}

}