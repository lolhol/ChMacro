package com.mit.commands;

import com.mit.gui.config.Config;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import java.util.Objects;

public class OpenGUI extends Command {

  public OpenGUI() {
    super("mitMenu");
  }

  @DefaultHandler
  public void handle() {
    GuiUtil.open(Objects.requireNonNull(Config.INSTANCE.gui()));
  }
}
