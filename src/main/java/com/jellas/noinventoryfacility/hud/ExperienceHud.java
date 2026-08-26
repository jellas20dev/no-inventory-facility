package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class ExperienceHud {

    public static void renderExperience(
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
}
