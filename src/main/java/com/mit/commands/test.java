package com.mit.commands;

import com.mit.global.Dependencies;
import com.mit.gui.config.Config;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import java.util.Objects;

public class test extends Command {

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle() {
    ChatUtils.chat(BlockUtils.getBlockType(Dependencies.MC.thePlayer.getPosition().add(0, -1, 0)).getUnlocalizedName());
  }
}
