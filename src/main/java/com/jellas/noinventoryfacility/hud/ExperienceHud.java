package com.jellas.noinventoryfacility.hud;

import com.jellas.noinventoryfacility.PlayerStatus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class ExperienceHud {

    private static final Identifier EXPERIENCE_BAR_BACKGROUND =
            Identifier.withDefaultNamespace(
                    "hud/experience_bar_background"
            );

    private static final Identifier EXPERIENCE_BAR_PROGRESS =
            Identifier.withDefaultNamespace(
                    "hud/experience_bar_progress"
            );

    public static void renderExperience(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        int screenWidth =
                graphics.guiWidth();

        int screenHeight =
                graphics.guiHeight();

        int barWidth = 182;
        int barHeight = 5;

        int x =
                (screenWidth - barWidth) / 2;

        // Bottom of the screen: the top center belongs to boss bars, and the
        // hand items (y = height - 40 .. height - 24) stay clear above it.
        int y = screenHeight - 10;

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                EXPERIENCE_BAR_BACKGROUND,
                x,
                y,
                barWidth,
                barHeight
        );

        int filledWidth =
                (int) (
                        barWidth
                                * player.experienceProgress
                );

        if (filledWidth > 0) {

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    EXPERIENCE_BAR_PROGRESS,
                    182,
                    5,
                    0,
                    0,
                    x,
                    y,
                    filledWidth,
                    barHeight
            );
        }

        if (player.experienceLevel > 0) {

            String level =
                    String.valueOf(
                            player.experienceLevel
                    );

            int textWidth =
                    PlayerStatus.getMinecraft()
                            .font
                            .width(level);

            graphics.text(
                    PlayerStatus.getMinecraft().font,
                    level,
                    (screenWidth - textWidth) / 2,
                    y - 6,
                    0xFFFFFF00,
                    true
            );
        }
    }
}