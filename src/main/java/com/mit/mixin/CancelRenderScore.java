package com.mit.mixin;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class CancelRenderScore {

  @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
  private void cancelScoreboard(ScoreObjective s, ScaledResolution sr, CallbackInfo ci) {
    ci.cancel();
  }
}
