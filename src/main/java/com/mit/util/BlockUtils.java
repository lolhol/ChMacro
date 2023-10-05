package com.mit.util;

import com.mit.global.Dependencies;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

  public static List<BlockPos> getBlocksInRadius(
    int x,
    int y,
    int z,
    BlockPos around,
    HashSet<String> blocks,
    HashSet<BlockPos> alrBroken,
    double distance
  ) {
    List<BlockPos> returnList = new ArrayList<>();

    BlockPos
      .getAllInBox(around.add(-x, -y, -z), around.add(x, y, z))
      .forEach(i -> {
        if (
          !alrBroken.contains(i) &&
          blocks.contains(getBlockType(i).getUnlocalizedName()) &&
          MathUtils.distanceFromTo(around, i) < distance
        ) {
          returnList.add(i);
        }
      });

    return returnList;
  }

  public static BlockPos getBlocksInFront(double reach) {
    double playerX = Dependencies.mc.thePlayer.posX;
    double playerY = Dependencies.mc.thePlayer.posY + Dependencies.mc.thePlayer.getEyeHeight();
    double playerZ = Dependencies.mc.thePlayer.posZ;

    float yaw = Dependencies.mc.thePlayer.rotationYaw;
    float pitch = Dependencies.mc.thePlayer.rotationPitch;

    double directionX = -Math.sin(Math.toRadians(yaw));
    double directionY = -Math.sin(Math.toRadians(pitch));
    double directionZ = Math.cos(Math.toRadians(yaw));

    BlockPos blockPos = new BlockPos(playerX + directionX, playerY + directionY, playerZ + directionZ);

    EnumFacing facing = EnumFacing.getHorizontal((int) yaw % 360);
    blockPos = blockPos.offset(facing);

    if (MathUtils.distanceFromTo(blockPos, Dependencies.mc.thePlayer.getPosition()) < reach) {
      return blockPos;
    }

    return null;
  }

  public static Block getBlockType(BlockPos blockPos) {
    return getBlockState(blockPos).getBlock();
  }

  public static IBlockState getBlockState(BlockPos blockPos) {
    return Dependencies.mc.theWorld.getBlockState(blockPos);
  }

  public static BlockPos getClosest(List<BlockPos> blocks, BlockPos around) {
    double curClosest = 9999;
    BlockPos block = null;

    for (BlockPos pos : blocks) {
      double distance = MathUtils.distanceFromTo(pos, around);

      if (distance < curClosest) {
        curClosest = distance;
        block = pos;
      }
    }

    return block;
  }
}
