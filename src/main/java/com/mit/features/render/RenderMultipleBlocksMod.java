package com.mit.features.render;

import com.mit.util.Render;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderMultipleBlocksMod {

  private static List<Vec3> blocks1 = new ArrayList<>();
  private static HashSet<Vec3> blocksRendered = new HashSet<>();
  private static boolean startRender = false;

  public static void renderMultipleBlocks(Vec3 block, boolean renderState) {
    startRender = renderState;
    if (startRender) {
      blocks1.add(block);
      blocksRendered.add(block);
    } else {
      startRender = false;
      blocks1 = new ArrayList<>();
      blocksRendered = new HashSet<>();
    }
  }

  public static boolean isRendered(Vec3 block) {
    if (!startRender) return false;

    return blocksRendered.contains(block);
  }

  public static void stopRenderBlock(Vec3 block) {
    if (blocks1 != null) {
      blocks1.remove(block);
    }
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    if (startRender && blocks1 != null) {
      for (int i = 0; i < blocks1.size(); i++) {
        try {
          Render.drawBox(
            blocks1.get(i).xCoord,
            blocks1.get(i).yCoord,
            blocks1.get(i).zCoord,
            Color.GREEN,
            0.5F,
            event.partialTicks,
            false
          );
        } catch (Exception e) {}
      }
    }
  }
}
