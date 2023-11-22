package com.mit.util;

import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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

  public static List<BlockPos> getBlocksInRadius(int x, int y, int z, Vec3 around) {
    List<BlockPos> returnList = new ArrayList<>();

    for (int i = -x; i <= x; i++) {
      for (int j = -y; j <= y; j++) {
        for (int k = -z; k <= z; k++) {
          BlockPos newBlock = fromVecToBP(around.addVector(i, j, k));
          returnList.add(newBlock);
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

  public static Vec3 getClosest(List<Vec3> blocks, Vec3 around) {
    double curClosest = 9999;
    Vec3 block = null;

    for (Vec3 pos : blocks) {
      double distance = MathUtils.distanceFromTo(pos, around);

      if (distance < curClosest) {
        curClosest = distance;
        block = pos;
      }
    }

    return block;
  }

  public static BlockPos getClosest(List<BlockPos> blocks, HashSet<BlockPos> broken, BlockPos around) {
    double curClosest = 9999;
    BlockPos block = null;

    for (BlockPos pos : blocks) {
      if (!broken.contains(pos)) {
        double distance = MathUtils.distanceFromTo(pos, around);

        if (distance < curClosest) {
          curClosest = distance;
          block = pos;
        }
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

  public static int amountNonAir(Iterable<BlockPos> blocks) {
    AtomicInteger air = new AtomicInteger();
    blocks.forEach(i -> {
      if (!isBlockWalkable(i)) {
        air.getAndIncrement();
      }
    });

    return air.get();
  }

  public static Vec3 getCenteredVec(Vec3 init) {
    return init.addVector(0.5, 0, 0.5);
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

  public static boolean isBlockWalkable(BlockPos block) {
    Block blockType = getBlockType(block);
    return (
      blockType == Blocks.air ||
      blockType == Blocks.red_flower ||
      blockType == Blocks.tallgrass ||
      blockType == Blocks.yellow_flower ||
      blockType == Blocks.double_plant
    );
  }

  public static boolean isAbleToWalkBetween(Vec3 start, Vec3 end) {
    return bresenham(start, end) == null && bresenham(start.addVector(0, 1, 0), end.addVector(0, 1, 0)) == null;
    /*return (
      !rayTraceVecs(MathUtils.getFourPointsAbout(start.addVector(0.5, 1, 0.5), end.addVector(0.5, 0.5, 0.5), 0.6)) &&
      !rayTraceVecs(MathUtils.getFourPointsAbout(start.addVector(0.5, 2, 0.5), end.addVector(0.5, 1, 0.5), 0.6))
    );*/
  }

  public static BlockPos bresenham(Vec3 start, Vec3 end) {
    int x1 = MathHelper.floor_double(end.xCoord);
    int y1 = MathHelper.floor_double(end.yCoord);
    int z1 = MathHelper.floor_double(end.zCoord);
    int x0 = MathHelper.floor_double(start.xCoord);
    int y0 = MathHelper.floor_double(start.yCoord);
    int z0 = MathHelper.floor_double(start.zCoord);

    if (isBlockSolid(new BlockPos(x0, y0, z0))) {
      return new BlockPos(x0, y0, z0);
    }

    int iterations = 200;

    while (iterations-- >= 0) {
      //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(new BlockPos(x0, y0, z0)), true);
      if (x0 == x1 && y0 == y1 && z0 == z1) {
        return null; //new BlockPos(end);
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

      //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(new BlockPos(x0, y0, z0)), true);
      if (isBlockSolid(new BlockPos(x0, y0, z0))) {
        return new BlockPos(x0, y0, z0);
      }
    }

    return null;
  }

  public static List<Vec3> shortenList(List<Vec3> original) {
    RenderPoints.renderPoint(null, 0.1, false);
    RenderPoints.renderPoint(null, 0, false);
    List<Vec3> newReturn = new ArrayList<>();
    Vec3 curVector = original.get(0);
    original.remove(0);

    for (int i = 0; i < original.size(); i++) {
      Vec3 cur = original.get(i);
      Vec3 prev = i - 1 < 0 ? null : original.get(i - 1);

      if (
        prev != null && (cur.yCoord > prev.yCoord || cur.yCoord < prev.yCoord) && isValidHigherPoint(fromVecToBP(cur))
      ) {
        newReturn.add(cur);
        curVector = cur;
        continue;
      }

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

  static boolean isValidHigherPoint(BlockPos block) {
    return !getBlockType(block.add(0, 1, 0)).getRegistryName().contains("slab");
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
