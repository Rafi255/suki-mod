package com.sukimod.registry;

import com.sukimod.SukiMod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SukiMod.MODID);

    public static final DeferredItem<Item> SUKI_SPAWN_EGG =
            ITEMS.registerItem("suki_spawn_egg",
                    props -> new SpawnEggItem(props.spawnEgg(ModEntities.SUKI.get())));
}
