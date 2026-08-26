package com.jellas.noinventoryfacility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class HudSystem {

    private static final Identifier FOOD_EMPTY =
            Identifier.withDefaultNamespace("hud/food_empty");

    private static final Identifier FOOD_HALF =
            Identifier.withDefaultNamespace("hud/food_half");

    private static final Identifier FOOD_FULL =
            Identifier.withDefaultNamespace("hud/food_full");

    private static final Identifier FOOD_EMPTY_HUNGER =
            Identifier.withDefaultNamespace("hud/food_empty_hunger");

    private static final Identifier FOOD_HALF_HUNGER =
            Identifier.withDefaultNamespace("hud/food_half_hunger");

    private static final Identifier FOOD_FULL_HUNGER =
            Identifier.withDefaultNamespace("hud/food_full_hunger");

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof InventoryScreen) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {

        if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {

            event.setCanceled(true);

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            ItemStack mainHand =
                    player.getInventory().getItem(0);

            ItemStack offHand =
                    player.getOffhandItem();

            renderHands(
                    graphics,
                    player,
                    mainHand,
                    offHand
            );

            return;
        }

        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {

            event.setCanceled(true);

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            renderHealth(
                    graphics,
                    player
            );

            return;
        }

        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {

            event.setCanceled(true);

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            renderFood(
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

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) {
                return;
            }

            GuiGraphicsExtractor graphics =
                    event.getGuiGraphics();

            renderExperience(
                    graphics,
                    player
            );
        }
    }

    private static void renderHealth(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();

        int totalHearts =
                (int) Math.ceil(maxHealth / 2.0F);

        for (int i = 0; i < totalHearts; i++) {

            int x = 10 + i * 8;
            int y = 10;

            Identifier container =
                    Hud.HeartType.CONTAINER.getSprite(
                            false,
                            false,
                            false
                    );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    container,
                    x,
                    y,
                    9,
                    9
            );

            float heartHealth =
                    health - (i * 2.0F);

            if (heartHealth >= 2.0F) {

                Identifier full =
                        Hud.HeartType.NORMAL.getSprite(
                                false,
                                false,
                                false
                        );

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        full,
                        x,
                        y,
                        9,
                        9
                );

            } else if (heartHealth > 0.0F) {

                Identifier half =
                        Hud.HeartType.NORMAL.getSprite(
                                false,
                                true,
                                false
                        );

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        half,
                        x,
                        y,
                        9,
                        9
                );
            }
        }
    }

    private static void renderFood(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        int food =
                player.getFoodData().getFoodLevel();

        boolean hasHunger =
                player.hasEffect(MobEffects.HUNGER);

        Identifier empty =
                hasHunger
                        ? FOOD_EMPTY_HUNGER
                        : FOOD_EMPTY;

        Identifier half =
                hasHunger
                        ? FOOD_HALF_HUNGER
                        : FOOD_HALF;

        Identifier full =
                hasHunger
                        ? FOOD_FULL_HUNGER
                        : FOOD_FULL;

        int screenWidth =
                graphics.guiWidth();

        for (int i = 0; i < 10; i++) {

            int x =
                    screenWidth - 10 - ((9 - i) * 8) - 9;

            int y = 10;

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    empty,
                    x,
                    y,
                    9,
                    9
            );

            int foodForIcon =
                    i * 2 + 1;

            if (foodForIcon < food) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        full,
                        x,
                        y,
                        9,
                        9
                );

            } else if (foodForIcon == food) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        half,
                        x,
                        y,
                        9,
                        9
                );
            }
        }
    }

    private static void renderExperience(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        int screenWidth =
                graphics.guiWidth();

        int barWidth = 182;
        int barHeight = 5;

        int x =
                (screenWidth - barWidth) / 2;

        int y = 10;

        int filledWidth =
                (int) (
                        barWidth
                                * player.experienceProgress
                );

        graphics.fill(
                x,
                y,
                x + barWidth,
                y + barHeight,
                0xFF000000
        );

        if (filledWidth > 0) {

            graphics.fill(
                    x,
                    y,
                    x + filledWidth,
                    y + barHeight,
                    0xFF80FF20
            );
        }

        if (player.experienceLevel > 0) {

            String level =
                    String.valueOf(
                            player.experienceLevel
                    );

            int textWidth =
                    Minecraft.getInstance()
                            .font
                            .width(level);

            graphics.text(
                    Minecraft.getInstance().font,
                    level,
                    (screenWidth - textWidth) / 2,
                    y + barHeight + 2,
                    0xFFFFFF00,
                    true
            );
        }
    }

    private static void renderHands(
            GuiGraphicsExtractor graphics,
            Player player,
            ItemStack mainHand,
            ItemStack offHand
    ) {
        boolean mainTwoHanded =
                !mainHand.isEmpty()
                        && ItemClassificationSystem.isTwoHanded(
                        mainHand,
                        player.level()
                );

        boolean offTwoHanded =
                !offHand.isEmpty()
                        && ItemClassificationSystem.isTwoHanded(
                        offHand,
                        player.level()
                );

        if (mainTwoHanded || offTwoHanded) {

            ItemStack twoHandedItem =
                    mainTwoHanded
                            ? mainHand
                            : offHand;

            renderCentered(
                    graphics,
                    twoHandedItem
            );

            return;
        }

        if (!mainHand.isEmpty()) {

            renderMainHand(
                    graphics,
                    mainHand
            );
        }

        if (!offHand.isEmpty()) {

            renderOffHand(
                    graphics,
                    offHand
            );
        }
    }

    private static void renderMainHand(
            GuiGraphicsExtractor graphics,
            ItemStack stack
    ) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int x = screenWidth - 40;
        int y = screenHeight - 40;

        graphics.item(
                stack,
                x,
                y
        );

        graphics.itemDecorations(
                Minecraft.getInstance().font,
                stack,
                x,
                y
        );
    }

    private static void renderOffHand(
            GuiGraphicsExtractor graphics,
            ItemStack stack
    ) {
        int screenHeight = graphics.guiHeight();

        int x = 24;
        int y = screenHeight - 40;

        graphics.item(
                stack,
                x,
                y
        );

        graphics.itemDecorations(
                Minecraft.getInstance().font,
                stack,
                x,
                y
        );
    }

    private static void renderCentered(
            GuiGraphicsExtractor graphics,
            ItemStack stack
    ) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int x = (screenWidth / 2) - 8;
        int y = screenHeight - 40;

        graphics.item(
                stack,
                x,
                y
        );

        graphics.itemDecorations(
                Minecraft.getInstance().font,
                stack,
                x,
                y
        );
    }
}