package com.mit.commands;

import com.mit.features.foraging.ForgaingMacroMain;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.features.render.RenderMultipleBlocksMod;
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

public class test extends Command {

  AStarPathFinder finder = new AStarPathFinder();
  WalkerMain walker = new WalkerMain();
  ForgaingMacroMain forg = new ForgaingMacroMain();

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle(int x, int y, int z) {
    Vec3 playerPos = BlockUtils.getCenteredVec(Dependencies.mc.thePlayer.getPositionVector());

    RayTracingUtils.CollisionResult firstCollision = RayTracingUtils.getCollisionVecs(
      playerPos.xCoord,
      playerPos.yCoord,
      playerPos.zCoord,
      x,
      y,
      z,
      MathUtils.distanceFromTo(playerPos, new Vec3(x, y, z)),
      new Block[] { Blocks.air, Blocks.tallgrass, Blocks.double_plant }
    );

    ChatUtils.chat(
      String.valueOf(
        BlockUtils.rayTraceVecs(MathUtils.getFourPointsAbout(playerPos.addVector(0, 0.5, 0), new Vec3(x, y, z), 0.4))
      )
    );
  }
}
