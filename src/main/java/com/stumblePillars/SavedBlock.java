package com.stumblePillars;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public record SavedBlock(Location location, BlockData blockData) {
}
