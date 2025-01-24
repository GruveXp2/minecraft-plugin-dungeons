package gruvexp.dungeons;

import gruvexp.dungeons.commands.DungeonCommand;
import gruvexp.dungeons.commands.DungeonTabCompletor;
import gruvexp.dungeons.commands.TestCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;
    public static World WORLD;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Dungeonsplugin started");
        getCommand("test").setExecutor(new TestCommand());
        getCommand("dungeon").setExecutor(new DungeonCommand());
        getCommand("dungeon").setTabCompleter(new DungeonTabCompletor());
        plugin = this;
        WORLD = Bukkit.getWorld("Dungeon Test");
        DungeonManager.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }
}
