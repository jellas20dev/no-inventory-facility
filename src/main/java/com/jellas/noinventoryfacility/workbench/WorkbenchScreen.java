package com.jellas.noinventoryfacility.workbench;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Top half of the vanilla crafting table texture (grid + result), then a
 * short strip with the two hand slots instead of the player inventory.
 */
public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {

    private static final Identifier TEXTURE = Identifier.withDefaultNamespace(
            "textures/gui/container/crafting_table.png"
    );

    private static final Component HANDS_LABEL =
            Component.translatable("container.noinventoryfacility.hands");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 118;

    /** Rows of the vanilla texture with the title, grid and result. */
    private static final int TOP_ROWS = 78;

    /** A plain row of the panel (borders + grey) used as filler. */
    private static final int FILLER_ROW = 76;

    /** Bottom border of the vanilla panel. */
    private static final int BOTTOM_ROW = 162;
    private static final int BOTTOM_ROWS = 4;

    /** Frame of the first grid slot, reused for the hand slots. */
    private static final int SLOT_U = 29;
    private static final int SLOT_V = 16;
    private static final int SLOT_SIZE = 18;

    public WorkbenchScreen(WorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
        this.inventoryLabelY = WorkbenchMenu.HANDS_Y - 11;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int x = this.leftPos;
        int y = this.topPos;

        blit(graphics, x, y, 0, 0, WIDTH, TOP_ROWS);

        for (int row = TOP_ROWS; row < HEIGHT - BOTTOM_ROWS; row++) {
            blit(graphics, x, y + row, 0, FILLER_ROW, WIDTH, 1);
        }

        blit(graphics, x, y + HEIGHT - BOTTOM_ROWS, 0, BOTTOM_ROW, WIDTH, BOTTOM_ROWS);

        // Slot frames sit one pixel up-left of the item position.
        blit(graphics, x + WorkbenchMenu.MAIN_HAND_X - 1, y + WorkbenchMenu.HANDS_Y - 1,
                SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);
        blit(graphics, x + WorkbenchMenu.OFF_HAND_X - 1, y + WorkbenchMenu.HANDS_Y - 1,
                SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        graphics.text(this.font, HANDS_LABEL, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
    }

    private static void blit(
            GuiGraphicsExtractor graphics,
            int x, int y, int u, int v, int width, int height
    ) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                x, y, u, v, width, height, 256, 256);
    }
}
