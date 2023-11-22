package com.mit.features.mining.hollows.macro.pathfinder;

import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.KeyBindHandler;
import com.mit.util.MathUtils;
import com.mit.util.VecUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ShiftTo {

  public boolean working = false;
  private BlockPos miningNext;
  private AStarPathFinder finder = new AStarPathFinder();
  private List<Vec3> path = null;

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
      for (BlockPos b : getBlocksUnder()) {
        double dist = MathUtils.distanceFromTo(b, miningNext);
        if (dist < bestDist) {
          bestDist = dist;
          best = b;
        }
      }

      path =
        finder.fromClassToVec(
          finder.run(finder.getWalkConfig(BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()), best))
        );
    })
      .start();
  }

  public void stop() {
    MinecraftForge.EVENT_BUS.unregister(this);
    working = false;
    path = null;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!working || path == null) return;

    Vec3 plyLoc = Dependencies.mc.thePlayer.getPositionVector();
    if (MathUtils.distanceFromToXZ(this.curPosWalking, plyLoc) < 0.5) {
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
}
