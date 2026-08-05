package me.lukiiy.slumo;

import me.lukiiy.flow.FDefaults;
import me.lukiiy.flow.GameEntry;
import me.lukiiy.flow.setting.CycleSetting;
import me.lukiiy.flow.setting.Option;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Entry extends GameEntry {
    public final CycleSetting<Integer> tickRate = setting(new CycleSetting<>("tickrate", "Tickrate", "TPS during the match.", () -> List.of(new Option<>(5, "5 TPS"), new Option<>(8, "8 TPS"), new Option<>(10, "10 TPS"), new Option<>(15, "15 TPS"), new Option<>(20, "20 TPS"))));
    public final CycleSetting<Kit> kit = setting(new CycleSetting<>("kit", "Kit", "", () -> List.of(
            new Option<>(Kits.TROLL, Kits.TROLL.getName()),
            new Option<>(Kits.PVP, Kits.PVP.getName()),
            new Option<>(Kits.SUMO, Kits.SUMO.getName())
    )));


    public Entry() {
        super("tasffa", "Slumo", Game::new, Component.text("Parkour Tag").color(FDefaults.LIME));
    }
}
