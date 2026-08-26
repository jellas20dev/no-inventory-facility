package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.network.PickupItemPayload;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ItemDetectorSystem {

    @SubscribeEvent
    public static void onRightClick(
            InputEvent.InteractionKeyMappingTriggered event) {

        if (!event.isUseItem()) {
            return;
        }

        Player player = PlayerStatus.getPlayer();

        if (player == null) {
            return;
        }

        ItemEntity itemEntity = findItemEntity(player);

        if (itemEntity == null) {
            return;
        }

        ClientPacketDistributor.sendToServer(
                new PickupItemPayload(itemEntity.getId())
        );

        event.setCanceled(true);
    }

    private static ItemEntity findItemEntity(Player player) {

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();

        double reach = 5.0;

        Vec3 end = start.add(
                direction.scale(reach)
        );

        AABB searchBox =
                new AABB(start, end).inflate(0.5);

        ItemEntity closest = null;
        double closestDistance =
                Double.MAX_VALUE;

        for (ItemEntity item :
                player.level().getEntitiesOfClass(
                        ItemEntity.class,
                        searchBox
                )) {

            if (!item.isAlive()) {
                continue;
            }

            double distance =
                    player.distanceToSqr(item);

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = item;
            }
        }

        return closest;
    }
}