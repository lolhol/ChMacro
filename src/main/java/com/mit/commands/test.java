package com.mit.commands;

import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import net.minecraft.util.Vec3;

public class test extends Command {

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle(int x, int y, int z) {}
}
