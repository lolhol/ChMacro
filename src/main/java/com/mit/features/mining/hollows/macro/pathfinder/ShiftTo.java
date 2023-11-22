package com.mit.features.mining.hollows.macro.pathfinder;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class ShiftTo {

  public boolean working = false;
  private BlockPos miningNext;

  public ShiftTo(BlockPos miningNext) {
    this.miningNext = miningNext;
  }

  public void run() {}

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
