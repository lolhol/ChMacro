package com.mit.features.mining.hollows;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import com.mit.util.RayTracingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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

  public List<BlockPos> run() {
    if (isRunning) return null;

    new Thread(() -> {
      while (scanner.isRunning);

      this.allFound = scanner.foundPos;
      List<BlockPos> resReqRun = reqRun(getMostOptimal(allFound), new ArrayList<>(), 0);
    })
      .start();

    return null;
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
      if (!isAbleToTPBetween(bp, tmpBP)) continue;

      if (depth >= this.pathSize) {
        cur.add(tmpBP);
        return cur;
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
}
