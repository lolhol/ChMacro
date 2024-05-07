package com.mit.commands.route;

import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;

public class AddBlocksToRoute extends Command {

  public AddBlocksToRoute() {
    super("addBlock");
  }

  @DefaultHandler
  public void handle() {
    if (Dependencies.MINING.currentRoute == null) {
      Dependencies.MINING.setRoute("", new ArrayList<>());
    }

    Dependencies.MINING.currentRoute.add(Dependencies.mc.thePlayer.getPosition().add(0, -1, 0));
  }
}
