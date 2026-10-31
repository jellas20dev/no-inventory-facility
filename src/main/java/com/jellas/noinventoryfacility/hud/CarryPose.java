package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

/**
 * Third person arm pose for heavy items: both arms forward and down, as if
 * carrying a box against the chest. Added to {@link HumanoidModel.ArmPose}
 * through META-INF/enumextensions.json.
 */
public final class CarryPose {

    /** Arms forward, a bit below horizontal (-PI/2 is straight ahead). */
    static final float ARM_X_ROT = -1.0F;

    public static final EnumProxy<HumanoidModel.ArmPose> PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            true,  // two-handed
            true,  // affects the off-hand pose
            (IArmPoseTransformer) CarryPose::applyTransform
    );

    private CarryPose() {
    }

    public static HumanoidModel.ArmPose get() {
        return PROXY.getValue();
    }

    private static void applyTransform(
            HumanoidModel<?> model,
            HumanoidRenderState state,
            HumanoidArm arm
    ) {
        // The pose affects the off-hand pose, so vanilla only calls this for
        // the main arm and skips the other one: pose both arms here (like
        // BOW_AND_ARROW does).
        poseArm(model.rightArm);
        poseArm(model.leftArm);
    }

    /** Absolute angles: no walking swing while carrying. */
    private static void poseArm(ModelPart part) {
        part.xRot = ARM_X_ROT;
        part.yRot = 0.0F;
        part.zRot = 0.0F;
    }
}
