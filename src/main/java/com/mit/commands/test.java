package com.mit.commands;

import com.mit.features.foraging.ForgaingMacroMain;
import com.mit.features.mining.hollows.macro.MiningMacro;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.util.PacketUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import net.minecraftforge.common.MinecraftForge;

public class test extends Command {

  AStarPathFinder finder = new AStarPathFinder();
  WalkerMain walker = new WalkerMain();
  ForgaingMacroMain forg = new ForgaingMacroMain();
  boolean b = false;
  boolean st = false;
  MiningMacro macro = null;

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle() {
    b = !b;
    if (b) {
      macro = new MiningMacro(true, 0, 0, PacketUtils.getPing(), 100);
      MinecraftForge.EVENT_BUS.register(macro);
    } else {
      MinecraftForge.EVENT_BUS.unregister(macro);
      macro = null;
    }
  }
}
