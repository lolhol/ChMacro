package com.mit.mixin;

import com.mit.event.PacketReceiveEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

  @Inject(
    method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V",
    at = @At("HEAD")
  )
  private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
    if (packet.getClass().getSimpleName().startsWith("S")) MinecraftForge.EVENT_BUS.post(
      new PacketReceiveEvent(packet)
    );
  }
}
