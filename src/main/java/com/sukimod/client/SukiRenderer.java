package com.sukimod.client;

import com.sukimod.SukiMod;
import com.sukimod.entity.SukiEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SukiRenderer extends MobRenderer<SukiEntity, SukiRenderState, SukiModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SukiMod.MODID, "textures/entity/suki.png");

    public SukiRenderer(EntityRendererProvider.Context context) {
        super(context, new SukiModel(context.bakeLayer(SukiModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public SukiRenderState createRenderState() {
        return new SukiRenderState();
    }

    @Override
    public void extractRenderState(SukiEntity entity, SukiRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.sitting = entity.isInSittingPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SukiRenderState state) {
        return TEXTURE;
    }
}
