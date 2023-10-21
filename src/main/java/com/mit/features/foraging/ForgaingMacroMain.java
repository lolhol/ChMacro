package com.mit.features.foraging;

import com.mit.event.MsEvent;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.global.Dependencies;
import com.mit.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgaingMacroMain {

  WalkerMain walker = new WalkerMain();
  AStarPathFinder finder = new AStarPathFinder();
  boolean isStart = false;
  List<BlockPos> curRoute = new ArrayList<>();
  Vec3 curBlock = null;

  boolean startNuker = false;
  int curNukerCount = 0;
  HashSet<BlockPos> broken = new HashSet<>();
  boolean paused = false;
  int curListPos = 0;

  int brokenClearTime = 0;

  public ForgaingMacroMain() {}

  public void setState(boolean st) {
    this.isStart = st;
    if (!st) {
      MinecraftForge.EVENT_BUS.unregister(this);
    }
  }

  public void run(List<BlockPos> route) {
    MinecraftForge.EVENT_BUS.register(this);

    new Thread(() -> {
      this.curRoute = route;
      curListPos = 0;
      this.curBlock = BlockUtils.fromBPToVec(this.curRoute.get(curListPos).add(0, 1, 0));
      curListPos++;

      PathFinderConfig newConfig = new PathFinderConfig(
        false,
        false,
        false,
        false,
        false,
        10,
        10000,
        1000,
        BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector().addVector(-0.5, 0, -0.5)),
        BlockUtils.fromVecToBP(curBlock),
        new Block[] { Blocks.air },
        new Block[] { Blocks.air },
        100,
        0
      );

      List<Vec3> path = finder.fromClassToVec(finder.run(newConfig));
      walker.run(path, true);
      setState(true);
    })
      .start();
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (brokenClearTime >= 50) {
      broken.clear();
      brokenClearTime = 0;
    } else {
      brokenClearTime++;
    }

    if (!isStart || startNuker) return;

    if (this.curListPos >= this.curRoute.size()) {
      this.curListPos = 0;
      return;
    }

    List<BlockPos> wood = getWoodAround();
    if (wood.size() > 3 && walker.isDone()) {
      startNuker = true;
      return;
    }

    if (walker.isDone()) {
      this.isStart = false;
      new Thread(() -> {
        this.curBlock = BlockUtils.fromBPToVec(this.curRoute.get(this.curListPos)).addVector(0, 1, 0);
        this.curListPos++;
        PathFinderConfig newConfig = new PathFinderConfig(
          false,
          false,
          false,
          false,
          false,
          10,
          10000,
          1000,
          BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector().addVector(-0.5, 0, -0.5)),
          BlockUtils.fromVecToBP(curBlock),
          new Block[] { Blocks.air },
          new Block[] { Blocks.air },
          100,
          0
        );

        List<Vec3> path = BlockUtils.shortenList(finder.fromClassToVec(finder.run(newConfig)));

        walker.run(path, true, true, 2);
        this.isStart = true;
      })
        .start();
    }
  }

  @SubscribeEvent
  public void onMs(MsEvent event) {
    if (!startNuker || !Dependencies.mc.thePlayer.onGround) return;

    if (!paused) {
      walker.pause();
      paused = true;
    }

    List<BlockPos> wood = getWoodAround();
    if (curNukerCount >= 100) {
      curNukerCount = 0;
      BlockPos closest = BlockUtils.getClosest(
        wood,
        broken,
        BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector())
      );

      if (closest != null) {
        broken.add(closest);

        if (
          MathUtils.distanceFromTo(Dependencies.mc.thePlayer.getPositionVector(), BlockUtils.fromBPToVec(closest)) <
          Dependencies.mc.playerController.getBlockReachDistance()
        ) {
          RotationUtils.smoothLook(RotationUtils.getRotation(closest), 300);
          PacketUtils.sendStartPacket(closest, PacketUtils.getEnum(closest));
        }
      } else {
        startNuker = false;
        isStart = true;
        paused = false;
        walker.unpause();
      }
    } else {
      curNukerCount++;
    }
  }

  List<BlockPos> getWoodAround() {
    List<BlockPos> returnBlocks = new ArrayList<>();
    Iterable<BlockPos> blocks = BlockPos.getAllInBox(
      BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()).add(-3, -3, -3),
      BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()).add(3, 3, 3)
    );
    blocks.forEach(i -> {
      if (BlockUtils.getBlockType(i) == Blocks.log) {
        returnBlocks.add(i);
      }
    });

    return returnBlocks;
  }
}
