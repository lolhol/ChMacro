package com.mit.features.mining.hollows.macro;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;

@Getter
@AllArgsConstructor
public class GemMacroConf {

  public final List<BlockPos> path;
  public final boolean isWalking;
  public final boolean isServerLook;
  public final boolean isTickGliding;
  public final long rotationTime;
}
