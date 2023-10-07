package com.mit.features.render;

import com.mit.util.Render;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class RenderOneBlockMod {

  private static boolean startRender = false;
  private static Vec3 blockPos = null;

  public static void renderOneBlock(Vec3 block, boolean state) {
    startRender = state;
    blockPos = block;
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    if (startRender && blockPos != null) {
      try {
        Render.drawBox(blockPos.xCoord, blockPos.yCoord, blockPos.zCoord, Color.BLUE, 0.5F, event.partialTicks, false);
        //RenderLine.drawLine(playerVec, blockVec, 0.5F, Color.CYAN, event.partialTicks);
      } catch (Exception e) {}
    }
  }
}
