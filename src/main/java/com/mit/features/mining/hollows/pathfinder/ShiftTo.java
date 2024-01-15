package com.mit.features.mining.hollows.pathfinder;

import com.mit.features.mining.hollows.macro.data.CostUtils;
import com.mit.features.mining.hollows.macro.util.ShifterCallback;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.global.Dependencies;
import com.mit.util.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;

public class ShiftTo {

  public boolean working = false;
  private BlockPos miningNext;
  private Vec3 block;
  private AStarPathFinder finder = new AStarPathFinder();
  private List<Vec3> path = null;
  private List<BlockPos> blocksUnder = null;
  ShifterCallback shifterCallback;

  Vec3 curPosWalking = null;

  public void run(BlockPos miningNext, ShifterCallback shifterCallback, List<BlockPos> blocksSeen) {
    this.miningNext = miningNext;
    this.shifterCallback = shifterCallback;
    MinecraftForge.EVENT_BUS.register(this);
    new Thread(() -> {
      RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
      try {
        working = true;
        BlockPos best = null;
        double bestDist = 1000;
        blocksUnder = getBlocksUnder();
        ChatUtils.chat(String.valueOf(blocksSeen.size()));
        blocksUnder.sort(Comparator.comparingInt((BlockPos a) -> a.getX()).thenComparingInt(Vec3i::getZ));

        for (int i = -1; i <= 1; i += 1) {
          for (int j = -1; j <= 1; j += 1) {
            if ((i != 0 && j != 0) || (i == 0 && j == 0)) continue;
            BlockPos b = new BlockPos(
              Dependencies.mc.thePlayer.posX + i,
              Dependencies.mc.thePlayer.posY,
              Dependencies.mc.thePlayer.posZ + j
            );

            if (b.getX() == Dependencies.mc.thePlayer.posX && b.getY() == Dependencies.mc.thePlayer.posY) continue;

            if (
              BlockUtils.getBlockType(b.add(0, 1, 0)) != Blocks.air ||
              BlockUtils.getBlockType(b.add(0, 2, 0)) != Blocks.air
            ) continue;

            if (
              RayTracingUtils.getPossibleLocDefault(
                BlockUtils.fromBPToVec(b.add(0, Dependencies.mc.thePlayer.eyeHeight, 0)),
                miningNext,
                new Block[] { Blocks.air }
              ) ==
              null ||
              MathUtils.distanceFromTo(b.add(0, Dependencies.mc.thePlayer.eyeHeight, 0), miningNext) >=
              Dependencies.mc.playerController.getBlockReachDistance()
            ) continue;

            if (MathUtils.distanceFromTo(b.add(0, Dependencies.mc.thePlayer.eyeHeight, 0), miningNext) < bestDist) {
              bestDist = MathUtils.distanceFromTo(b.add(0, Dependencies.mc.thePlayer.eyeHeight, 0), miningNext);
              best = b.add(0.5, 0, 0.5);
            }
          }
        }

        if (best != null) {
          RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(best), true);
          ChatUtils.chat("PATHFINDING");
          block = BlockUtils.fromBPToVec(best);
          /*path =
            finder.fromClassToVec(
              finder.run(
                finder.getWalkConfig(
                  BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()),
                  best.add(0, 1, 0)
                )
              )
            );*/
        } else {
          ChatUtils.chat("BEST NULL");
          stop();
        }
      } catch (Exception e) {
        ChatUtils.chat("NULL!!1");
        stop();
        ChatUtils.chat(e.getMessage());
        e.printStackTrace();
      }
    })
      .start();
  }

  public void stop() {
    KeyBindHandler.resetKeybindState();
    //MinecraftForge.EVENT_BUS.unregister(this);
    working = false;
    shifterCallback.shifterDone();
    path = null;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!working || block == null) return;

    Vec3 plyLoc = Dependencies.mc.thePlayer.getPositionVector();
    if (MathUtils.distanceFromToXZ(block, plyLoc) < 0.4) {
      /*if (path.isEmpty()) {
        this.path = null;
        this.stop();
        return;
      }

      this.curPosWalking = BlockUtils.getCenteredVec(path.remove(0));
      return;*/
      this.stop();
      return;
    }

    KeyBindHandler.resetKeybindState();
    for (KeyBinding k : VecUtils.getNeededKeyPresses(plyLoc, block)) {
      KeyBindHandler.setKeyBindState(k, true);
    }
    KeyBindHandler.setKeyBindState(Dependencies.mc.gameSettings.keyBindSneak, true);
  }

  private List<BlockPos> getBlocksUnder() {
    List<BlockPos> open = new ArrayList<>();
    HashSet<BlockPos> alrSeen = new HashSet<>();
    open.add(Dependencies.mc.thePlayer.getPosition().add(0, -1, 0));
    while (!open.isEmpty()) {
      BlockPos cur = open.remove(0);
      BlockPos[] sides = new BlockPos[] { cur.north(), cur.south(), cur.east(), cur.west() };
      for (BlockPos b : sides) {
        if (!alrSeen.contains(b) && BlockUtils.getBlockType(b) == Blocks.cobblestone) {
          alrSeen.add(b);
          open.add(b);
        }
      }
    }

    return new ArrayList<>(alrSeen);
  }
  /*@SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (blocksUnder == null) return;
    BlockPos b1 = blocksUnder.get(0);
    BlockPos b2 = blocksUnder.get(blocksUnder.size() - 1);
    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
    double x1 = b1.getX() - (viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks);
    double y1 = b1.getY() - (viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks);
    double z1 = b1.getZ() - (viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks);

    double x12 = b2.getX() - (viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks);
    double y12 = b2.getY() - (viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks);
    double z12 = b2.getZ() - (viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks);
    Render.drawFilledBoundingBox(
      new AxisAlignedBB(new BlockPos(x1, y1, z1), new BlockPos(x12, y12 - 1, z12)),
      Color.cyan.getRGB(),
      0.5F
    );
  }*/
}
