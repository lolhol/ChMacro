package com.mit.event;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketReceiveEvent extends Event {

  public Packet<?> packet;

  public PacketReceiveEvent(Packet<?> packet) {
    this.packet = packet;
  }
}
