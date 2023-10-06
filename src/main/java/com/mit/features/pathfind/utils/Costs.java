package com.mit.features.pathfind.utils;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import net.minecraft.util.BlockPos;

public class Costs {

  public static double calculateGCost(BlockNodeClass nodeClass, BlockPos startBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, startBlock);
  }

  public static double calculateHCost(BlockNodeClass nodeClass, BlockPos finalBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, finalBlock);
  }

  public static double calculateFullCostDistance(BlockNodeClass nodeClass, BlockPos start, BlockPos end) {
    return calculateGCost(nodeClass, start) + calculateHCost(nodeClass, end);
  }

  public static double calculateGCostBlockPos(BlockPos pos1, BlockPos startBlock) {
    return MathUtils.distanceFromTo(pos1, startBlock);
  }

  public static double calculateHCostBlockPos(BlockPos pos1, BlockPos finalBlock) {
    return MathUtils.distanceFromTo(pos1, finalBlock);
  }

  public static double calculateFullCostDistance(BlockPos pos1, BlockPos startBlock, BlockPos finalBlock) {
    return calculateGCostBlockPos(pos1, startBlock) + calculateHCostBlockPos(pos1, finalBlock);
  }

  public static double getFullCost(BlockPos pos1, BlockPos startBlock, BlockPos finalBlock) {
    return (
      calculateGCostBlockPos(pos1, startBlock) + calculateHCostBlockPos(pos1, finalBlock)
      /*calculateSurroundingsDoubleCost(pos1) +
      getBreakCost(pos1) +
      walkCost()*/
    );
  }

  public static double getActionCost(ActionTypes action) {
    switch (action) {
      case WALK:
        return 1;
      case JUMP:
        return 2;
      case FALL:
        return 3;
      case BREAK:
        return 10;
    }

    return 0;
  }

  public static double calculateSurroundingsDoubleCost(BlockPos block) {
    Iterable<BlockPos> blocks = BlockPos.getAllInBox(block.add(-4, 0, -4), block.add(4, 1, 4));
    //double percent = BlockUtils.getPercentOfNonAir(blocks);
    return 0.4;
  }

  public static double getBreakCost(BlockPos block) {
    return BlockUtils.getBlockType(block).getBlockHardness(Dependencies.mc.theWorld, block) * 5;
  }

  public static double walkCost() {
    return 1;
  }
}
