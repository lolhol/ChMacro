package com.mit.features.pathfind.main;

import com.mit.features.pathfind.utils.*;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AStarPathFinder extends Utils {

  public static HashSet<BlockNodeClass> closedSet = new HashSet<>();

  public BlockPos startBlock = null;
  public BlockPos endBlock = null;

  boolean isStart;
  int ticks;

  int opened = 0;
  int closed = 0;

  public List<BlockNodeClass> run(PathFinderConfig pathFinderConfig) {
    //RenderMultipleBlocksMod.renderMultipleBlocks(null, false);

    int depth = 0;
    isStart = true;

    PriorityQueue<BlockNodeClass> openSet = new PriorityQueue<>(new BlockNodeCompare());
    closedSet = new HashSet<>();
    HashSet<BlockNodeClass> openList = new HashSet<>();

    BlockNodeClass previousNode = null;
    BlockNodeClass startPoint = Utils.getClassOfStarting(
      pathFinderConfig.startingBlock,
      pathFinderConfig.destinationBlock
    );

    startBlock = pathFinderConfig.startingBlock;
    endBlock = pathFinderConfig.destinationBlock;

    if (pathFinderConfig.startingBlock.equals(pathFinderConfig.destinationBlock)) {
      List<BlockNodeClass> ls = new ArrayList<>();
      ls.add(Utils.getClassOfStarting(pathFinderConfig.startingBlock, pathFinderConfig.destinationBlock));

      return ls;
    }

    BlockNodeClass endPoint = Utils.getClassOfEnding(pathFinderConfig.startingBlock, pathFinderConfig.destinationBlock);
    openSet.add(Utils.getClassOfStarting(pathFinderConfig.startingBlock, pathFinderConfig.destinationBlock));

    while (depth <= pathFinderConfig.maxIterations && !openSet.isEmpty()) {
      opened = openSet.size();
      closed = closedSet.size();

      //----------------------------------------------------------------------
      //| hCost ====> distance from end node.                                |
      //| gCost ====> distance from start node.                              |
      //| fCost ====> gCost + hCost.                                         |
      //----------------------------------------------------------------------

      BlockNodeClass node = openSet.poll();
      closedSet.add(node);

      if (node.blockPos.equals(endBlock) && node.parentOfBlock != null) {
        endPoint.parentOfBlock = node.parentOfBlock;
        isStart = false;

        return Utils.retracePath(startPoint, endPoint);
      }

      List<BlockNodeClass> children = Utils.getBlocksAround(node, startBlock, endBlock);
      for (BlockNodeClass child : children) {
        if (closedSet.contains(child)) {
          openSet.remove(child);
          openList.remove(child);
          continue;
        }

        Utils.ReturnClass typeAction = Utils.isAbleToInteract(
          child.blockPos,
          child.parentOfBlock,
          pathFinderConfig.isMine
        );

        if (typeAction == null) {
          continue;
        }

        child.actionType = typeAction.actionType;
        double newGCost =
          child.parentOfBlock.gCost + MathUtils.distanceFromTo(child.blockPos, child.parentOfBlock.blockPos);
        if (!openList.contains(child) || newGCost < child.gCost) {
          child.gCost += Costs.calcOtherTotalCost(child);

          child.totalCost = child.hCost + child.gCost;

          openList.add(child);
          openSet.add(child);
        }
      }

      depth++;
    }

    isStart = false;
    return null;
  }

  public List<Vec3> fromClassToVec(List<BlockNodeClass> blockNode) {
    List<Vec3> returnList = new ArrayList<>();

    for (BlockNodeClass block : blockNode) {
      returnList.add(block.getVec());
    }

    return returnList;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!this.isStart) return;
    if (this.ticks >= 50) {
      this.ticks = 0;
      ChatUtils.chat("Update: Opened " + this.opened + ". And closed " + this.closed);
    }

    this.ticks++;
  }
}
