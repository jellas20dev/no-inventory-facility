package com.jellas.noinventoryfacility.workbench;

import com.jellas.noinventoryfacility.NoInventoryFacility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WorkbenchBlockEntity extends BlockEntity {

    private final WorkbenchCraftingContainer grid =
            new WorkbenchCraftingContainer(this::setChanged);

    public WorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(NoInventoryFacility.WORKBENCH_ENTITY.get(), pos, state);
    }

    public WorkbenchCraftingContainer getGrid() {
        return grid;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, grid.stacks());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        grid.stacks().clear();
        ContainerHelper.loadAllItems(input, grid.stacks());
    }

    /** Breaking the workbench spills the grid instead of deleting it. */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (level != null) {
            Containers.dropContents(level, pos, grid);
        }
    }
}
