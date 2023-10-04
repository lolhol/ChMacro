package com.mit.gui.config;

import gg.essential.vigilance.Vigilant;

import java.io.File;

public class config extends Vigilant {

  public config() {
    super(new File("./MiningInTwoConf.toml"));
    initialize();
  }
}
