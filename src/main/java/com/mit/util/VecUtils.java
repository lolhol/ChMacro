package com.mit.util;

import com.mit.global.Dependencies;
import java.util.HashMap;
import net.minecraft.client.settings.KeyBinding;

public class VecUtils {

  private static final HashMap<Integer, KeyBinding> keyBindMap = new HashMap<Integer, KeyBinding>() {
    {
      put(0, Dependencies.mc.gameSettings.keyBindForward);
      put(90, Dependencies.mc.gameSettings.keyBindLeft);
      put(180, Dependencies.mc.gameSettings.keyBindBack);
      put(-90, Dependencies.mc.gameSettings.keyBindRight);
    }
  };
  // Soon(TM)
  /*public static ArrayList<KeyBinding> getNeededKeyPresses(final Vec3 from, final Vec3 to) {
        final ArrayList<KeyBinding> e = new ArrayList<>();
        final RotationUtils.Rotation neededRot = RotationUtils.getNeededChange(RotationUtils.getRotation(from, to));
        final double neededYaw = neededRot.getYaw() * -1.0f;
        keyBindMap.forEach((k, v) -> {
            if (Math.abs(k - neededYaw) < 67.5 || Math.abs(k - (neededYaw + 360.0)) < 67.5) {
                e.add(v);
            }
            return;
        });
        return e;
    }*/
}
