package com.sukimod;

import com.sukimod.registry.ModEntities;
import com.sukimod.registry.ModItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SukiMod.MODID)
public class SukiMod {
    public static final String MODID = "sukimod";

    public SukiMod(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
    }
}
