package me.lukiiy.slumo;

import me.lukiiy.flow.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Game extends Minigame {
    private World world;
    public AtomicBoolean active = new AtomicBoolean(false);

    @Override
    protected void prepare() {
        world = Slumo.getInstance().getMapMaker().create();

        if (world == null) throw new MinigameException("An error occurred when creating the world");
    }

    @Override
    protected void onStart() {
        Location spawn = world.getSpawnLocation();

        forEachPlayer(fp -> {
            Player p = fp.getPlayer();

            FUtils.softReset(p, GameMode.SURVIVAL);
            p.teleport(spawn);
            p.setRespawnLocation(spawn, true);
            entry().kit.getValue().apply(p);
        });

        new Countdown(Slumo.getInstance(), Duration.ofSeconds(5), (c) -> {
            TextColor color = FDefaults.GREEN;

            if (c < 4) color = FDefaults.YELLOW;
            if (c < 2) color = FDefaults.RED;

            TextColor finalColor = color;

            forEachPlayer(it -> it.getPlayer().showTitle(Title.title(Component.text("Starting in").color(FDefaults.GRAY), Component.text((c + 1) + " seconds!").color(finalColor), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofSeconds(1)))));
        }, () -> {
            Bukkit.getServer().getServerTickManager().setTickRate(entry().tickRate.getValue());
            active.set(true);
        }).start();
    }

    @Override
    protected List<Listener> listeners() {
        return List.of(new Listen(this));
    }

    public List<FlowPlayer> getAlive() {
        return getPlayers().stream().map(FlowPlayer.class::cast).filter(fp -> fp.getState() == FlowPlayer.State.PLAYING).toList();
    }

    public void kill(Player player) {
        getFlowPlayer(player).ifPresent(fp -> fp.setState(FlowPlayer.State.SPECTATING));
        player.setGameMode(GameMode.SPECTATOR);
        broadcast(Component.empty().append(Component.text("  » ").color(FDefaults.DARK_GRAY)).append(player.displayName().color(FDefaults.RED)).append(Component.text(" was eliminated!").color(FDefaults.GRAY)));

        if (getAlive().size() <= 1) end();
    }

    private void end() {
        Bukkit.getServer().getServerTickManager().setTickRate(20);
        active.set(false);

        FlowPlayer winner = getAlive().stream().findFirst().orElse(null);

        if (winner != null) {
            broadcast(Component.empty().append(Component.text("  » ").color(FDefaults.DARK_GRAY)).append(winner.getPlayer().displayName().color(FDefaults.GREEN)).append(Component.text(" has won!").color(FDefaults.GRAY)));
        } else {
            broadcast(Component.empty().append(Component.text("  » ").color(FDefaults.DARK_GRAY)).append(Component.text("Nobody won.")));
        }

        Bukkit.getGlobalRegionScheduler().runDelayed(Slumo.getInstance(), _ -> stop(), 80L);
    }

    @Override
    protected void onStop() {
        Bukkit.getServer().getServerTickManager().setTickRate(20);

        BaseLobby lobby = Flow.getInstance().getManager().getLobby();
        if (lobby != null) forEachPlayer(lobby::sendToLobby);

        if (world != null) {
            File wFolder = world.getWorldFolder();

            Bukkit.getServer().unloadWorld(world, false);

            if (wFolder.exists()) {
                try (Stream<Path> stream = Files.walk(wFolder.toPath())) {
                    stream.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
                } catch (IOException e) {
                    Slumo.getInstance().getLogger().warning("Could not delete instanced world " + wFolder.getName() + "! " + e.getMessage());
                }
            }
        }
    }

    public Entry entry() {
        return (Entry) entry;
    }
}
