package com.jellas.noinventoryfacility.hud;

import com.jellas.noinventoryfacility.ItemClassificationSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HandHud {

    public static void renderHands(
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
