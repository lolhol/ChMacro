package com.mit.features.pathfind.utils.render;

import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderMultipleLines;
import com.mit.util.BlockUtils;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PathRender {

  public static void renderPath(List<Vec3> path) {
    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    RenderMultipleLines.renderMultipleLines(null, null, false);

    for (int i = 0; i < path.size(); ++i) {
      Vec3 b = path.get(i);
      RenderMultipleBlocksMod.renderMultipleBlocks(b, true);

      if (i + 1 < path.size()) {
        RenderMultipleLines.renderMultipleLines(
          BlockUtils.fromVecToBP(b).add(0, 0.5, 0),
          BlockUtils.fromVecToBP(path.get(i + 1)).add(0, 0.5, 0),
          true
        );
      }
    }
  }
}
