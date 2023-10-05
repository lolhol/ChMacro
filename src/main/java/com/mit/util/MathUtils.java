package com.mit.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class MathUtils {

  public static double distanceFromTo(BlockPos pos1, BlockPos pos2) {
    double d1 = pos1.getX() - pos2.getX();
    double d2 = pos1.getY() - pos2.getY();
    double d3 = pos1.getZ() - pos2.getZ();

    return java.lang.Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
  }

  public static double distanceFromTo(Vec3 pos1, Vec3 pos2) {
    double d1 = pos1.xCoord - pos2.xCoord;
    double d2 = pos1.yCoord - pos2.yCoord;
    double d3 = pos1.zCoord - pos2.zCoord;

    return java.lang.Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
  }

  public static double distanceFromToXZ(BlockPos pos1, BlockPos pos2) {
    final double d1 = pos1.getX() - pos2.getX();
    final double d2 = pos1.getZ() - pos2.getZ();
    return MathHelper.sqrt_double(d1 * d1 + d2 * d2);
  }

  public static double distanceFromToXZ(Vec3 vec1, Vec3 vec2) {
    final double d1 = vec1.xCoord - vec2.xCoord;
    final double d2 = vec1.zCoord - vec2.zCoord;
    return MathHelper.sqrt_double(d1 * d1 + d2 * d2);
  }
}