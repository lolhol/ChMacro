package com.mit.commands.ai;

import com.mit.global.Dependencies;
import com.mit.util.ChatUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import org.jetbrains.annotations.NotNull;

public class AISwitchTrainingTesting extends Command {

  boolean isOn;

  public AISwitchTrainingTesting() {
    super("aiSwitchTrainingTesting");
  }

  @DefaultHandler
  public void onCommand() {
    //Dependencies.ai.setTraining(isOn);
    ChatUtils.chat("Set AI training/testing mode to " + (isOn ? "training" : "testing"));
    isOn = !isOn;
  }
}
