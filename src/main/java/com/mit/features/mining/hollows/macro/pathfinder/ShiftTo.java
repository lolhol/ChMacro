package com.mit.features.mining.hollows.macro.pathfinder;

import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.global.Dependencies;
import com.mit.util.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
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

public class ShiftTo {

  public boolean working = false;
  private BlockPos miningNext;
  private AStarPathFinder finder = new AStarPathFinder();
  private List<Vec3> path = null;
  private List<BlockPos> blocksUnder = null;

  Vec3 curPosWalking = null;

  public ShiftTo(BlockPos miningNext) {
    this.miningNext = miningNext;
  }

  public void run() {
    MinecraftForge.EVENT_BUS.register(this);
    new Thread(() -> {
      working = true;
      BlockPos best = null;
      double bestDist = 10000;
      blocksUnder = getBlocksUnder();
      blocksUnder.sort(Comparator.comparingInt((BlockPos a) -> a.getX()).thenComparingInt(Vec3i::getZ));
      for (BlockPos b : blocksUnder) {
        double dist = MathUtils.distanceFromTo(b, miningNext);
        if (dist < bestDist) {
          bestDist = dist;
          best = b;
        }
      }

      path =
        BlockUtils.shortenList(
          finder.fromClassToVec(
            finder.run(
              finder.getWalkConfig(
                BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()),
                best.add(0, 1, 0)
              )
            )
          )
        );
    })
      .start();
  }

  public void stop() {
    KeyBindHandler.resetKeybindState();
    MinecraftForge.EVENT_BUS.unregister(this);
    working = false;
    path = null;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!working || path == null) return;

    Vec3 plyLoc = Dependencies.mc.thePlayer.getPositionVector();
    if (this.curPosWalking == null || MathUtils.distanceFromToXZ(this.curPosWalking, plyLoc) < 0.2) {
      if (path.isEmpty()) {
        this.path = null;
        this.stop();
        return;
      }

      this.curPosWalking = path.remove(0);
      return;
    }

    KeyBindHandler.resetKeybindState();
    for (KeyBinding k : VecUtils.getNeededKeyPresses(plyLoc, curPosWalking)) {
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
