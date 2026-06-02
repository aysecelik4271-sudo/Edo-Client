package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class ExampleMod implements ClientModInitializer {
    public static final String MOD_ID = "modid";
    private static final MinecraftClient client = MinecraftClient.getInstance();

    // Hile Modülleri Durumları
    public static boolean flyEnabled = false;
    public static boolean elytraFlyEnabled = false;
    public static boolean storageEspEnabled = false;
    public static boolean primeChunkFinder = false;
    public static boolean spawnerProtect = false;
    public static boolean fakeStats = false;
    public static boolean tunnelBaseFinder = false;
    public static boolean rtpBaseFinder = false;
    public static boolean netheriteFinder = false;
    public static boolean seedChunkFinder = false;
    public static boolean holeEsp = false;
    public static boolean lightFinder = false;
    public static boolean susChunkFinder = false;
    public static boolean fullbright = false;
    public static boolean nameTags = false;
    public static boolean freelook = false;
    public static boolean spamEnabled = false;

    private static final Set<BlockPos> loggedSpawners = new HashSet<>();
    private static final Set<BlockPos> loggedTunnels = new HashSet<>();
    private static final Set<ChunkPos> loggedSusChunks = new HashSet<>();
    private long lastSpamTime = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (client.player == null || client.world == null) return;

            long currentTime = System.currentTimeMillis();

            if (spamEnabled && (currentTime - lastSpamTime >= 3000)) {
                client.player.networkHandler.sendChatMessage("Edo Client VIP on top! Dominated by Edo.");
                lastSpamTime = currentTime;
            }

            if (fullbright) {
                client.options.getGamma().setValue(16.0);
            }

            runBaseHuntingAlgorithms();
        });
    }

    private void runBaseHuntingAlgorithms() {
        BlockPos playerPos = client.player.getBlockPos();
        int radius = 40;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = playerPos.add(x, y, z);
                    BlockState state = client.world.getBlockState(targetPos);

                    if (netheriteFinder && state.isOf(Blocks.ANCIENT_DEBRIS)) { }

                    if (tunnelBaseFinder && !loggedTunnels.contains(targetPos)) {
                        if (state.isAir() && client.world.getBlockState(targetPos.up()).isAir()) {
                            if (isYapayTunnel(targetPos)) {
                                loggedTunnels.add(targetPos);
                                client.player.sendMessage(Text.of("§6[Edo TunnelFinder] §eYapay Tünel: " + targetPos.toShortString()), false);
                            }
                        }
                    }

                    if (lightFinder && state.getLuminance() > 0 && targetPos.getY() < 40) {
                        if (state.isOf(Blocks.TORCH) || state.isOf(Blocks.LANTERN)) { }
                    }
                }
            }
        }

        for (BlockEntity entity : client.world.blockEntities) {
            BlockPos pos = entity.getPos();
            if (entity instanceof MobSpawnerBlockEntity && !loggedSpawners.contains(pos)) {
                loggedSpawners.add(pos);
                client.player.sendMessage(Text.of("§6[Edo Spawner] §dSpawner Bulundu! X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ()), false);
            }
        }

        ChunkPos currentChunk = client.player.getChunkPos();
        if (susChunkFinder && !loggedSusChunks.contains(currentChunk)) {
            long inhabitedTime = client.world.getChunk(currentChunk.x, currentChunk.z).getInhabitedTime();
            if (inhabitedTime > 5000 && client.player.distanceToHorizontal(currentChunk.getCenterX(), currentChunk.getCenterZ()) > 500) {
                loggedSusChunks.add(currentChunk);
                client.player.sendMessage(Text.of("§6[Edo SusChunk] §cŞüpheli Aktif Chunk: X: " + currentChunk.getCenterX() + " Z: " + currentChunk.getCenterZ()), false);
            }
        }
    }

    private boolean isYapayTunnel(BlockPos pos) {
        int airCount = 0;
        for (int i = 1; i <= 5; i++) {
            if (client.world.getBlockState(pos.north(i)).isAir()) airCount++;
        }
        return airCount >= 4;
    }
}
