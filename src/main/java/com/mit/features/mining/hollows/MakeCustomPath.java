package com.mit.features.mining.hollows;

import com.mit.features.mining.hollows.scan.PathScanner;
import java.util.List;
import net.minecraft.util.BlockPos;

public class MakeCustomPath {

  boolean multi;
  int maxX, maxY;
  PathScanner scanner;
  public boolean isRunning;

  public MakeCustomPath(int maxX, int maxY, int maxZ, boolean multithreading) {
    this.maxX = maxX;
    this.maxY = maxY;
    this.multi = multithreading;

    scanner = new PathScanner(maxX, maxY, maxZ);
    scanner.scan();
    isRunning = true;
  }

  public List<BlockPos> getPath() {
    if (scanner.foundPos.isEmpty() || scanner.isRunning || isRunning) return null;
    return null;
  }

  public List<BlockPos> makePathAlgo() {
    List<BlockPos> blocksFound = scanner.foundPos;
    return null;
  }

  public BlockPos getBestBlock(List<BlockPos> blocks) {
    double bestPerc = 0;
    BlockPos bestBlock;

    for (BlockPos b : blocks) {}
    return null;
  }
}
