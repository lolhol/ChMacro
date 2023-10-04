package com.mit.features.mining;

import com.mit.event.MsEvent;
import com.mit.global.Dependencies;
import com.mit.gui.config.Config;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.Render;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiningNuker {

  int nukerTickCount = 0;
  int blocksToBreakUpdate = 500;
  HashSet<String> blocksToBreak = new HashSet<>();
  HashSet<BlockPos> alrBroken = new HashSet<>();
  BlockPos curBlock = null;

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

    if (nukerTickCount >= 1000 / Config.nukerBPS) {
      nukerTickCount = 0;

      List<BlockPos> blocksToBreak = BlockUtils.getBlocksInRadius(
        Config.nukerRange,
        Config.nukerRange,
        Config.nukerRange,
        Dependencies.MC.thePlayer.getPosition(),
        this.blocksToBreak,
        this.alrBroken
      );

      if (blocksToBreak.isEmpty()) {
        curBlock = null;
        return;
      }

      curBlock = blocksToBreak.get(0);
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
      Render.drawBox(curBlock.getX(), curBlock.getY(), curBlock.getZ(), Color.RED, 0.5F, event.partialTicks, false);
    }
  }

  private void parseString(String str) {
    blocksToBreak.clear();
    str = str.toLowerCase();
    StringBuilder curWord = new StringBuilder();

    for (int i = 0; i < str.length(); i++) {
      String cur = str.charAt(i) + "";
      if (!cur.equals(",")) {
        curWord.append(cur);
      } else {
        blocksToBreak.add("tile." + curWord.toString());
        curWord = new StringBuilder();
      }
    }

    blocksToBreak.add("tile." + curWord.toString());
  }
}
