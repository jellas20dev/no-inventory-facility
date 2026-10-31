package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.network.PickupItemPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Optional;

public class ItemDetectorSystem {

    /**
     * Item entities have a tiny 0.25 bounding box; grow it a bit so they
     * are actually aimable.
     */
    private static final double PICK_MARGIN = 0.25;

    @SubscribeEvent
    public static void onRightClick(
            InputEvent.InteractionKeyMappingTriggered event) {

        if (!event.isUseItem()) {
            return;
        }

        Player player = PlayerStatus.getPlayer();

        if (player == null || player.isSpectator()) {
            return;
        }

        ItemEntity itemEntity = findTargetedItem(player);

        if (itemEntity == null) {
            return;
        }

        // Hands are full: let the click through (eat, place, open a door...)
        // instead of swallowing it with a pickup the server would reject.
        if (!InventorySystem.canPickup(player, itemEntity.getItem())) {
            return;
        }

        ClientPacketDistributor.sendToServer(
                new PickupItemPayload(itemEntity.getId())
        );

        event.setCanceled(true);
    }

    /**
     * Returns the item entity under the crosshair, or null if there is none
     * or if a block / other entity the player is aiming at is closer.
     */
    private static ItemEntity findTargetedItem(Player player) {

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();

        double reach = player.blockInteractionRange();
        double nearestSqr = reach * reach;

        // Anything vanilla already targets (block or entity) blocks the ray.
        HitResult vanillaHit = Minecraft.getInstance().hitResult;

        if (vanillaHit != null && vanillaHit.getType() != HitResult.Type.MISS) {
            nearestSqr = Math.min(
                    nearestSqr,
                    vanillaHit.getLocation().distanceToSqr(start)
            );
        }

        Vec3 end = start.add(direction.scale(reach));

        AABB searchBox =
                new AABB(start, end).inflate(1.0);

        ItemEntity closest = null;

        for (ItemEntity item :
                player.level().getEntitiesOfClass(
                        ItemEntity.class,
                        searchBox,
                        ItemEntity::isAlive
                )) {

            AABB box = item.getBoundingBox().inflate(PICK_MARGIN);

            if (box.contains(start)) {
                return item;
            }

            Optional<Vec3> hit = box.clip(start, end);

            if (hit.isEmpty()) {
                continue;
            }

            double distance = start.distanceToSqr(hit.get());

            if (distance < nearestSqr) {
                nearestSqr = distance;
                closest = item;
            }
        }

        return closest;
    }
}
