package com.mit.util;

import com.mit.global.Dependencies;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

  public static void chat(String msg) {
    Dependencies.MC.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(msg));
  }
}
