package com.mit.features.pathfind.main;

import com.mit.features.pathfind.utils.BlockNodeClass;
import com.mit.util.Render;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnPathRenderer {

  public static List<BlockNodeClass> blocks = new ArrayList<>();
  public static boolean startRender = false;

  public static void renderList(List<BlockNodeClass> blocksOnRoute, boolean state) {
    if (state) {
      blocks = blocksOnRoute;
      startRender = true;
    } else {
      startRender = false;
      blocks.clear();
    }
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    if (!startRender) return;

    for (BlockNodeClass block : blocks) {
      BlockPos pos = block.blockPos;
      if (block.actionType != null) {
        switch (block.actionType) {
          case WALK:
            Render.drawBox(pos.getX(), pos.getY(), pos.getZ(), Color.BLUE, 0.4F, event.partialTicks, false);
            break;
          case JUMP:
            Render.drawBox(pos.getX(), pos.getY(), pos.getZ(), Color.RED, 0.4F, event.partialTicks, false);
            break;
          case FALL:
            Render.drawBox(pos.getX(), pos.getY(), pos.getZ(), Color.GREEN, 0.4F, event.partialTicks, false);
            break;
          case BREAK:
            Render.drawBox(pos.getX(), pos.getY(), pos.getZ(), Color.MAGENTA, 0.4F, event.partialTicks, false);
            break;
        }
      }
    }
  }
}
