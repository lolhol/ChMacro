package com.mit.event;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BlockBreakStateUpdateEvent extends Event {
    public final int progress;
    public final BlockPos position;
    public final int breakerId;

    public BlockBreakStateUpdateEvent(int progress, BlockPos position, int breakerId) {
        this.progress = progress;
        this.position = position;
        this.breakerId = breakerId;
    }
}
