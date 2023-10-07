package com.mit.util;

import com.mit.global.Dependencies;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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

  public static double percentNonAir(Iterable<BlockPos> blocks) {
    AtomicInteger air = new AtomicInteger();
    AtomicInteger nonAir = new AtomicInteger();

    blocks.forEach(i -> {
      if (getBlockType(i) == Blocks.air) {
        air.getAndIncrement();
      } else {
        nonAir.getAndIncrement();
      }
    });

    if (nonAir.get() == 0) {
      return 0;
    } else {
      return ((double) air.get() / nonAir.get()) * 100;
    }
  }

  public static int amountAir(Iterable<BlockPos> blocks) {
    AtomicInteger air = new AtomicInteger();
    blocks.forEach(i -> {
      if (getBlockType(i) != Blocks.air) {
        air.getAndIncrement();
      }
    });

    return air.get();
  }

  public static boolean isBlockSolid(BlockPos block) {
    Block blockType = getBlockType(block);
    return (
      blockType != Blocks.water &&
      blockType != Blocks.lava &&
      blockType != Blocks.air &&
      blockType != Blocks.red_flower &&
      blockType != Blocks.tallgrass &&
      blockType != Blocks.yellow_flower &&
      blockType != Blocks.double_plant &&
      blockType != Blocks.flowing_water
    );
  }

  public static boolean isAbleToWalkBetween(Vec3 start, Vec3 end) {
    Vec3[] vecs = MathUtils.getFourPointsAbout(start, end, 0.5);

    MovingObjectPosition firstCollision = Dependencies.mc.theWorld.rayTraceBlocks(vecs[0], vecs[1], true, true, true);

    if (firstCollision != null) return false;

    MovingObjectPosition secondCollision = Dependencies.mc.theWorld.rayTraceBlocks(vecs[2], vecs[3], true, true, true);

    if (secondCollision != null) return false;

    MovingObjectPosition finalCollision = Dependencies.mc.theWorld.rayTraceBlocks(start, end, true, true, true);

    return finalCollision == null;
  }

  public static List<Vec3> shortenList(List<Vec3> original) {
    List<Vec3> newReturn = new ArrayList<>();
    Vec3 curVector = original.get(0);
    original.remove(0);

    for (int i = 0; i < original.size(); i++) {
      Vec3 cur = original.get(i);
      Vec3 prev = i - 1 < 0 ? null : original.get(i - 1);

      /*
      List<RayTracingUtils.CollisionResult> blocksIntersected = RayTracingUtils.getCollisionVecsList(
        curVector.xCoord + 0.5,
        curVector.yCoord - 1.5,
        curVector.zCoord + 0.5,
        cur.xCoord,
        cur.yCoord - 1.5,
        cur.zCoord,
        MathUtils.distanceFromTo(curVector, cur)
      );

      if (blocksIntersected != null) {
        int airAmount = 0;
        for (RayTracingUtils.CollisionResult block : blocksIntersected) {
          Block blockType = BlockUtils.getBlockType(block.blockPos);
          if (blockType == Blocks.air) {
            airAmount++;
          }
        }

        if (airAmount > 2) {
          if (prev == null) {
            newReturn.add(cur);
          } else {
            newReturn.add(prev);
          }

          curVector = cur;

          continue;
        }
      }*/

      if (!isAbleToWalkBetween(curVector, cur)) {
        if (prev == null) {
          newReturn.add(cur);
        } else {
          newReturn.add(prev);
        }

        curVector = cur;
      }
    }

    newReturn.add(original.get(original.size() - 1));

    return newReturn;
  }

  public static BlockPos fromVecToBP(Vec3 block) {
    return new BlockPos(block.xCoord, block.yCoord, block.zCoord);
  }

  public static Vec3 fromBPToVec(BlockPos block) {
    return new Vec3(block.getX(), block.getY(), block.getZ());
  }
  /*public static List<BlockPos> getShortList(List<BlockNodeClass> blocks) {
    boolean added = false;

    List<BlockPos> returnBlocks = new ArrayList<>();
    blocks.remove(0);
    BlockPos curBlock = BlockUtils.getCenteredBlock(blocks.get(0).blockPos);
    //returnBlocks.add(curBlock);

    List<Vec3> pointList = new ArrayList<>();
    pointList.add(new Vec3(BlockSideVecs.LEFT.dx, 0, BlockSideVecs.LEFT.dz));
    pointList.add(new Vec3(BlockSideVecs.RIGHT.dx, 0, BlockSideVecs.RIGHT.dz));
    pointList.add(new Vec3(BlockSideVecs.BACKLEFT.dx, 0, BlockSideVecs.BACKLEFT.dz));
    pointList.add(new Vec3(BlockSideVecs.BACKRIGHT.dx, 0, BlockSideVecs.BACKRIGHT.dz));
    pointList.add(new Vec3(0, 0, 0));

    int curCount = 0;
    for (int i = 1; i < blocks.size(); i++) {
      BlockNodeClass curBlockClassNode = blocks.get(i);
      BlockPos curBlockArList = blocks.get(i).blockPos;
      BlockPos centered = BlockUtils.getCenteredBlock(curBlockArList);

      List<RayTracingUtils.CollisionResult> blocksIntersected = RayTracingUtils.getCollisionVecsList(
        curBlock.getX() + 0.5,
        curBlock.getY() - 1.5,
        curBlock.getZ() + 0.5,
        centered.getX(),
        centered.getY() - 1.5,
        centered.getZ(),
        DistanceFromTo.distanceFromTo(curBlock, centered)
      );

      if (blocksIntersected != null) {
        int airAmount = 0;
        for (RayTracingUtils.CollisionResult block : blocksIntersected) {
          Block blockType = BlockUtils.getBlockType(block.blockPos);
          if (blockType == Blocks.air) {
            airAmount++;
          }
        }

        //SendChat.chat(String.valueOf(airAmount));

        if (airAmount > 2) {
          returnBlocks.add(blocks.get(i - 1).blockPos);
          curBlock = blocks.get(i - 1).blockPos;
          curCount = 0;
          continue;
        }
      }

      if (added) {
        added = false;
        returnBlocks.add(blocks.get(i - 1).blockPos);
        curBlock = blocks.get(i - 1).blockPos;
      }

      for (Vec3 vec : pointList) {
        Vec3 cur = new Vec3(
          curBlockArList.getX() + vec.xCoord + 0.5,
          curBlockArList.getY() + 1.1,
          curBlockArList.getZ() + vec.zCoord + 0.5
        );

        //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBlockPosToVec3(curBlockArList), true);

        Vec3 vec3 = new Vec3(
          curBlock.getX() + vec.xCoord + 0.5,
          curBlock.getY() + 1.1,
          curBlock.getZ() + vec.zCoord + 0.5
        );

        MovingObjectPosition obj = ids.mc.theWorld.rayTraceBlocks(vec3, cur, true, true, true);

        //RenderPoints.renderPoint(vec3, 0.2, true);

        if (
          (obj != null && !obj.hitVec.equals(cur)) ||
          curBlockClassNode.actionType == ActionTypes.BREAK ||
          curBlockClassNode.actionType == ActionTypes.JUMP
        ) {
          returnBlocks.add(blocks.get(i - 1).blockPos);
          curBlock = blocks.get(i - 1).blockPos;

          curCount = 0;
          break;
        }
      }
      /*if (curCount >= 4) {
        curCount = 0;
        returnBlocks.add(blocks.get(i - 1).blockPos);
        curBlock = blocks.get(i - 1).blockPos;
        continue;
      }

      curCount++;
    }

    returnBlocks.add(blocks.get(blocks.size() - 1).blockPos);

    return returnBlocks;
  }*/
}
