package gruvexp.dungeons;

import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.support.RoofType;

public record RoofSpace(RoofType roofType, Direction direction) {}
