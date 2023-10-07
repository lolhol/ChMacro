package com.mit.features.pathfind.utils;

import com.mit.util.BlockUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Utils extends Costs {

  public static BlockNodeClass getClassOfStarting(BlockPos startingBlock, BlockPos endBlock) {
    return new BlockNodeClass(
      null,
      startingBlock,
      calculateGCostBlockPos(startingBlock, startingBlock),
      calculateHCostBlockPos(startingBlock, endBlock),
      0,
      getFullCost(startingBlock, startingBlock, endBlock),
      null,
      new HashSet<>()
    );
  }

  public static BlockNodeClass getClassOfEnding(BlockPos startingBlock, BlockPos endBlock) {
    return new BlockNodeClass(
      null,
      endBlock,
      calculateGCostBlockPos(startingBlock, endBlock),
      calculateHCostBlockPos(endBlock, endBlock),
      0,
      getFullCost(endBlock, startingBlock, endBlock),
      null,
      new HashSet<>()
    );
  }

  public static BlockNodeClass getClassOfBlock(
    BlockPos block,
    BlockNodeClass parent,
    BlockPos starting,
    BlockPos ending,
    HashSet<BlockPos> addBroken
  ) {
    addBroken.addAll(parent.broken);

    return new BlockNodeClass(
      parent,
      block,
      calculateGCostBlockPos(block, starting),
      calculateHCostBlockPos(block, ending),
      calcOtherTotalCost(block),
      getFullCost(block, starting, ending),
      null,
      addBroken
    );
  }

  public static List<BlockNodeClass> getBlocksAround(BlockNodeClass reference, BlockPos start, BlockPos end) {
    List<BlockNodeClass> returnBlocks = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (x == 0 || z == 0) {
            BlockPos curBlock = reference.blockPos.add(x, y, z);

            if (!BlockUtils.isBlockSolid(curBlock)) {
              returnBlocks.add(getClassOfBlock(curBlock, reference, start, end, reference.broken));
            }
            /*while (y == -1 && !BlockUtils.isBlockSolid(curBlock.add(0, -1, 0))) {
              curBlock = reference.blockPos.add(0, -1, 0);
              returnBlocks.add(getClassOfBlock(curBlock, reference, start, end, reference.broken));
            }*/
          }
        }
      }
    }

    return returnBlocks;
  }

  public static List<BlockNodeClass> retracePath(BlockNodeClass startNode, BlockNodeClass endNode) {
    List<BlockPos> blockPath = new ArrayList<BlockPos>();
    List<BlockNodeClass> nodeClass = new ArrayList<>();
    BlockNodeClass currentNode = endNode;

    while (currentNode.parentOfBlock != null && !currentNode.equals(startNode)) {
      blockPath.add(currentNode.blockPos());
      nodeClass.add(currentNode);
      currentNode = currentNode.parentOfBlock;
    }

    return reverseList(nodeClass);
  }

  public static List<BlockNodeClass> reverseList(List<BlockNodeClass> initList) {
    int len = initList.size();
    if (len == 0) return null;

    int len2 = len >> 1;
    BlockNodeClass temp;

    for (int i = 0; i < len2; ++i) {
      temp = initList.get(i);
      initList.set(i, initList.get(initList.size() - i - 1));
      initList.set(initList.size() - i - 1, temp);
    }

    return initList;
  }

  public static ReturnClass isAbleToInteract(BlockPos block, BlockNodeClass parentBlock, boolean isMine) {
    if (canWalkOn(block, parentBlock)) return new ReturnClass(new ArrayList<>(), ActionTypes.WALK);

    if (canJumpOn(block, parentBlock)) return new ReturnClass(new ArrayList<>(), ActionTypes.JUMP);

    if (canFall(block, parentBlock)) return new ReturnClass(new ArrayList<>(), ActionTypes.FALL);

    return null;
  }

  private static List<BlockPos> getTheMinList(List<List<BlockPos>> lists) {
    return lists.stream().min(Comparator.comparingInt(List::size)).orElse(null);
  }

  @Getter
  @AllArgsConstructor
  public static class ReturnClass {

    public List<BlockPos> blocksToBreak;
    public ActionTypes actionType;
  }

  public static boolean canWalkOn(BlockPos block, BlockNodeClass parent) {
    double yDif = Math.abs(parent.blockPos.getY() - block.getY());

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    return (yDif <= 0.001 && !BlockUtils.isBlockSolid(blockAbove1) && BlockUtils.isBlockSolid(blockBelow1));
  }

  public static boolean canJumpOn(BlockPos block, BlockNodeClass parentBlock) {
    double yDiff = block.getY() - parentBlock.blockPos.getY();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    BlockPos blockAboveOneParent = parentBlock.blockPos.add(0, 1, 0);
    BlockPos blockAboveTwoParent = parentBlock.blockPos.add(0, 2, 0);

    return (
      yDiff == 1 &&
      BlockUtils.isBlockSolid(blockBelow1) &&
      !BlockUtils.isBlockSolid(blockAbove1) &&
      !BlockUtils.isBlockSolid(blockAboveOneParent) &&
      !BlockUtils.isBlockSolid(blockAboveTwoParent)
    );
  }

  public static boolean canFall(BlockPos block, BlockNodeClass parentBlock) {
    double yDiff = block.getY() - parentBlock.blockPos.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    BlockPos blockAbove1 = block.add(0, 1, 0);

    return (yDiff < 0 && yDiff > -4 && BlockUtils.isBlockSolid(blockBelow1) && !BlockUtils.isBlockSolid(blockAbove1));
  }

  public static boolean isAllClearToY(int y1, int y2, BlockPos block) {
    boolean isGreater = y1 < y2;
    int rem = 0;

    while (y1 != y2) {
      BlockPos curBlock = block.add(0, rem, 0);

      if (!BlockUtils.isBlockSolid(curBlock)) return false;
      y2--;
      rem--;
    }

    return true;
  }

  public static boolean isSameBlock(BlockNodeClass block1, BlockNodeClass block2) {
    return block1.blockPos.equals(block2.blockPos);
  }
}
