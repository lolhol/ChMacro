package com.mit.commands;

import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class test extends Command {

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle() {
    ChatUtils.chat(BlockUtils.getBlockType(Dependencies.mc.thePlayer.getPosition().add(0, -1, 0)).getUnlocalizedName());
  }
}
