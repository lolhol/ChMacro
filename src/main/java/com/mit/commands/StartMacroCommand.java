package com.mit.commands;

import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderMultipleLines;
import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class StartMacroCommand extends Command {

  AStarPathFinder finder = new AStarPathFinder();
  WalkerMain walker = new WalkerMain();

  public StartMacroCommand() {
    super("macroWalk");
  }

  @DefaultHandler
  public void handle(int x, int y, int z) {
    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    RenderPoints.renderPoint(null, 0.1, false);
    RenderMultipleLines.renderMultipleLines(null, null, false);

    new Thread(() -> {
      PathFinderConfig newConfig = new PathFinderConfig(
        false,
        false,
        false,
        false,
        false,
        10,
        100000,
        1000,
        BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector().addVector(-0.5, 0, -0.5)),
        new BlockPos(x, y, z),
        new Block[] { Blocks.air },
        new Block[] { Blocks.air },
        100,
        0
      );

      List<Vec3> path = finder.fromClassToVec(finder.run(newConfig));
      ChatUtils.chat("Done!");

      path.forEach(i -> {
        RenderMultipleBlocksMod.renderMultipleBlocks(i, true);
      });
      //BlockUtils.shortenList(

      walker.run(path, true);
    })
      .start();
  }
}
