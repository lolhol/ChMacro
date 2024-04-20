package com.mit.util;

import com.mit.global.Dependencies;
import net.minecraft.util.ChatComponentText;

public class ChatUtils {

  public static void chat(String msg) {
    Dependencies.mc.thePlayer.addChatMessage(new ChatComponentText(msg));
  }
}
