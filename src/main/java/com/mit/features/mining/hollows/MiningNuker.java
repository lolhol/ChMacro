package com.mit.features.mining.hollows;

import com.mit.event.MsEvent;
import com.mit.global.Dependencies;
import com.mit.gui.config.Config;
import com.mit.util.BlockUtils;
import com.mit.util.PacketUtils;
import com.mit.util.Render;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MiningNuker {

  int nukerTickCount = 0;
  int blocksToBreakUpdate = 500;
  HashSet<String> blocksToBreak = new HashSet<>();
  HashSet<BlockPos> alrBroken = new HashSet<>();
  BlockPos curBlock = null;
  int timeBeforeReNuke = 0;

  @SubscribeEvent
  public void onMs(MsEvent event) {
    if (!Config.nuker) {
      nukerTickCount = 0;
      blocksToBreakUpdate = 500;
      return;
    }

    if (blocksToBreakUpdate >= 500) {
      parseString(Config.nukerBlocks);
      blocksToBreakUpdate = 0;
    } else {
      blocksToBreakUpdate++;
    }

    if (timeBeforeReNuke >= 2000) {
      this.alrBroken.clear();
    }

    if (nukerTickCount >= 1000 / Config.nukerBPS) {
      List<BlockPos> blocksToBreak = new ArrayList<>();

      if (!Config.nukerMode) {
        nukerTickCount = 0;
        blocksToBreak =
          BlockUtils.getBlocksInRadius(
            Config.nukerRange,
            Config.nukerRange,
            Config.nukerRange,
            Dependencies.mc.thePlayer.getPosition(),
            this.blocksToBreak,
            this.alrBroken,
            Config.nukerRange
          );
      } else {
        BlockPos block = BlockUtils.getBlocksInFront(Config.nukerRange);
        if (!this.alrBroken.contains(block)) {
          blocksToBreak.add(BlockUtils.getBlocksInFront(Config.nukerRange));
        }
      }

      if (blocksToBreak.isEmpty()) {
        curBlock = null;
        return;
      }

      curBlock = BlockUtils.getClosest(blocksToBreak, Dependencies.mc.thePlayer.getPosition());
      PacketUtils.sendStartPacket(curBlock, PacketUtils.getEnum(curBlock));
      this.alrBroken.add(curBlock);
    } else {
      nukerTickCount++;
    }
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!Config.nuker) {
      return;
    }

    if (curBlock != null) {
      Render.drawFilledInBlock(curBlock, Color.WHITE, 0.2F, event.partialTicks);
    }
  }

  void parseString(String str) {
    blocksToBreak.clear();
    str = str.toLowerCase();
    StringBuilder curWord = new StringBuilder();

    for (int i = 0; i < str.length(); i++) {
      String cur = str.charAt(i) + "";
      if (!cur.equals(",")) {
        curWord.append(cur);
      } else {
        blocksToBreak.add("tile." + curWord);
        curWord = new StringBuilder();
      }
    }

    blocksToBreak.add("tile." + curWord);
  }
}
