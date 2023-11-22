package com.mit.features.mining.hollows;

import com.google.common.util.concurrent.AtomicDouble;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderOneBlockMod;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PathScanner {

  int maxX, maxY, maxZ;

  private HashSet<BlockPos> alrSeen = new HashSet<>();
  public List<BlockPos> foundPos = new ArrayList<>();
  public boolean isRunning = false;
  public boolean masterS = false;

  public PathScanner(int maxX, int maxY, int maxZ) {
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;

    MinecraftForge.EVENT_BUS.register(this);
  }

  public void scan() {
    new Thread(() -> {
      isRunning = true;
      int count = 0;
      BlockPos player = Dependencies.mc.thePlayer.getPosition();
      for (int i = -maxX; i <= maxX; i++) {
        for (int j = -maxY; j <= maxY; j++) {
          for (int k = -maxZ; k <= maxZ; k++) {
            BlockPos BP = new BlockPos(player.getX() + i, player.getY() + j, player.getZ() + k);

            if (alrSeen.contains(BP))
              continue;

            if (BlockUtils.getBlockType(BP) != Blocks.stained_glass &&
                BlockUtils.getBlockType(BP) != Blocks.stained_glass_pane) {
              continue;
            }

            Tuple<BlockPos, HashSet<BlockPos>> ret = getParentGems(BP);
            if (ret.getFirst() == null)
              continue;
            this.alrSeen.addAll(ret.getSecond());
            this.foundPos.add(ret.getFirst());
          }
        }
      }

      ChatUtils.chat(String.valueOf(count));

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      isRunning = false;
    })
        .start();
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!masterS)
      return;
    if (!isRunning) {
      RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
      foundPos.forEach(a -> {
        RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(a), true);
      });
      alrSeen.clear();

      // ChatUtils.chat(String.valueOf(foundPos.size()));
      foundPos.clear();
      RenderOneBlockMod.renderOneBlock(null, false);

      scan();
    }
  }

  private boolean isGlass(Block b) {
    return b == Blocks.stained_glass || b == Blocks.stained_glass_pane;
  }

  public void printGemAboveP() {
    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    BlockPos block = null;

    for (int i = 0; i <= 3; i++) {
      BlockPos BP = Dependencies.mc.thePlayer.getPosition().add(0, i, 0);
      if (BlockUtils.getBlockType(BP) == Blocks.stained_glass) {
        block = BP;
        break;
      }
    }

    if (block == null)
      return;

    List<int[]> blocks = new ArrayList<>();
    for (BlockPos b : BlockUtils
        .getBlocksInRadius(7, 7, 7, BlockUtils.fromBPToVec(block))
        .stream()
        .filter(a -> {
          return isGlass(BlockUtils.getBlockType(a));
        })
        .collect(Collectors.toList())) {
      RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(b), true);
      blocks.add(new int[] { b.getX() - block.getX(), b.getY() - block.getY(), b.getZ() - block.getZ() });
    }

    StringBuilder str = new StringBuilder("{");
    ChatUtils.chat(String.valueOf(blocks.size()));
    for (int[] ints : blocks) {
      int x = ints[0];
      int y = ints[1];
      int z = ints[2];
      str.append("{").append(x).append(",").append(y).append(",").append(z).append("}").append(",");
    }
    str.append("}");
    System.out.println(str);
  }

  private Tuple<BlockPos, HashSet<BlockPos>> getParentGems(BlockPos init) {
    List<BlockPos> open = new ArrayList<>();
    HashSet<BlockPos> all = new HashSet<>();

    open.add(init);
    all.add(init);

    while (!open.isEmpty()) {
      BlockPos cur = open.remove(0);
      for (BlockPos i : BlockPos.getAllInBox(cur.add(-1, -1, -1), cur.add(1, 1, 1))) {
        if (!isGlass(BlockUtils.getBlockType(i)))
          continue;
        if (!all.contains(i)) {
          all.add(i);
          open.add(i);
        }
      }
    }

    return new Tuple<>(calcForMostAround(all), all);
  }

  private BlockPos calcForMostAround(HashSet<BlockPos> all) {
    AtomicDouble curMax = new AtomicDouble(1000);
    AtomicReference<BlockPos> block = new AtomicReference<>();
    for (BlockPos a : all) {
      for (BlockPos vloc : getStandOn(a)) {
        AtomicDouble cur = new AtomicDouble();
        for (BlockPos b : all) {
          if (Math.abs(b.getY() - vloc.getY()) > 1) {
            cur.addAndGet(10);
          }

          double dst = MathUtils.distanceFromTo(vloc, b);

          cur.addAndGet(MathUtils.distanceFromTo(vloc, b));

          if (BlockUtils.getBlockType(b) == Blocks.stained_glass_pane) {
            cur.addAndGet(2);
          }

          if (dst > Dependencies.mc.playerController.getBlockReachDistance()) {
            cur.addAndGet(3);
          }
        }

        cur.set(cur.get() / all.size());
        if (cur.get() < curMax.get()) {
          block.set(vloc);
          curMax.set(cur.get());
        }
      }
    }
    return block.get();
  }

  private List<BlockPos> getStandOn(BlockPos init) {
    List<BlockPos> standOn = new ArrayList<>();

    BlockPos
        .getAllInBox(init.add(-1, -1, -1), init.add(1, 1, 1))
        .forEach(a -> {
          if (!isGlass(BlockUtils.getBlockType(a)) &&
              !isGlass(BlockUtils.getBlockType(a.add(0, 1, 0))) &&
              !isGlass(BlockUtils.getBlockType(a.add(0, 2, 0)))) {
            standOn.add(a);
          }
        });

    return standOn;
  }
}
