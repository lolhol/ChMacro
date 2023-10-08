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
    return MathUtils.distanceFromTo(node.blockPos, node.parentOfBlock.blockPos) <= 1
      ? 0
      : MathUtils.distanceFromTo(node.blockPos, node.parentOfBlock.blockPos) * 5;
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

  public static double calcOtherTotalCost(BlockPos pos) {
    // Calc random costs like surrounding etc.
    return Utils.calculateSurroundingsDoubleCost(pos);
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

  public static double getActionCost(ActionTypes action, double totalCost) {
    return 1;
  }

  public static double getYawCost(BlockNodeClass node) {
    Vec3 childVec = BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(node.blockPos));
    Vec3 parentVec = BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(node.parentOfBlock.blockPos));

    // Calculate the yaw difference in radians
    double yawDifference = Math.atan2(childVec.zCoord - parentVec.zCoord, childVec.xCoord - parentVec.xCoord);

    // Convert yaw difference to degrees
    double yawDegrees = Math.toDegrees(yawDifference);

    // Ensure yaw is within the range -180 to 180 degrees
    if (yawDegrees > 180.0) {
      yawDegrees -= 360.0;
    } else if (yawDegrees < -180.0) {
      yawDegrees += 360.0;
    }

    // Calculate the cost based on the yaw difference
    // You can define your cost function here; for example, you can penalize larger yaw differences more.
    double yawCost = Math.abs(yawDegrees) / 100;

    return yawCost;
  }

  public static int calculateSurroundingsDoubleCost(BlockPos block) {
    Iterable<BlockPos> blocks = BlockPos.getAllInBox(block.add(0, 1, 0).add(-2, -1, -2), block.add(2, 1, 2));
    return BlockUtils.amountNonAir(blocks) / 2;
  }

  public static double getBreakCost(BlockPos block) {
    return BlockUtils.getBlockType(block).getBlockHardness(Dependencies.mc.theWorld, block) * 5;
  }

  public static double walkCost() {
    return 1;
  }
}
