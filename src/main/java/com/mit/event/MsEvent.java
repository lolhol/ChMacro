package com.mit.event;

import java.time.LocalDateTime;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MsEvent extends Event {

  public LocalDateTime dateTime;
  public long timestamp;

  public MsEvent() {
    timestamp = System.currentTimeMillis();
    dateTime = LocalDateTime.now();
  }
}
