package com.mit.features.mining.hollows;

import static com.mit.util.RayTracingUtils.*;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import com.mit.util.RayTracingUtils;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;

public class PathMaker {

  private final PathScanner scanner;
  private List<BlockPos> returnPath = new ArrayList<>();
  private boolean isRunning = false;
  private List<BlockPos> allFound;

  private Block[] blocks2Ignore = new Block[] {
    Blocks.lapis_ore,
    Blocks.diamond_ore,
    Blocks.iron_ore,
    Blocks.coal_ore,
    Blocks.wool,
    Blocks.air,
    Blocks.stone,
  };

  private int pathSize = 0;

  public PathMaker(PathScanner scanner, final int pathSize) {
    this.scanner = scanner;
    this.pathSize = pathSize;
  }

  public void run() {
    if (isRunning) return;

    new Thread(() -> {
      while (scanner.isRunning);

      this.allFound = scanner.foundPos;
      returnPath = reqRun(getMostOptimal(allFound), new ArrayList<>(), 0);
      isRunning = false;
    })
      .start();
  }

  public Tuple<Boolean, List<BlockPos>> getRes() {
    return new Tuple<>(this.isRunning, this.returnPath);
  }

  private List<BlockPos> reqRun(BlockPos bp, List<BlockPos> cur, int depth) {
    for (BlockPos tmpBP : allFound
      .stream()
      .filter(a -> {
        return MathUtils.distanceFromTo(bp, a) <= 52;
      })
      .sorted(
        (
          (a, b) -> {
            return Double.compare(getGemSaturation(a), getGemSaturation(b));
          }
        )
      )
      .collect(Collectors.toList())) {
      if (!cur.isEmpty() && MathUtils.distanceFromTo(cur.get(0), tmpBP) > 150) continue;
      if (!isAbleToTPBetween(bp, tmpBP) || cur.contains(tmpBP)) continue;

      if (depth >= this.pathSize) {
        if (isAbleToTPBetween(cur.get(0), tmpBP)) {
          cur.add(tmpBP);
          return cur;
        }
      } else {
        List<BlockPos> reqRunRes = reqRun(tmpBP, cur, depth++);
        if (reqRunRes != null) {
          return reqRunRes;
        }
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

  // TODO: Might need to re-code @this bc saturation diff
  private double getGemSaturation(BlockPos bp) {
    double total = 0;
    double glassTotal = 0;
    for (BlockPos b : BlockPos.getAllInBox(bp.add(-2, -2, -2), bp.add(2, 2, 2))) {
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
    Vec3 result = RayTracingUtils.adjustLook(BlockUtils.fromBPToVec(bp1), bp2, blocks2Ignore, false);
    return result != null;
  }

  // TODO: Make this custom raytracing goog :) || Bresingham implementation l8tr?
  private Vec3 getCollision(Vec3 block1, BlockPos destBlock, Block[] blocksToIgnore, boolean isCheck) {
    double playerHeight = 1.54;

    Vec3 destBlockCenter = new Vec3(destBlock.getX() + 0.5, destBlock.getY() + 0.5, destBlock.getZ() + 0.5);

    double distToBlockCenter = MathUtils.distanceFromTo(
      new Vec3(block1.xCoord, block1.yCoord + playerHeight, block1.zCoord),
      destBlockCenter
    );

    //test

    CollisionResult collision =
      this.getCollisionBlock(
          block1.xCoord,
          block1.yCoord + playerHeight,
          block1.zCoord,
          destBlockCenter.xCoord,
          destBlockCenter.yCoord,
          destBlockCenter.zCoord,
          distToBlockCenter
        );

    if (collision == null) {
      return null;
    }

    double radiusStep = 0.1;
    double radiusMax = Math.sqrt(3) / 2 + radiusStep;

    for (double radius = radiusStep; radius < radiusMax; radius += radiusStep) {
      double angleStep = (radiusMax / radius) * 5;
      for (double angle = 0; angle < 360 + angleStep; angle += angleStep) {
        Vec3 vec = getCylinderBaseVec(
          new double[] { block1.xCoord, block1.yCoord + playerHeight, block1.zCoord },
          new double[] { destBlockCenter.xCoord, destBlockCenter.yCoord, destBlockCenter.zCoord },
          angle,
          radius
        );

        Vec3 point = new Vec3(
          destBlockCenter.xCoord + vec.xCoord,
          destBlockCenter.yCoord + vec.yCoord,
          destBlockCenter.zCoord + vec.zCoord
        );

        CollisionResult collisionPoint =
          this.getCollisionBlock(
              block1.xCoord,
              block1.yCoord + playerHeight,
              block1.zCoord,
              point.xCoord,
              point.yCoord,
              point.zCoord,
              distToBlockCenter + Math.sqrt(3) / 2
            );

        try {
          if (collisionPoint != null && collisionPoint.blockPos.equals(destBlock)) {
            return point;
          }
        } catch (Exception ignored) {}
      }
    }

    return null;
  }

  private CollisionResult getCollisionBlock(
    double x1,
    double y1,
    double z1,
    double x2,
    double y2,
    double z2,
    double maxDist
  ) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double dz = z2 - z1;

    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

    double stepX = dx / length;
    double stepY = dy / length;
    double stepZ = dz / length;

    double xCur = x1;
    double yCur = y1;
    double zCur = z1;

    List<CollisionResult> collidingBlocks = new ArrayList<>();
    for (int i = 0; i < maxDist + 1; i++) {
      for (int sx = -1; sx <= 1; sx++) {
        for (int sy = -1; sy <= 1; sy++) {
          for (int sz = -1; sz <= 1; sz++) {
            if (sx * stepX + sy * stepY + sz * stepZ < 0) {
              continue;
            }

            int x = (int) Math.floor(xCur + sx);
            int y = (int) Math.floor(yCur + sy);
            int z = (int) Math.floor(zCur + sz);

            IBlockState blockState = Dependencies.mc.theWorld.getBlockState(new BlockPos(x, y, z));
            Block block = blockState.getBlock();

            double[] ro = new double[] { x1, y1, z1 };
            double[] rd = new double[] { stepX, stepY, stepZ };
            double[][] aabb = new double[][] { { x, y, z }, { x + 1, y + 1, z + 1 } };
            double[] output = intersection(ro, rd, aabb);
            if (output == null) {
              continue;
            }

            if (getDistanceB(ro, new double[] { x + 0.5, y + 0.5, z + 0.5 }) > maxDist) {
              continue;
            }

            collidingBlocks.add(new CollisionResult(new BlockPos(x, y, z), output));
          }
        }
      }

      xCur += stepX;
      yCur += stepY;
      zCur += stepZ;
    }

    collidingBlocks.sort(Comparator.comparingDouble(a -> getDistanceB(new double[] { x1, y1, z1 }, a.output)));
    return !collidingBlocks.isEmpty() ? collidingBlocks.get(0) : null;
  }
}
