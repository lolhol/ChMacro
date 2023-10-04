package com.mit.commands;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class StartMacroCommand extends Command {

  public StartMacroCommand() {
    super("macro-start");
  }

  @DefaultHandler
  public void handle() {}
}
