package com.mit.util;

import com.mit.global.Dependencies;
import com.sun.javafx.geom.Vec3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;

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

  public static BlockPos blocksInFront(HashSet<BlockPos> broken, int maxDistance, HashSet<String> allowed) {
    Vec3 vec = Dependencies.mc.thePlayer.getPositionVector().addVector(0, Dependencies.mc.thePlayer.eyeHeight, 0);
    float pitch = Dependencies.mc.thePlayer.rotationPitch;
    float yaw = Dependencies.mc.thePlayer.rotationYaw;
    double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
    double y = -Math.sin(Math.toRadians(pitch));
    double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

    Vec3 direction = new Vec3(x, y, z);
    Vec3 end = vec.addVector(
      direction.xCoord * maxDistance,
      direction.yCoord * maxDistance,
      direction.zCoord * maxDistance
    );

    MovingObjectPosition result = Dependencies.mc.theWorld.rayTraceBlocks(vec, end, false);

    if (
      result != null &&
      result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
      !broken.contains(result.getBlockPos()) &&
      allowed.contains(getBlockType(result.getBlockPos()).getUnlocalizedName())
    ) {
      return result.getBlockPos();
    }

    return null;
  }

  public static List<BlockPos> getBlocksInRadius(
    int x,
    int y,
    int z,
    BlockPos around,
    HashSet<String> blocks,
    HashSet<BlockPos> alrBroken,
    double distance,
    boolean isDigUnder
  ) {
    List<BlockPos> returnList = new ArrayList<>();

    if (isDigUnder) {
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
    } else {
      BlockPos
        .getAllInBox(around.add(-x, 0, -z), around.add(x, y, z))
        .forEach(i -> {
          if (
            !alrBroken.contains(i) &&
            blocks.contains(getBlockType(i).getUnlocalizedName()) &&
            MathUtils.distanceFromTo(around, i) < distance
          ) {
            returnList.add(i);
          }
        });
    }

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
