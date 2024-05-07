package com.mit.commands.ai;

import com.mit.global.Dependencies;
import com.mit.util.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class AITestingSaveAsFile extends Command {

  public AITestingSaveAsFile() {
    super("saveAIToFile");
  }

  @DefaultHandler
  public void handle() {
    //Dependencies.ai.saveModel();
    //ChatUtils.chat("Model saved to " + Dependencies.ai.getLatestSavedModel());
  }
}
