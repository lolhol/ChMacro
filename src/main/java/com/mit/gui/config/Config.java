package com.mit.gui.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

  public static Config INSTANCE = new Config();

  @Property(
    type = PropertyType.SWITCH,
    name = "Metal Detector Solver",
    description = "What it seems. (gumtune's)",
    category = "Mining",
    subcategory = "QOL"
  )
  public static boolean metalDetectorSolver = false;

  @Property(
    type = PropertyType.SWITCH,
    name = "Nuker Master Switch",
    description = "The nuker master switch that turns the nuker on/off.",
    category = "Mining",
    subcategory = "Nuker"
  )
  public static boolean nuker = false;

  @Property(
    type = PropertyType.SWITCH,
    name = "Mode",
    description = "On = when you look at a block it breaks it | off = blocks around you are broken",
    category = "Mining",
    subcategory = "Nuker"
  )
  public static boolean nukerMode = false;

  @Property(
    type = PropertyType.SWITCH,
    name = "Dig Under",
    description = "Turn this off to not dig under player.",
    category = "Mining",
    subcategory = "Nuker"
  )
  public static boolean isDigUnder = false;

  @Property(
    type = PropertyType.SLIDER,
    name = "Nuker BPS",
    description = "dont set this too high and ur chilling",
    category = "Mining",
    subcategory = "Nuker",
    min = 1,
    max = 80
  )
  public static int nukerBPS = 20;

  @Property(
    type = PropertyType.TEXT,
    name = "Nuker Blocks",
    description = "Put the blocks you want the nuker to break here IN THE FORM OF name,name NO SPACE BETWEEN , !",
    category = "Mining",
    subcategory = "Nuker"
  )
  public static String nukerBlocks = "";

  @Property(
    type = PropertyType.SLIDER,
    name = "Nuker Range",
    description = "dont set this too high and ur chilling",
    category = "Mining",
    subcategory = "Nuker",
    min = 1,
    max = 4
  )
  public static int nukerRange = 3;

  public Config() {
    super(new File("./MiningInTwoConf.toml"));
    initialize();
  }
}
