package com.mit.features.pathfind.utils;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import com.mit.util.PacketUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Costs {

  public static double calculateGCost(BlockNodeClass nodeClass, BlockPos startBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, startBlock);
  }

  public static double getDistCost(BlockNodeClass node) {
    return node.isOnSide() ? 0.5 : 0;
  }

  public static double calculateHCost(BlockNodeClass nodeClass, BlockPos finalBlock) {
    return MathUtils.distanceFromTo(nodeClass.blockPos, finalBlock);
  }

  public static double getSlabCost(BlockNodeClass block) {
    String name = BlockUtils.getBlockType(block.blockPos.down()).getRegistryName();
    return (getDistCost(block) == 0 && (name.contains("slab") || name.contains("layer"))) ? -1 : 0;
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

  public static double calcOtherTotalCost(BlockNodeClass child, int pathWidth) {
    // Calc random costs like surrounding etc.
    return (
      //Utils.calculateSurroundingsDoubleCost(child.blockPos.up(), pathWidth) +
      Costs.getActionCost(child.actionType) +
      Costs.getSlabCost(child) +
      //Costs.getDistCost(child) +
      //Costs.getPathWidthCost(child, child.parentOfBlock) +
      Costs.calcDirectionChange(child)
    );
  }

  private static double calcDirectionChange(BlockNodeClass node) {
    double cost = 0;
    double c = Math.abs(MathUtils.minusAbs(node.blockPos.getX(), node.parentOfBlock.blockPos.getX()));
    cost += c == 0 ? 0 : c - 1;

    double c1 = Math.abs(MathUtils.minusAbs(node.blockPos.getX(), node.parentOfBlock.blockPos.getX()));
    cost += c1 == 0 ? 0 : c1 - 1;

    return cost;
  }

  private static double getDirectionChangeCost(BlockNodeClass node) {
    BlockNodeClass bp = node;
    double cost = 0;
    if (node != null) {
      cost = calcDirectionChange(bp);
      double prevCost = cost;

      int c = 0;
      while (bp != null && calcDirectionChange(bp) == prevCost && c < 4) {
        prevCost = calcDirectionChange(bp);
        bp = bp.parentOfBlock;

        cost += 1;

        c++;
      }
    }

    return cost;
  }

  private static double getPathWidthCost(BlockNodeClass node, BlockNodeClass parent) {
    if (MathUtils.minusAbs(node.blockPos.getX(), parent.blockPos.getX()) > 1) {
      int cost = 0;
      for (BlockPos block : BlockUtils.getBlocksInRadius(0, 0, 5, BlockUtils.fromBPToVec(node.blockPos))) {
        if (!BlockUtils.isWalkable(block)) {
          cost++;
        }
      }

      return (double) cost / 2;
    }

    int cost = 0;
    for (BlockPos block : BlockUtils.getBlocksInRadius(5, 0, 0, BlockUtils.fromBPToVec(node.blockPos))) {
      if (!BlockUtils.isWalkable(block)) {
        cost++;
      }
    }

    return (double) cost / 2;
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
      case JUMP:
        return 1;
    }
    return 0;
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

  public static double calculateSurroundingsDoubleCost(BlockPos block, int pathSize) {
    Iterable<BlockPos> blocks = BlockPos.getAllInBox(block.add(-3, 1, -3), block.add(3, 4, 3));

    return BlockUtils.amountNonAir(blocks); // + BlockUtils.amountNonAir(blocksV1);
  }

  public static double getBreakCost(BlockPos block) {
    return BlockUtils.getBlockType(block).getBlockHardness(Dependencies.mc.theWorld, block) * 5;
  }

  public static double walkCost() {
    return 1;
  }
}
