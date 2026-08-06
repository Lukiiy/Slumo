package me.lukiiy.slumo;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;

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
                if (b.getBlockData().getMaterial().isAir()) continue;

                Location loc = b.getLocation();

                int x = loc.getBlockX();
                int y = loc.getBlockY();
                int z = loc.getBlockZ();

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            }

            if (maxX != -1) platforms.add(new Platform(structure, -minX, -minY, -minZ, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1));
        }
    }

    public World create() {
        WorldCreator creator = new WorldCreator("tasffa_" + UUID.randomUUID()).generator(new VoidGen()).generateStructures(false).environment(World.Environment.NORMAL).type(WorldType.FLAT);

        World world = creator.createWorld();
        if (world == null) return null;

        world.setAutoSave(false);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);

        world.setViewDistance(4);
        world.setSimulationDistance(4);

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

    private static final class VoidGen extends ChunkGenerator {}
}
