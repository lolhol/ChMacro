package com.mit.features.mining.hollows.macro;

import com.mit.features.mining.hollows.macro.data.CostUtils;
import com.mit.features.mining.hollows.macro.data.ListUtil;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.MathUtils;
import com.mit.util.RayTracingUtils;
import com.mit.util.RotationUtils;
import java.awt.*;
import java.util.*;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class MiningMacroUtil extends CostUtils {

  public void getAllBlocks(ListUtil lists) {
    int reach = (int) Dependencies.mc.playerController.getBlockReachDistance();
    lists.possibleBreaks = getVeinAround(Dependencies.mc.thePlayer.getPosition(), reach, reach, reach);
  }

  public long getMiningTimeBlock(BlockPos bp, double miningSpeed) {
    double blockStrength = getHardnessBlock(Dependencies.mc.theWorld.getBlockState(bp)) * 30;
    double speedSeconds = blockStrength / miningSpeed / 20;

    return (long) (speedSeconds * 1000);
  }

  public double getHardnessBlock(IBlockState blockState) {
    EnumDyeColor dyeColor = null;
    if (blockState.getBlock() == Blocks.stained_glass) {
      dyeColor = blockState.getValue(BlockStainedGlass.COLOR);
      if (dyeColor == EnumDyeColor.RED) {
        return 2300;
      } else if (dyeColor == EnumDyeColor.PURPLE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.LIME) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.LIGHT_BLUE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.ORANGE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.YELLOW) {
        return 3800;
      } else if (dyeColor == EnumDyeColor.MAGENTA) {
        return 4800;
      }
    }
    if (blockState.getBlock() == Blocks.stained_glass_pane) {
      dyeColor = blockState.getValue(BlockStainedGlassPane.COLOR);
      if (dyeColor == EnumDyeColor.RED) {
        return 2300;
      } else if (dyeColor == EnumDyeColor.PURPLE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.LIME) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.LIGHT_BLUE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.ORANGE) {
        return 3000;
      } else if (dyeColor == EnumDyeColor.YELLOW) {
        return 3800;
      } else if (dyeColor == EnumDyeColor.MAGENTA) {
        return 4800;
      }
    }

    return 0.0;
  }

  public void updateListData(ListUtil lists, float partialTicks, HashSet<BlockPos> broken) {
    int reach = (int) Dependencies.mc.playerController.getBlockReachDistance();
    List<BlockPos> tmp = new ArrayList<>(lists.possibleBreaks);
    tmp.removeIf(b ->
      broken.contains(b) ||
      MathUtils.distanceFromTo(
        BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(b)),
        BlockUtils.getCenteredVec(Dependencies.mc.thePlayer.getPositionVector())
      ) >=
      reach ||
      RayTracingUtils.getPossibleLocDefault(
        Dependencies.mc.thePlayer.getPositionVector(),
        b,
        new Block[] { Blocks.air }
      ) ==
      null
    );
    tmp.sort((a, b) -> Double.compare(getCost(a), getCost(b)));
    lists.currentlyPossibleToSee = tmp;
  }

  private double getCost(BlockPos b) {
    double cost = 0;
    if (BlockUtils.getBlockType(b) == Blocks.stained_glass_pane) {
      cost += paneCost;
    } else {
      cost += fullBlockCost;
    }

    double distance = Math.round(
      MathUtils.distanceFromTo(
        BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(b)),
        BlockUtils.getCenteredVec(Dependencies.mc.thePlayer.getPositionVector())
      )
    );

    cost += distance * distance1MCost;
    double yaw = RotationUtils.getRotation(b).yaw;
    cost += yaw * yawCost;

    return cost;
  }

  public List<BlockPos> getBlocksToBreak(float partialTicks) {
    int reach = (int) Dependencies.mc.playerController.getBlockReachDistance();
    return removeNotInHitDist(
      removeNotVisible(
        Dependencies.mc.thePlayer.getPositionVector(),
        sortBlocks(getVeinAround(Dependencies.mc.thePlayer.getPosition(), reach, reach, reach))
      ),
      partialTicks
    );
  }

  private List<BlockPos> getVeinAround(BlockPos posAround, int addX, int addY, int addZ) {
    return BlockUtils.getBlocksInRadius(
      addX,
      addY,
      addZ,
      posAround,
      new Block[] { Blocks.stained_glass_pane, Blocks.stained_glass }
    );
  }

  private List<BlockPos> sortBlocks(List<BlockPos> init) {
    init.sort((BlockPos a, BlockPos b) -> RotationUtils.getRotation(a).yaw > RotationUtils.getRotation(b).yaw ? 1 : -1);
    return init;
  }

  private List<BlockPos> removeNotVisible(Vec3 playerPos, List<BlockPos> init) {
    init.removeIf(a ->
      BlockUtils.bresenham(
        BlockUtils.getCenteredVec(playerPos.addVector(0, 1.54, 0)),
        BlockUtils.getCenteredVec(BlockUtils.fromBPToVec(a))
      ) ==
      null
    );
    return init;
  }

  private List<BlockPos> removeNotInHitDist(List<BlockPos> init, float partialTicks) {
    init.removeIf(a ->
      MathUtils.distanceFromTo(BlockUtils.fromBPToVec(a), Dependencies.mc.thePlayer.getPositionEyes(partialTicks)) >=
      Dependencies.mc.playerController.getBlockReachDistance()
    );
    return init;
  }
}
