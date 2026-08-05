package me.lukiiy.slumo;

import me.lukiiy.flow.Flow;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Slumo extends JavaPlugin {
    private MapMaker mapMaker;

    @Override
    public void onEnable() {
        try {
            mapMaker = new MapMaker();
        } catch (IOException e) {
            getLogger().severe("Couldn't setup the map maker! " + e.getMessage());
        }

        Flow.getInstance().getManager().register(new Entry());
    }

    public static Slumo getInstance() {
        return JavaPlugin.getPlugin(Slumo.class);
    }

    public MapMaker getMapMaker() {
        return mapMaker;
    }
}
