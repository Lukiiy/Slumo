package me.lukiiy.slumo;

import me.lukiiy.flow.FDefaults;
import me.lukiiy.flow.GameEntry;
import net.kyori.adventure.text.Component;

public class Entry extends GameEntry {
    public Entry() {
        super("tasffa", "Slumo", Game::new, Component.text("Parkour Tag").color(FDefaults.LIME));
    }
}
