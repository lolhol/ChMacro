package com.mit.features.render;

import com.mit.global.Dependencies;
import com.mit.util.Render;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RouteRenderer {

  private static boolean isRendering;

  public static void setRendering(boolean state) {
    isRendering = state;
  }

  public static void switchRendering() {
    isRendering = !isRendering;
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    if (!isRendering || Dependencies.MINING.currentRoute == null) return;

    for (BlockPos blockPos : Dependencies.MINING.currentRoute) {
      try {
        Render.drawBox(
          blockPos.getX(),
          blockPos.getY(),
          blockPos.getZ(),
          Dependencies.MINING.renderColor,
          0.5F,
          event.partialTicks,
          false
        );
      } catch (Exception e) {}
    }
  }
}
