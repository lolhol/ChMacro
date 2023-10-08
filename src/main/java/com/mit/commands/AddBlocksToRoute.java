package com.mit.commands;

import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;

public class AddBlocksToRoute extends Command {

  public static List<BlockPos> blocks = new ArrayList<>();

  public AddBlocksToRoute() {
    super("addBlock");
  }

  @DefaultHandler
  public void handle() {
    RenderMultipleBlocksMod.renderMultipleBlocks(
      BlockUtils.fromBPToVec(BlockUtils.fromVecToBP(Dependencies.mc.thePlayer.getPositionVector()).add(0.5, -1, 0.5)),
      true
    );
    blocks.add(Dependencies.mc.thePlayer.getPosition().add(0, -1, 0));
  }
}
