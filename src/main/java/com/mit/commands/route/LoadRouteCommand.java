package com.mit.commands.route;

import com.mit.global.Dependencies;
import com.mit.util.ChatUtils;
import com.mit.util.FileUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

public class LoadRouteCommand extends Command {

  public LoadRouteCommand() {
    super("loadRoute");
  }

  @DefaultHandler
  public void handle(String name) {
    try {
      if (!Dependencies.MINING.isNull) {
        Dependencies.MINING.loadRoute(name);
        ChatUtils.chat(Dependencies.MOD.ModPrefix + "Loaded " + name + ".mitroute!");
      } else {
        ChatUtils.chat(Dependencies.MOD.ModPrefix + "No route loaded!");
      }
    } catch (IOException e) {
      ChatUtils.chat(Dependencies.MOD.ModPrefix + "Failed to load route because of an error!");
      throw new RuntimeException(e);
    }
  }
}
