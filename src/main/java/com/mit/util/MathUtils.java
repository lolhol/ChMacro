package com.mit.util;

import com.sun.javafx.geom.Vec2d;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
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

  public static Vec3 getNormalVecBetweenVecsRev(Vec3 vec1, Vec3 vec2) {
    return vec2.subtract(vec1).normalize().rotateYaw(90);
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

  public static Vec3[] getFourPointsAbout(Vec3 vec1, Vec3 vec2, double distBetween) {
    double d1 = vec2.xCoord - vec1.xCoord;
    double d2 = vec2.zCoord - vec1.zCoord;

    double dist = Math.sqrt(d1 * d1 + d2 * d2);

    double revX = -d2;
    double revY = d1;

    double X = (revX / dist) * distBetween;
    double Y = (revY / dist) * distBetween;

    Vec3[] vecs = new Vec3[4];

    vecs[0] = new Vec3(vec1.xCoord - X, vec1.yCoord, vec1.zCoord - Y);
    vecs[1] = new Vec3(X + vec1.xCoord, vec1.yCoord, Y + vec1.zCoord);

    vecs[2] = new Vec3(vec2.xCoord - X, vec2.yCoord, vec2.zCoord - Y);
    vecs[3] = new Vec3(X + vec2.xCoord, vec2.yCoord, Y + vec2.zCoord);

    return vecs;
  }
}
