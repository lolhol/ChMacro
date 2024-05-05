package com.mit.AI;

import com.mit.util.BlockUtils;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.nd4j.linalg.api.ndarray.INDArray;

public class AIUtils {

  public static void convertToRelativeData(
    List<BlockPos> data,
    BlockPos center,
    BlockPos curMining,
    int miningProgress,
    INDArray arrayToWriteTo
  ) {
    int cubeSideSize = (int) arrayToWriteTo.shape()[0];

    for (BlockPos cur : data) {
      BlockPos relative = cur.subtract(center);
      int xIndex = relative.getX() + cubeSideSize / 2;
      int yIndex = relative.getY() + cubeSideSize / 2;
      int zIndex = relative.getZ() + cubeSideSize / 2;

      if (
        xIndex >= 0 &&
        xIndex < cubeSideSize &&
        yIndex >= 0 &&
        yIndex < cubeSideSize &&
        zIndex >= 0 &&
        zIndex < cubeSideSize
      ) {
        if (curMining.equals(cur)) {
          arrayToWriteTo.putScalar(new int[] { 1, 1, xIndex, yIndex, zIndex }, miningProgress);
        } else {
          int blockType = BlockUtils.getBlockType(cur) == Blocks.air ? 0 : 1;
          arrayToWriteTo.putScalar(new int[] { 1, 1, xIndex, yIndex, zIndex }, (double) blockType);
        }
      }
    }
  }
}
