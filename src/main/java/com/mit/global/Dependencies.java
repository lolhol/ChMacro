package com.mit.global;

import com.mit.AI.ArtificialInteli;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.FileUtils;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class Dependencies {

  public static final Minecraft mc = Minecraft.getMinecraft();

  public static final class AI {

    public static final ArtificialInteli ai = new ArtificialInteli();
  }

  public static final class MOD {

    public static final String ModPrefix = "[ MIT ] ";
    public static final double ModVersion = 0.1;
  }

  public static final class MINING {

    public static boolean isNull = true;
    public static String routeName;
    public static List<BlockPos> currentRoute;

    // cosmetic
    public static Color renderColor = Color.BLUE;

    public static void setRoute(String name, List<BlockPos> route) {
      routeName = name;
      currentRoute = route;
      isNull = false;
    }

    /**
     * @apiNote this might have to be re-located to a different place. not sure if it fits here.
     */
    public static boolean loadRoute(String name) throws IOException {
      File file = new File(FileUtils.getFolderPath() + name + ".mitroute");
      if (!file.exists()) {
        return false;
      }

      DataInputStream r = new DataInputStream(new FileInputStream(file));
      String routeName = r.readUTF();
      List<BlockPos> route = new ArrayList<>();
      while (r.available() > 0) {
        int x = r.readInt();
        int y = r.readInt();
        int z = r.readInt();
        route.add(new BlockPos(x, y, z));
      }

      setRoute(routeName, route);

      return true;
    }

    /**
     * @apiNote this might have to be re-located to a different place. not sure if it fits here.
     */
    public static boolean saveRoute(String name) throws IOException {
      List<BlockPos> route = Dependencies.MINING.currentRoute;

      // TODO: logic will have to be re-done
      File file = new File(FileUtils.getFolderPath(), name + ".mitroute");

      // IK IK it does delete route first but whatever right? :
      if (file.exists()) {
        file.delete();
      }

      file.createNewFile();

      DataOutputStream w = new DataOutputStream(new FileOutputStream(file));
      w.writeUTF(name);
      w.writeInt(route.size());
      for (BlockPos pos : route) {
        w.writeInt(pos.getX());
        w.writeInt(pos.getY());
        w.writeInt(pos.getZ());
      }

      return true;
    }

    public static void reRenderRoute() {}
  }
}
