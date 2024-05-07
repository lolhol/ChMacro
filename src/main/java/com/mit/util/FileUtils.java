package com.mit.util;

import com.mit.global.Dependencies;
import java.io.File;
import net.minecraft.client.Minecraft;

public class FileUtils {

  public static File getFolderPath() {
    return new File(Minecraft.getMinecraft().mcDataDir, "MiningInTwo");
  }
}
