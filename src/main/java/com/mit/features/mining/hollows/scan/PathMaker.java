package com.mit.features.mining.hollows.scan;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;

public class PathMaker {

  private final PathScanner scanner;
  private List<BlockPos> returnPath = new ArrayList<>();
  public boolean isRunning = false;
  private List<BlockPos> allFound;

  private Thread curScanThread = null;

  private Block[] blocks2Ignore = new Block[] {
    Blocks.lapis_ore,
    Blocks.diamond_ore,
    Blocks.iron_ore,
    Blocks.coal_ore,
    Blocks.wool,
    Blocks.air,
    Blocks.stone,
  };

  public int pathSize = 0;

  public PathMaker(PathScanner scanner, final int pathSize) {
    this.scanner = scanner;
    this.pathSize = pathSize;
  }

  public void run() {
    if (curScanThread != null) {
      curScanThread.stop();
    }

    this.isRunning = true;
    curScanThread =
      new Thread(() -> {
        while (scanner.isRunning) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        ChatUtils.chat(Dependencies.MOD.ModPrefix + " Scanner done running!");

        this.allFound = scanner.foundPos;
        returnPath = reqRun(getMostOptimal(allFound), new ArrayList<>(), 0);
        ChatUtils.chat("Done!");
        this.isRunning = false;
      });

    curScanThread.start();
  }

  public Tuple<Boolean, List<BlockPos>> getRes() {
    return new Tuple<>(!this.isRunning, this.returnPath);
  }

  private List<BlockPos> reqRun(BlockPos bp, List<BlockPos> cur, int depth) {
    int depthMut = depth + 1;
    for (BlockPos tmpBP : allFound
      .stream()
      .filter(a -> MathUtils.distanceFromTo(bp, a) <= 52)
      .sorted((a, b) -> Double.compare(getGemSaturation(a), getGemSaturation(b)))
      .collect(Collectors.toList())) {
      if ((!cur.isEmpty() && MathUtils.distanceFromTo(cur.get(0), tmpBP) > 150) || bp == tmpBP) continue;
      if (!isAbleToTPBetween(bp, tmpBP) || cur.contains(tmpBP)) continue;
      //ChatUtils.chat(String.valueOf(allFound.size()));

      //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(tmpBP), true);

      if (depthMut >= this.pathSize) {
        if (isAbleToTPBetween(tmpBP, cur.get(0))) {
          cur.add(tmpBP);
          return cur;
        }

        continue;
      }

      cur.add(tmpBP);
      List<BlockPos> reqRunRes = reqRun(tmpBP, cur, depthMut);
      if (reqRunRes != null) {
        return reqRunRes;
      }
    }

    return null;
  }

  private BlockPos getMostOptimal(List<BlockPos> blocks) {
    double bestSat = 0;
    BlockPos best = null;

    for (BlockPos block : blocks) {
      double gemSat = getGemSaturation(block);
      if (gemSat > bestSat) {
        bestSat = gemSat;
        best = block;
      }
    }

    return best;
  }

  // TODO: Might need to re-code @this bc of speed
  private double getGemSaturation(BlockPos bp) {
    double total = 0;
    double glassTotal = 0;
    float plyReach = Dependencies.mc.playerController.getBlockReachDistance();
    for (BlockPos b : (BlockUtils.getBlocksInRadius(5, 5, 5, BlockUtils.fromBPToVec(bp))).stream()
      .filter(a ->
        MathUtils.distanceFromTo(Dependencies.mc.thePlayer.getPositionVector(), BlockUtils.fromBPToVec(a)) < plyReach
      )
      .collect(Collectors.toList())) {
      total++;

      if (BlockUtils.getBlockType(b) == Blocks.stained_glass_pane) {
        glassTotal += 0.5;
      } else {
        glassTotal++;
      }
    }

    return glassTotal / total;
  }

  private boolean isGlass(Block b) {
    return b == Blocks.stained_glass || b == Blocks.stained_glass_pane;
  }

  private boolean isAbleToTPBetween(BlockPos bp1, BlockPos bp2) {
    /*Vec3 result = RayTracingUtils.adjustLook(BlockUtils.fromBPToVec(bp1), bp2, blocks2Ignore, false);
    return result != null;*/
    return isAbleToTPBetween(BlockUtils.fromBPToVec(bp1).addVector(0, 1.5, 0), BlockUtils.fromBPToVec(bp2));
  }

  private boolean isAbleToTPBetween(Vec3 start, Vec3 end) {
    int x1 = MathHelper.floor_double(end.xCoord);
    int y1 = MathHelper.floor_double(end.yCoord);
    int z1 = MathHelper.floor_double(end.zCoord);
    int x0 = MathHelper.floor_double(start.xCoord);
    int y0 = MathHelper.floor_double(start.yCoord);
    int z0 = MathHelper.floor_double(start.zCoord);

    int iterations = 200;

    while (iterations-- >= 0) {
      if (x0 == x1 && y0 == y1 && z0 == z1) {
        return true;
      }

      boolean hasNewX = true;
      boolean hasNewY = true;
      boolean hasNewZ = true;

      double newX = 999.0;
      double newY = 999.0;
      double newZ = 999.0;

      if (x1 > x0) {
        newX = (double) x0 + 1.0;
      } else if (x1 < x0) {
        newX = (double) x0 + 0.0;
      } else {
        hasNewX = false;
      }
      if (y1 > y0) {
        newY = (double) y0 + 1.0;
      } else if (y1 < y0) {
        newY = (double) y0 + 0.0;
      } else {
        hasNewY = false;
      }
      if (z1 > z0) {
        newZ = (double) z0 + 1.0;
      } else if (z1 < z0) {
        newZ = (double) z0 + 0.0;
      } else {
        hasNewZ = false;
      }

      double stepX = 999.0;
      double stepY = 999.0;
      double stepZ = 999.0;

      double dx = end.xCoord - start.xCoord;
      double dy = end.yCoord - start.yCoord;
      double dz = end.zCoord - start.zCoord;

      if (hasNewX) {
        stepX = (newX - start.xCoord) / dx;
      }
      if (hasNewY) {
        stepY = (newY - start.yCoord) / dy;
      }
      if (hasNewZ) {
        stepZ = (newZ - start.zCoord) / dz;
      }
      if (stepX == -0.0) {
        stepX = -1.0E-4;
      }
      if (stepY == -0.0) {
        stepY = -1.0E-4;
      }
      if (stepZ == -0.0) {
        stepZ = -1.0E-4;
      }

      EnumFacing enumfacing;
      if (stepX < stepY && stepX < stepZ) {
        enumfacing = x1 > x0 ? EnumFacing.WEST : EnumFacing.EAST;
        start = new Vec3(newX, start.yCoord + dy * stepX, start.zCoord + dz * stepX);
      } else if (stepY < stepZ) {
        enumfacing = y1 > y0 ? EnumFacing.DOWN : EnumFacing.UP;
        start = new Vec3(start.xCoord + dx * stepY, newY, start.zCoord + dz * stepY);
      } else {
        enumfacing = z1 > z0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
        start = new Vec3(start.xCoord + dx * stepZ, start.yCoord + dy * stepZ, newZ);
      }
      x0 = MathHelper.floor_double(start.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
      y0 = MathHelper.floor_double(start.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
      z0 = MathHelper.floor_double(start.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
      if (
        !isInIgnore(BlockUtils.getBlockType(new BlockPos(x0, y0, z0))) &&
        MathUtils.distanceFromTo(start, new Vec3(x0, y0, z0)) > 6
      ) {
        return false;
      }
    }

    return true;
  }

  private boolean isInIgnore(Block b) {
    for (Block bt : blocks2Ignore) {
      if (bt == b) return true;
    }

    return false;
  }
}
