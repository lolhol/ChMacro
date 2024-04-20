package com.mit.features.mining.hollows;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mit.global.Dependencies;
import com.mit.gui.config.Config;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.StringUtils;
import gg.essential.api.utils.Multithreading;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*
      Not my code! Taken from GTC.
*/

public class GetMetalDetectorChestLoc {

  private static BlockPos anchor;
  private static final HashSet<BlockPos> relativeChestCoords = new HashSet<>();
  private static final HashSet<BlockPos> absoluteChestCoords = new HashSet<>();
  private static BlockPos ignoreBlockPos;
  private static final HashSet<BlockPos> predictedChestLocations = new HashSet<>();
  private static Vec3 lastPos;

  private static long lastScan = 0;
  private static boolean lobbyInitialized = false;

  public GetMetalDetectorChestLoc() {
    JsonParser jsonParser = new JsonParser();
    Object obj = jsonParser.parse(
      new BufferedReader(
        new InputStreamReader(
          Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("assets/metal_detector_coords.json"))
        )
      )
    );
    JsonArray jsonArray = (JsonArray) obj;
    for (JsonElement jsonElement : jsonArray) {
      relativeChestCoords.add(
        new BlockPos(
          jsonElement.getAsJsonArray().get(0).getAsInt(),
          jsonElement.getAsJsonArray().get(1).getAsInt(),
          jsonElement.getAsJsonArray().get(2).getAsInt()
        )
      );
    }
  }

  @SubscribeEvent
  public void onActionBar(ClientChatReceivedEvent event) {
    if (!Config.metalDetectorSolver || Dependencies.mc.theWorld == null || Dependencies.mc.thePlayer == null) return;
    if (event.type != 2) return;

    String text = StringUtils.removeFormatting(event.message.getUnformattedText());
    if (text.contains("TREASURE")) {
      if (!lobbyInitialized && System.currentTimeMillis() - lastScan > 3000) {
        lastScan = System.currentTimeMillis();
        Multithreading.runAsync(GetMetalDetectorChestLoc::scanChunks);

        if (anchor != null) {
          absoluteChestCoords.clear();
          for (BlockPos blockPos : relativeChestCoords) {
            BlockPos absolutePosition = new BlockPos(
              anchor.getX() - blockPos.getX(),
              anchor.getY() - blockPos.getY() + 1,
              anchor.getZ() - blockPos.getZ()
            );
            absoluteChestCoords.add(absolutePosition);
          }

          lobbyInitialized = true;
        } else {
          return;
        }
      }

      // MIGHT BREAK HERE!
      EntityPlayerSP player = Dependencies.mc.thePlayer;
      if (
        lastPos == null ||
        player.posX != lastPos.xCoord ||
        player.posY != lastPos.yCoord ||
        player.posZ != lastPos.zCoord
      ) {
        lastPos = player.getPositionVector();
        return;
      }

      double treasureDistance = Double.parseDouble(
        text.split("TREASURE: ")[1].split("m")[0].replaceAll("(?!\\.)\\D", "")
      );
      for (BlockPos blockPos : absoluteChestCoords) {
        double dist = Math.sqrt(
          Math.pow(player.posX - blockPos.getX(), 2) +
          Math.pow(player.posY - blockPos.getY(), 2) +
          Math.pow(player.posZ - blockPos.getZ(), 2)
        );

        if (Math.round(dist * 10D) / 10D == treasureDistance) {
          if (blockPos.add(0, -1, 0).equals(ignoreBlockPos)) {
            ignoreBlockPos = null;
            return;
          }

          if (!predictedChestLocations.contains(blockPos.add(0, -1, 0))) {
            Dependencies.mc.thePlayer.playSound("random.orb", 1, 0.5F);
          }

          predictedChestLocations.clear();
          predictedChestLocations.add(blockPos.add(0, -1, 0));
        }
      }
    }
  }

  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    anchor = null;
    ignoreBlockPos = null;
    lastScan = 0;
    lastPos = null;
    predictedChestLocations.clear();
    absoluteChestCoords.clear();
  }

  private static void scanChunks() {
    int playerX = (int) Dependencies.mc.thePlayer.posX;
    int playerY = (int) Dependencies.mc.thePlayer.posY;
    int playerZ = (int) Dependencies.mc.thePlayer.posZ;

    for (int x = playerX - 50; x < playerX + 50; x++) {
      for (int y = playerY + 35; y > playerY; y--) {
        for (int z = playerZ - 50; z < playerZ + 50; z++) {
          if (
            BlockUtils.getBlockType(new BlockPos(x, y, z)) == Blocks.quartz_stairs &&
            BlockUtils.getBlockType(new BlockPos(x, y + 13, z)) == Blocks.barrier
          ) {
            anchor = verifyAnchor(x, y + 13, z);
            return;
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onChat(ClientChatReceivedEvent event) {
    if (!Config.metalDetectorSolver || Dependencies.mc.theWorld == null || Dependencies.mc.thePlayer == null) return;
    if (event.type != 0) return;
    String text = StringUtils.removeFormatting(event.message.getUnformattedText());
    if (text.startsWith("You found") && text.endsWith("Metal Detector!")) {
      if (predictedChestLocations.iterator().hasNext()) {
        ignoreBlockPos = predictedChestLocations.iterator().next();
      }
      predictedChestLocations.clear();
    }
  }

  public BlockPos predictedBlock() {
    return predictedChestLocations.iterator().next();
  }

  private static BlockPos verifyAnchor(int posX, int posY, int posZ) {
    boolean loop = true;

    if (BlockUtils.getBlockType(new BlockPos(posX, posY, posZ)) != Blocks.barrier) {
      return new BlockPos(posX, posY, posZ);
    }
    while (loop) {
      loop = false;
      if (BlockUtils.getBlockType(new BlockPos(posX + 1, posY, posZ)) == Blocks.barrier) {
        posX++;
        loop = true;
      }
      if (BlockUtils.getBlockType(new BlockPos(posX, posY - 1, posZ)) == Blocks.barrier) {
        posY--;
        loop = true;
      }
      if (BlockUtils.getBlockType(new BlockPos(posX, posY, posZ + 1)) == Blocks.barrier) {
        posZ++;
        loop = true;
      }
    }

    ChatUtils.chat("Found anchor at " + new BlockPos(posX, posY, posZ));
    return new BlockPos(posX, posY, posZ);
  }
}
