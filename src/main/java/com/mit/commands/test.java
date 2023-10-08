package com.mit.commands;

import com.mit.features.foraging.ForgaingMacroMain;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderMultipleLines;
import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import com.mit.util.RayTracingUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class test extends Command {

  AStarPathFinder finder = new AStarPathFinder();
  WalkerMain walker = new WalkerMain();
  ForgaingMacroMain forg = new ForgaingMacroMain();

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle(int x, int y, int z) {
    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    RenderMultipleLines.renderMultipleLines(null, null, false);
    RenderPoints.renderPoint(null, 0.1, false);

    BlockPos block = new BlockPos(x, y, z);
    BlockPos blockPos = BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector().addVector(-0.5, 0, -0.5));

    Vec3 perpNorm = MathUtils.getNormalVecBetweenVecsRev(
      BlockUtils.fromBPToVec(block.add(-0.5, 0, -0.5)),
      BlockUtils.fromBPToVec(blockPos.add(-0.5, 0, -0.5))
    );

    BlockPos b01 = block.add(perpNorm.xCoord * 2, 0, perpNorm.zCoord * 2);
    BlockPos b02 = block.add(-perpNorm.xCoord, 0, perpNorm.zCoord);
    BlockPos b11 = block.add(perpNorm.xCoord * 2, 1, perpNorm.zCoord * 2);
    BlockPos b12 = block.add(-perpNorm.xCoord, 1, perpNorm.zCoord);

    RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(block), true);

    RenderMultipleLines.renderMultipleLines(block.add(0, 1, 0), b11.add(0, 1, 0), true);
    RenderMultipleLines.renderMultipleLines(block.add(0, 1, 0), b12.add(0, 1, 0), true);

    //RenderPoints.renderPoint(BlockUtils.fromBPToVec(b01), 0.1, true);

    ChatUtils.chat(
      String.valueOf(
        !BlockUtils.isBlockSolid(b01) &&
        !BlockUtils.isBlockSolid(b02) &&
        !BlockUtils.isBlockSolid(b11) &&
        !BlockUtils.isBlockSolid(b12)
      )
    );
  }
}
