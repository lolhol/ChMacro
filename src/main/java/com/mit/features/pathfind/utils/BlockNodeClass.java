package com.mit.features.pathfind.utils;

import com.mit.util.BlockUtils;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@Getter
@AllArgsConstructor
public class BlockNodeClass extends BlockUtils {

  public BlockNodeClass parentOfBlock;
  public BlockPos blockPos;

  // Costs
  public double gCost;
  public double hCost;
  public double totalCost;

  // Other
  public ActionTypes actionType;
  public HashSet<BlockPos> broken;

  public BlockPos blockPos() {
    return this.blockPos;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof BlockNodeClass) {
      return this.blockPos.equals(((BlockNodeClass) other).blockPos);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.blockPos.hashCode();
  }

  public Boolean isSame(BlockPos block) {
    return this.blockPos.equals(block);
  }

  public Vec3 getVec() {
    return fromBPToVec(this.blockPos);
  }
}
