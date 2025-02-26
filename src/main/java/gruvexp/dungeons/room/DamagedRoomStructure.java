package gruvexp.dungeons.room;

import gruvexp.dungeons.DungeonManager;
import gruvexp.dungeons.dungeon.Dungeon;
import gruvexp.dungeons.location.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;

import java.util.Set;

public class DamagedRoomStructure extends RoomStructure{
    public DamagedRoomStructure(String structureGroup, String structureName, RoomType roomType) {
        super(structureGroup, structureName, roomType);
    }

    public static final Material[] colors = new Material[] {Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.GREEN_WOOL, Material.CYAN_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL, Material.PURPLE_WOOL, Material.MAGENTA_WOOL};
    private static final Set<Material> FORTRESS_MATERIALS = Set.of(Material.NETHER_BRICKS, Material.NETHER_BRICK_SLAB, Material.NETHER_BRICK_STAIRS, Material.CHISELED_NETHER_BRICKS);

    public static Material get(int i) {
        while (i >= colors.length) {
            i -= colors.length;
        }
        return colors[i];
    }

    @Override
    public void place(Dungeon dungeon, Location loc, Direction dir) {
        super.place(dungeon, loc, dir);
        BlockVector size = structure.getSize();
        int height = size.getBlockY();
        int width = size.getBlockX();
        int z = size.getBlockZ();
        for (int x = 0; x < width; x++) {
            for (int y = 1; y < height; y++) {
                Location locRay = loc.clone();
                locRay.getBlock().setType(Material.GOLD_BLOCK);
                switch (dir) { // går til enden
                    case S -> {
                        locRay.add(x, y,  z);
                        //locRay.getBlock().setType(Material.RED_WOOL);
                    }
                    case E -> {
                        locRay.add(z, y,  -x);
                        //locRay.getBlock().setType(Material.ORANGE_WOOL);
                    }
                    case N -> {
                        locRay.add(-x, y, -z);
                        //locRay.getBlock().setType(Material.BLUE_WOOL);
                    }
                    case W -> {
                        locRay.add(-z, y, x);
                        //locRay.getBlock().setType(Material.CYAN_WOOL);
                    }
                }
                int damage = DungeonManager.RANDOM.nextInt(z); // hvor langt inn det blir damage, fra 0 damage til heilt inn
                if (x == 0 && y == 1) {
                    while (damage > 0) { // flytter inn til man kommer fram til en blokk
                        locRay.getBlock().setType(get(damage));
                        DungeonManager.moveForward(locRay, dir, -1);
                        damage--;
                    }
                    continue;
                }
                while (damage > 0 && (locRay.getBlock().getType() == Material.AIR || !FORTRESS_MATERIALS.contains(locRay.getBlock().getType()))) { // flytter inn til man kommer fram til en blokk
                    DungeonManager.moveForward(locRay, dir, -1);
                    damage--;
                }
                if (damage == 0) continue; // hvis det ikke kom langt nok inn til å gi damage
                int air = DungeonManager.RANDOM.nextInt(damage); // hvor mange blokker som blir totalt fjerna vs blir til slabs
                for (int dist = 0; dist < damage; dist++) {
                    if (dist <= air) {
                        locRay.getBlock().setType(Material.AIR);
                    } else {
                        locRay.getBlock().setType(Material.NETHER_BRICK_SLAB);
                    }
                    DungeonManager.moveForward(locRay, dir, -1);
                }
            }
        }
    }
}
