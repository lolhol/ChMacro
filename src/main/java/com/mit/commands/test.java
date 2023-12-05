package com.mit.commands;

import com.mit.features.foraging.ForgaingMacroMain;
import com.mit.features.mining.hollows.macro.GemMacro;
import com.mit.features.mining.hollows.macro.GemMacroConf;
import com.mit.features.mining.hollows.macro.Util;
import com.mit.features.pathfind.main.AStarPathFinder;
import com.mit.features.pathfind.utils.PathFinderConfig;
import com.mit.features.pathfind.walker.WalkerMain;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderMultipleLines;
import com.mit.features.render.RenderPoints;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import com.mit.util.RayTracingUtils;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.common.MinecraftForge;

public class test extends Command {

  AStarPathFinder finder = new AStarPathFinder();
  WalkerMain walker = new WalkerMain();
  ForgaingMacroMain forg = new ForgaingMacroMain();
  boolean b = false;
  boolean st = false;

  GemMacro gem = new GemMacro(new GemMacroConf(null, false, false, true, 200));

  public test() {
    super("test");
  }

  @DefaultHandler
  public void handle() {
    gem.reset();
    gem.isMacroOn = b;
    gem.miningState = Util.MiningState.MINING;
    b = !b;
    ChatUtils.chat(String.valueOf(b));

    MinecraftForge.EVENT_BUS.register(gem);
  }
}
