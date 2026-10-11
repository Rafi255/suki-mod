package com.sukimod.registry;

import com.sukimod.SukiMod;
import com.sukimod.entity.SukiEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SukiMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SukiEntity>> SUKI =
            ENTITY_TYPES.register("suki", id ->
                    EntityType.Builder.of(SukiEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 0.95f)
                            .eyeHeight(0.6f)
                            .clientTrackingRange(8)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
}
