package com.mit.util.renderModules;

import com.mit.util.Render;
import java.awt.*;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderSingleLineTwoPoints {

  private static Vec3 block1 = null;
  private static Vec3 block2 = null;
  private static boolean start = false;

  public static void RenderSingleLineTwoPoints(Vec3 startBlock, Vec3 stopBlock, boolean state) {
    block1 = startBlock;
    block2 = stopBlock;
    start = state;
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    if (start && block1 != null && block2 != null) {
      try {
        Render.drawLine(block1, block2, 1F, Color.CYAN, event.partialTicks);
      } catch (Exception e) {
        System.out.println("!");
      }
    }
  }
}
