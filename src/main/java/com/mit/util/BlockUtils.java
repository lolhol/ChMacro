package com.mit.util;

import com.mit.global.Dependencies;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class BlockUtils {

  public static List<BlockPos> getBlocksInRadius(int x, int y, int z, BlockPos around, Block[] blocks) {
    List<BlockPos> returnList = new ArrayList<>();

    for (int i = -x; i <= x; i++) {
      for (int j = -y; j <= y; j++) {
        for (int k = -z; k <= z; k++) {
          BlockPos newBlock = around.add(i, j, k);

          if (
            Arrays
              .stream(blocks)
              .anyMatch(block -> {
                return getBlockType(newBlock) == block;
              })
          ) {
            returnList.add(newBlock);
          }
        }
      }
    }

    return returnList;
  }

  public static List<BlockPos> getBlocksInRadius(int x, int y, int z, BlockPos around, HashSet<String> blocks) {
    List<BlockPos> returnList = new ArrayList<>();

    for (int i = -x; i <= x; i++) {
      for (int j = -y; j <= y; j++) {
        for (int k = -z; k <= z; k++) {
          BlockPos newBlock = around.add(i, j, k);

          if (blocks.contains(getBlockType(newBlock).getLocalizedName())) {
            returnList.add(newBlock);
          }
        }
      }
    }

    return returnList;
  }

  public static List<BlockPos> getBlocksInRadius(
    int x,
    int y,
    int z,
    BlockPos around,
    HashSet<String> blocks,
    HashSet<BlockPos> alrBroken
  ) {
    List<BlockPos> returnList = new ArrayList<>();

    for (int i = -x; i <= x; i++) {
      for (int j = -y; j <= y; j++) {
        for (int k = -z; k <= z; k++) {
          BlockPos newBlock = around.add(i, j, k);

          if (!alrBroken.contains(newBlock) && blocks.contains(getBlockType(newBlock).getUnlocalizedName())) {
            returnList.add(newBlock);
          }
        }
      }
    }

    return returnList;
  }

  public static Block getBlockType(BlockPos blockPos) {
    return getBlockState(blockPos).getBlock();
  }

  public static IBlockState getBlockState(BlockPos blockPos) {
    return Dependencies.MC.theWorld.getBlockState(blockPos);
  }
}
