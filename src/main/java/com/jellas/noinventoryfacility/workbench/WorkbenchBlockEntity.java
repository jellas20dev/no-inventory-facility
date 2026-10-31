package com.jellas.noinventoryfacility.workbench;

import com.jellas.noinventoryfacility.NoInventoryFacility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WorkbenchBlockEntity extends BlockEntity {

    public WorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(NoInventoryFacility.WORKBENCH_ENTITY.get(), pos, state);
    }
}
