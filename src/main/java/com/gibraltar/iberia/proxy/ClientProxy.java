package com.gibraltar.iberia.proxy;

import com.gibraltar.iberia.init.blocks;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenders() {
		blocks.registerRenders();
	}
}
