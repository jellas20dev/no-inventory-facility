package com.jellas.noinventoryfacility.hud;

import com.jellas.noinventoryfacility.ItemClassificationSystem;
import com.jellas.noinventoryfacility.NoInventoryFacility;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;

/**
 * Third person counterpart of {@link TwoHandedHoldRenderer}: a player
 * carrying a heavy item (or a tower of blocks) holds it with both arms,
 * centred in front of the chest, instead of in the right hand.
 */
public class CarriedItemLayer extends RenderLayer<AvatarRenderState, PlayerModel> {

    private record Carried(ItemStackRenderState item, int blocks) {
    }

    private static final ContextKey<Carried> CARRIED = new ContextKey<>(
            Identifier.fromNamespaceAndPath(NoInventoryFacility.MODID, "carried")
    );

    /**
     * Where the hands meet with {@link CarryPose#ARM_X_ROT}, in model
     * space (pixels / 16; y points down, -z points forward).
     */
    private static final float HANDS_Y = 7.5F / 16.0F;
    private static final float HANDS_Z = -8.5F / 16.0F;

    /** FIXED display: a block is 0.5 wide. */
    private static final float BLOCK_SIZE = 0.5F;

    /** Same reduction as first person for a tower of 2+ blocks. */
    private static final float TOWER_SCALE = 0.75F;

    public CarriedItemLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    /**
     * Runs after vanilla fills the render state: switches both arms to the
     * carry pose, hides the item vanilla would draw in the hand and keeps
     * it for this layer.
     */
    public static final AvatarRenderStateModifier MODIFIER = new AvatarRenderStateModifier() {
        @Override
        public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState state) {

            state.setRenderData(CARRIED, null);

            ItemStack stack = avatar.getMainHandItem();

            if (!ItemClassificationSystem.isTwoHanded(stack)
                    || !avatar.getOffhandItem().isEmpty()
                    || avatar.isSleeping()
                    || avatar.isSwimming()) {
                return;
            }

            ItemStackRenderState item = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    item,
                    stack,
                    ItemDisplayContext.FIXED,
                    avatar.level(),
                    avatar,
                    avatar.getId()
            );

            int blocks = ItemClassificationSystem.isStackable(stack)
                    ? Math.min(stack.getCount(), ItemClassificationSystem.MAX_TOWER)
                    : 1;

            state.rightArmPose = CarryPose.get();
            state.leftArmPose = CarryPose.get();
            state.rightHandItemState.clear();
            state.leftHandItemState.clear();
            state.setRenderData(CARRIED, new Carried(item, blocks));
        }
    };

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            AvatarRenderState state,
            float yRot,
            float xRot
    ) {
        Carried carried = state.getRenderData(CARRIED);

        if (carried == null || carried.item().isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // Follow the body (bending when crouching), then go to the hands.
        this.getParentModel().body.translateAndRotate(poseStack);
        poseStack.translate(0.0F, HANDS_Y, HANDS_Z);

        // Model space is upside down; turn the item upright.
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        float scale = carried.blocks() > 1 ? TOWER_SCALE : 1.0F;
        poseStack.scale(scale, scale, scale);

        for (int i = 0; i < carried.blocks(); i++) {
            carried.item().submit(
                    poseStack,
                    collector,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    state.outlineColor
            );

            // Upright again, so +y is up: the tower grows upwards.
            poseStack.translate(0.0F, BLOCK_SIZE, 0.0F);
        }

        poseStack.popPose();
    }
}
