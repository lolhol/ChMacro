package com.mit.commands;

import com.mit.features.mining.hollows.scan.PathMaker;
import com.mit.features.mining.hollows.scan.PathScanner;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.utils.render.PathRender;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderMultipleLines;
import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
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
  PathScanner scanner = new PathScanner(150, 150, 150);
  PathMaker maker = new PathMaker(scanner, 10);

  //PathScanner scanner = new PathScanner(5, 5, 5);

  public StartMacroCommand() {
    super("createPath");
  }

  @DefaultHandler
  public void handle(int x, int y, int z) {
    /*RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    scanner.masterS = false;
    scanner.scan();
    maker.pathSize = 40;
    maker.run();

    new Thread(() -> {
      long start = System.currentTimeMillis();
      while (maker.isRunning) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
      int amt = 0;
      for (BlockPos bp : maker.getRes().getSecond()) {
        amt++;
        RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(bp), true);
      }

      ChatUtils.chat(Dependencies.ModPrefix + " Found Path! Size -> " + amt + ".");
      ChatUtils.chat("Took -> " + (System.currentTimeMillis() - start) + "ms.");
    })
      .start();*/
    ///////////////////////////////////////////////////////////

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
        5,
        100000,
        1000,
        BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector().addVector(-0.5, 0, -0.5)),
        new BlockPos(x, y, z),
        new Block[] { Blocks.air },
        new Block[] { Blocks.air },
        100,
        0
      );

      List<Vec3> path = BlockUtils.shortenList(finder.fromClassToVec(finder.run(newConfig)));

      PathRender.renderPath(path);

      walker.run(path, true);
    })
      .start();
  }
  // /createpath -113 74 -31
}
