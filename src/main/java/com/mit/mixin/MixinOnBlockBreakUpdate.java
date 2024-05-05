package com.mit.mixin;

import com.mit.event.BlockBreakStateUpdateEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinOnBlockBreakUpdate {

  @Inject(method = "sendBlockBreakProgress", at = @At("HEAD"))
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
    MinecraftForge.EVENT_BUS.post(new BlockBreakStateUpdateEvent(progress, pos, breakerId));
  }
}
