package com.mit.commands.route;

import com.mit.global.Dependencies;
import com.mit.util.ChatUtils;
import com.mit.util.FileUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.util.BlockPos;

public class SaveRouteFileCommand extends Command {

  public SaveRouteFileCommand() {
    super("saveCurRoute");
  }

  @DefaultHandler
  public void handle(String name) {
    try {
      if (!Dependencies.MINING.isNull) {
        Dependencies.MINING.saveRoute(name);
        ChatUtils.chat(Dependencies.MOD.ModPrefix + "Saved " + name + ".mitroute!");
      } else {
        ChatUtils.chat(Dependencies.MOD.ModPrefix + "No route saved!");
      }
    } catch (IOException e) {
      ChatUtils.chat(Dependencies.MOD.ModPrefix + "Failed to save route because of an error!");
      throw new RuntimeException(e);
    }
  }
}
