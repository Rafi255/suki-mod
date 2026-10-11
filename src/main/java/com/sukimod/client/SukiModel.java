package com.sukimod.client;

import com.sukimod.SukiMod;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Badan = kubus 12x12x12 (muka 🤓 di sisi depan) + 2 kaki kecil. */
public class SukiModel extends EntityModel<SukiRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SukiMod.MODID, "suki"), "main");

    private final ModelPart body;
    private final ModelPart leftFoot;
    private final ModelPart rightFoot;

    public SukiModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.leftFoot = root.getChild("left_foot");
        this.rightFoot = root.getChild("right_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f),
                PartPose.offset(0.0f, 21.0f, 0.0f));

        root.addOrReplaceChild("left_foot",
                CubeListBuilder.create().texOffs(0, 24).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                PartPose.offset(-3.0f, 21.0f, 0.0f));

        root.addOrReplaceChild("right_foot",
                CubeListBuilder.create().texOffs(0, 24).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                PartPose.offset(3.0f, 21.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(SukiRenderState state) {
        super.setupAnim(state);
        if (state.sitting) {
            this.body.y = 24.0f;       // duduk: badan nempel tanah
            this.leftFoot.visible = false;
            this.rightFoot.visible = false;
        } else {
            this.leftFoot.visible = true;
            this.rightFoot.visible = true;
            float swing = Mth.cos(state.walkAnimationPos * 0.6662f) * 1.4f * state.walkAnimationSpeed;
            this.leftFoot.xRot = swing;
            this.rightFoot.xRot = -swing;
        }
    }
}
