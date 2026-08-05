package me.lukiiy.slumo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapMaker {
    private final Random random = ThreadLocalRandom.current();
    private final List<Platform> platforms = new ArrayList<>();

    public record Platform(Structure structure, int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ) {}

    public MapMaker() throws IOException {
        File dir = new File(Bukkit.getServer().getWorldContainer(),"tasffa");

        dir.mkdirs();

        for (File file : Objects.requireNonNull(dir.listFiles(it -> it.getName().endsWith(".nbt")))) {
            Structure structure = Bukkit.getStructureManager().loadStructure(file);
            BlockVector size = structure.getSize();

            int minX = size.getBlockX();
            int minY = size.getBlockY();
            int minZ = size.getBlockZ();
            int maxX = -1;
            int maxY = -1;
            int maxZ = -1;

            for (BlockState b : structure.getPalettes().getFirst().getBlocks()) {
                if (b.getBlock().isEmpty()) continue;

                Location loc = b.getBlock().getLocation();

                minX = Math.min(minX, loc.blockX());
                minY = Math.min(minY, loc.blockY());
                minZ = Math.min(minZ, loc.blockZ());
                maxX = Math.max(maxX, loc.blockX());
                maxY = Math.max(maxY, loc.blockY());
                maxZ = Math.max(maxZ, loc.blockZ());
            }

            if (maxX != -1) platforms.add(new Platform(structure, -minX, -minY, -minZ, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1));
        }
    }

    public World create() {
        String name = "tasffa_" + UUID.randomUUID();

        World world = new WorldCreator("tasffa_" + UUID.randomUUID()).generator(new VoidGen()).createWorld();
        if (world == null) return null;

        Platform platform = platforms.get(random.nextInt(platforms.size()));

        int x = platform.originX();
        int y = platform.originY() + 64;
        int z = platform.originZ();

        platform.structure.place(new Location(world, x, y, z), true, StructureRotation.NONE, Mirror.NONE, 0, 1, random);

        int centerX = x + platform.sizeX() / 2;
        int centerZ = z + platform.sizeZ() / 2;
        int heightY = world.getHighestBlockYAt(centerX, centerZ) + 1;

        world.setSpawnLocation(new Location(world, centerX, heightY, centerZ).toCenterLocation());

        return world;
    }

    private static final class VoidGen extends ChunkGenerator {
        @Override
        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {}

        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }

        @Override
        public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {}
    }

}
