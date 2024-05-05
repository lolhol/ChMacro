package com.mit;

import com.mit.AI.ArtificialInteli;
import com.mit.commands.AddBlocksToRoute;
import com.mit.commands.OpenGUI;
import com.mit.commands.StartMacroCommand;
import com.mit.commands.ai.AISwitchTrainingTesting;
import com.mit.commands.ai.AITestingSaveAsFile;
import com.mit.commands.test;
import com.mit.event.MsEvent;
import com.mit.event.SecondEvent;
import com.mit.features.mining.MiningFeatures;
import com.mit.features.mining.hollows.GetMetalDetectorChestLoc;
import com.mit.features.mining.hollows.MiningNuker;
import com.mit.features.render.*;
import com.mit.global.Dependencies;
import com.mit.util.PacketUtils;
import com.mit.util.RotationUtils;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

@Mod(modid = "autogg", name = "autogg", version = "1.0.0", clientSideOnly = true)
@SideOnly(Side.CLIENT)
public class MIT {

  public static File modFile = null;
  MiningFeatures miningFeatures = new MiningFeatures();
  RenderModules renderFeatures = new RenderModules();
  public static ArrayList<KeyBinding> keybinds = new ArrayList<>();

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    //System.out.println("!!!!!!MIT~");
    Display.setTitle("MiningInTwo");
    registerCommands(
      new OpenGUI(),
      new test(),
      new AddBlocksToRoute(),
      new StartMacroCommand(),
      new AISwitchTrainingTesting(),
      new AITestingSaveAsFile()
    );
    registerEvents(
      new RenderSingleLineTwoPoints(),
      new RenderPoints(),
      new MiningNuker(),
      new GetMetalDetectorChestLoc(),
      new RenderMultipleLines(),
      new RenderMultipleBlocksMod(),
      new RotationUtils(),
      new PacketUtils(),
      Dependencies.ai
    );
  }

  @Mod.EventHandler
  public void postFMLInitialization(FMLPostInitializationEvent event) {
    LocalDateTime now = LocalDateTime.now();
    Duration initialDelay = Duration.between(now, now);
    long initialDelaySeconds = initialDelay.getSeconds();

    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
    threadPool.scheduleAtFixedRate(
      () -> MinecraftForge.EVENT_BUS.post(new SecondEvent()),
      initialDelaySeconds,
      1,
      TimeUnit.SECONDS
    );
    threadPool.scheduleAtFixedRate(
      () -> MinecraftForge.EVENT_BUS.post(new MsEvent()),
      initialDelaySeconds,
      1,
      TimeUnit.MILLISECONDS
    );
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    modFile = event.getSourceFile();
  }

  private void registerKeybinds(ArrayList<KeyBinding> keybinds) {
    for (KeyBinding keybind : keybinds) {
      ClientRegistry.registerKeyBinding(keybind);
    }
  }

  private void registerEvents(Object... events) {
    for (Object event : events) {
      MinecraftForge.EVENT_BUS.register(event);
    }
  }

  private void registerCommands(Command... commands) {
    for (Command command : commands) {
      EssentialAPI.getCommandRegistry().registerCommand(command);
    }
  }
}
