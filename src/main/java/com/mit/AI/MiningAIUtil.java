package com.mit.AI;

import java.util.List;
import net.minecraft.util.BlockPos;

// TODO: implement this??
public class MiningAIUtil {

  private List<BlockPos> routeData;

  public MiningAIUtil(List<BlockPos> routeData) {
    this.routeData = routeData;
  }

  public MiningAIUtil() {
    this(null);
  }

  public void updateMiningRouteData(List<BlockPos> newRouteData) {
    routeData = newRouteData;
  }
}
