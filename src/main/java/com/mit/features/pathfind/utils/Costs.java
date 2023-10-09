package com.mit.features.pathfind.utils;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Costs {

  public static double calculateGCost(BlockNodeClass nodeClass, BlockPos startBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, startBlock);
  }

  public static double getDistCost(BlockNodeClass node) {
    return node.isOnSide() ? 2 : 0;
  }

  public static double calculateHCost(BlockNodeClass nodeClass, BlockPos finalBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, finalBlock);
  }

  public static double getSlabCost(BlockNodeClass block) {
    return getDistCost(block) == 0 && BlockUtils.getBlockType(block.blockPos.down()).getRegistryName().contains("slab")
      ? -1
      : 0;
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

  public static double calcOtherTotalCost(BlockNodeClass child) {
    // Calc random costs like surrounding etc.
    return (
      Utils.calculateSurroundingsDoubleCost(child.blockPos.up()) +
      Costs.getActionCost(child.actionType) +
      Costs.getSlabCost(child) +
      Costs.getDistCost(child)
    );
  }

  public static double calculateFullCostDistance(BlockPos pos1, BlockPos startBlock, BlockPos finalBlock) {
    return calculateGCostBlockPos(pos1, startBlock) + calculateHCostBlockPos(pos1, finalBlock);
  }

  public static double getFullCost(BlockPos pos1, BlockPos startBlock, BlockPos finalBlock) {
    return (calculateGCostBlockPos(pos1, startBlock) + calculateHCostBlockPos(pos1, finalBlock));
  }

  public static double getActionCost(ActionTypes action) {
    switch (action) {
      case FALL:
        return 1.5;
      case JUMP:
        return 2;
    }
    return 1;
  }

  public static double getYawCost(BlockNodeClass node) {
    Vec3 childVec = BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(node.blockPos));
    Vec3 parentVec = BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(node.parentOfBlock.blockPos));

    double yawDifference = Math.atan2(childVec.zCoord - parentVec.zCoord, childVec.xCoord - parentVec.xCoord);

    double yawDegrees = Math.toDegrees(yawDifference);

    if (yawDegrees > 180.0) {
      yawDegrees -= 360.0;
    } else if (yawDegrees < -180.0) {
      yawDegrees += 360.0;
    }

    double yawCost = Math.abs(yawDegrees) / 360;

    return yawCost;
  }

  public static double calculateSurroundingsDoubleCost(BlockPos block) {
    Iterable<BlockPos> blocks = BlockPos.getAllInBox(block.up().up().add(-2, -1, -2), block.add(2, 1, 2));
    return BlockUtils.amountNonAir(blocks) * 1.5;
  }

  public static double getBreakCost(BlockPos block) {
    return BlockUtils.getBlockType(block).getBlockHardness(Dependencies.mc.theWorld, block) * 5;
  }

  public static double walkCost() {
    return 1;
  }
}
