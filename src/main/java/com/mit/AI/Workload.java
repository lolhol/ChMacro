package com.mit.AI;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Workload {

  public final List<BlockPos> blockData;
  public final Vec3 position;
  public final BlockPos curBreaking;
  public final int breakProgress;

  public Workload(List<BlockPos> data, Vec3 curPos, BlockPos curBreaking, int breakProgress) {
    this.blockData = data;
    this.position = curPos;
    this.curBreaking = curBreaking;
    this.breakProgress = breakProgress;
  }

  public Workload(List<BlockPos> data, BlockPos curBreaking, int breakProgress) {
    this.blockData = data;
    this.position = null;
    this.curBreaking = curBreaking;
    this.breakProgress = breakProgress;
  }
}
