package com.jellas.noinventoryfacility.hud;

import com.jellas.noinventoryfacility.ItemClassificationSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

/**
 * First person: heavy (two-handed) items are held with both arms, the item
 * centred between them. The arm pose is the one vanilla uses for a map held
 * with both hands; the item takes the map's place.
 */
public class TwoHandedHoldRenderer {

    /** Height of the held item relative to the vanilla map position. */
    private static final float ITEM_Y_OFFSET = 0.05F;

    /** Pushes the item away from the camera so it sits between the hands. */
    private static final float ITEM_Z_OFFSET = -0.1F;

    /** FIXED display is 0.5 block wide; the map the arms grip is ~0.76. */
    private static final float ITEM_SCALE = 1.5F;

    /** Extra scale for a tower of 2+ blocks. */
    private static final float TOWER_SCALE = 0.75F;

    /** Height of a block model in the FIXED display (scale 0.5). */
    private static final float FIXED_BLOCK_HEIGHT = 0.5F;

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        ItemStack stack = event.getItemStack();

        if (!ItemClassificationSystem.isTwoHanded(stack)
                || !player.getOffhandItem().isEmpty()) {
            return;
        }

        event.setCanceled(true);

        render(
                minecraft,
                player,
                stack,
                event.getPoseStack(),
                event.getSubmitNodeCollector(),
                event.getPackedLight(),
                event.getInterpolatedPitch(),
                event.getEquipProgress(),
                event.getSwingProgress()
        );
    }

    /** Mirrors ItemInHandRenderer#renderTwoHandedMap. */
    private static void render(
            Minecraft minecraft,
            LocalPlayer player,
            ItemStack stack,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            float xRot,
            float inverseArmHeight,
            float attack
    ) {
        poseStack.pushPose();

        float sqrtAttack = Mth.sqrt(attack);
        float ySwing = -0.2F * Mth.sin(attack * Mth.PI);
        float zSwing = -0.4F * Mth.sin(sqrtAttack * Mth.PI);
        poseStack.translate(0.0F, -ySwing / 2.0F, zSwing);

        float tilt = calculateTilt(xRot);
        poseStack.translate(
                0.0F,
                0.04F + inverseArmHeight * -1.2F + tilt * -0.5F,
                -0.72F
        );
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt * -85.0F));

        if (!player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            renderArm(minecraft, player, poseStack, collector, light, HumanoidArm.RIGHT);
            renderArm(minecraft, player, poseStack, collector, light, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        // Undo the map tilt so the item stays upright between the hands.
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt * 85.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(
                Mth.sin(sqrtAttack * Mth.PI) * 20.0F
        ));
        poseStack.translate(0.0F, ITEM_Y_OFFSET, ITEM_Z_OFFSET);

        // Building blocks carried as a tower: one model per block, stacked
        // upwards, a bit smaller so the tower does not cover the screen.
        int blocks = ItemClassificationSystem.isStackable(stack)
                ? Math.min(stack.getCount(), ItemClassificationSystem.MAX_TOWER)
                : 1;

        float scale = blocks > 1 ? ITEM_SCALE * TOWER_SCALE : ITEM_SCALE;
        poseStack.scale(scale, scale, scale);

        for (int i = 0; i < blocks; i++) {

            minecraft.gameRenderer.itemInHandRenderer.renderItem(
                    player,
                    stack,
                    ItemDisplayContext.FIXED,
                    poseStack,
                    collector,
                    light
            );

            poseStack.translate(0.0F, FIXED_BLOCK_HEIGHT, 0.0F);
        }

        poseStack.popPose();
    }

    private static float calculateTilt(float xRot) {
        float tilt = Mth.clamp(1.0F - xRot / 45.0F + 0.1F, 0.0F, 1.0F);
        return -Mth.cos(tilt * Mth.PI) * 0.5F + 0.5F;
    }

    /** Mirrors ItemInHandRenderer#renderMapHand. */
    private static void renderArm(
            Minecraft minecraft,
            LocalPlayer player,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            HumanoidArm arm
    ) {
        AvatarRenderer<AbstractClientPlayer> renderer =
                minecraft.getEntityRenderDispatcher().getPlayerRenderer(player);

        poseStack.pushPose();

        float invert = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(invert * -41.0F));
        poseStack.translate(invert * 0.3F, -1.1F, 0.45F);

        Identifier skin = player.getSkin().body().texturePath();

        if (arm == HumanoidArm.RIGHT) {
            renderer.renderRightHand(poseStack, collector, light, skin,
                    player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE), player);
        } else {
            renderer.renderLeftHand(poseStack, collector, light, skin,
                    player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE), player);
        }

        poseStack.popPose();
    }
}
