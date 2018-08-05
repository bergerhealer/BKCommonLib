package com.bergerkiller.bukkit.common;

import org.bukkit.Material;

import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;

/**
 * Material enum declarations that have the MC 1.13 material enum name changes in mind.
 */
public class MaterialEx {
    public static final Material RAIL = getFirst("RAIL", "RAILS");
    public static final Material OAK_WOODEN_PLANKS = getFirst("OAK_PLANKS", "WOOD");
    public static final Material OAK_LOG = getFirst("OAK_LOG", "LOG");
    public static final Material NETHER_PORTAL = getFirst("NETHER_PORTAL", "PORTAL");
    public static final Material END_PORTAL = getFirst("END_PORTAL", "ENDER_PORTAL");
}
