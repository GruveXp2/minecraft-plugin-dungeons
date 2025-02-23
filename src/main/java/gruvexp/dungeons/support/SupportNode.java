package gruvexp.dungeons.support;

import gruvexp.dungeons.DungeonManager;
import gruvexp.dungeons.RoofSpace;
import gruvexp.dungeons.Utils;
import gruvexp.dungeons.dungeon.Dungeon;
import gruvexp.dungeons.location.Direction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TextDisplay;

public class SupportNode {
    private final Location location;
    private final Direction direction;
    private final SupportType supportType;
    private final TextDisplay dirMarker;
    private final TextDisplay typeMarker;

    public SupportNode(Dungeon dungeon, Location loc, Direction dir, SupportType roomType) {
        this.location = loc;
        this.direction = dir;
        this.supportType = roomType;
        String name = switch (dir) {
            case N -> ChatColor.BLUE + "North";
            case S -> ChatColor.RED + "South";
            case E -> ChatColor.GOLD + "East";
            case W -> ChatColor.AQUA + "West";
            case EW -> ChatColor.YELLOW + "East-West";
            case NS -> ChatColor.LIGHT_PURPLE + "North-South";
            case ANY -> ChatColor.WHITE + "Any";
        };
        this.dirMarker = DungeonManager.spawnTextMarker(loc.clone().add(0, 0.5, 0), name, "dungeon_support_node");
        this.typeMarker = DungeonManager.spawnTextMarker(loc.clone().add(0, 0.25, 0), ChatColor.GRAY + roomType.name(), "dungeon_support_node");
    }

    public void spawn(Dungeon dungeon) {
        dirMarker.remove();
        typeMarker.remove();
        Location locSpace = location.clone();
        locSpace.subtract(0, 4, 0);
        Support support;
        Direction roofDirection = Direction.ANY;
        if (dungeon.usedSpaces.contains(locSpace)) {
            if (dungeon.roofSpaces.containsKey(locSpace)) {
                RoofSpace roofSpace = dungeon.roofSpaces.get(locSpace);
                RoofType roofType = roofSpace.roofType();
                roofDirection = roofSpace.direction();
                support = switch (supportType) {
                    case BRIDGE_PILLAR -> Support.PLACEHOLDER;
                    case BRIDGE_TOWER -> switch (roofType) {
                        case FORTRESS_BRIDGE_TOWER_STAIRS -> Support.BRIDGE_TOWER_STAIRS;
                        case FORTRESS_BRIDGE_TOWER_F -> Support.BRIDGE_TOWER_FILLED;
                        case FORTRESS_BRIDGE_BOW_F -> Support.BRIDGE_TOWER_BOW_F;
                        case FORTRESS_BRIDGE_BOW_X -> Support.BRIDGE_TOWER_BOW_X;
                    };
                    case CORRIDOR -> Support.PLACEHOLDER;
                };
            } else return;
        } else { // fortsetter nedover
            if (location.getBlock().getType() != Material.AIR && location.getBlock().getType() != Material.LAVA && location.getBlock().getType() != Material.WATER) return;
            support = switch (supportType) {
                case BRIDGE_PILLAR -> Support.PLACEHOLDER;
                case BRIDGE_TOWER -> Support.BRIDGE_TOWER_FILLED;
                case CORRIDOR -> Support.PLACEHOLDER;
            };
        }
        SupportStructure structure = support.structure();
        structure.place(dungeon, location, roofDirection);
    }
}
