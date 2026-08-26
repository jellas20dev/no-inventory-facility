package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class HudSystem {

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof InventoryScreen) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {

            event.setCanceled(true);

            if (player.isSpectator()) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            ItemStack mainHand =
                    player.getInventory().getItem(0);

            ItemStack offHand =
                    player.getOffhandItem();

            HandHud.renderHands(
                    graphics,
                    player,
                    mainHand,
                    offHand
            );

            return;
        }

        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {

            event.setCanceled(true);

            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            HealthHud.renderHealth(
                    graphics,
                    player
            );

            return;
        }

        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {

            event.setCanceled(true);

            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            HungerHud.renderHunger(
                    graphics,
                    player
            );

            return;
        }

        if (event.getName().equals(
                VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND)) {

            event.setCanceled(true);
            return;
        }

        if (event.getName().equals(
                VanillaGuiLayers.CONTEXTUAL_INFO_BAR)) {

            event.setCanceled(true);
            return;
        }

        if (event.getName().equals(
                VanillaGuiLayers.EXPERIENCE_LEVEL)) {

            event.setCanceled(true);

            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            ExperienceHud.renderExperience(
                    graphics,
                    player
            );

            return;
        }
    }
}
