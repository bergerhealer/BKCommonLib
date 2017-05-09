package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion2.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.mojang.authlib.GameProfile;
import org.bukkit.entity.HumanEntity;

public class NMSEntityHuman extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityHuman");
    
    public static final FieldAccessor<Object> inventory = T.nextField("public PlayerInventory inventory");
    public static final FieldAccessor<Object> enderChest = T.nextField("private InventoryEnderChest enderChest");
    
    static {
    	T.skipField("public Container defaultContainer");
    	T.skipField("public Container activeContainer");
    }
    
    public static final FieldAccessor<Object> foodData = T.nextField("protected FoodMetaData foodData");

    public static final FieldAccessor<Boolean> sleeping = T.nextField("public boolean sleeping");
    public static final FieldAccessor<IntVector3> bedPosition = T.nextField("public BlockPosition bedPosition").translate(DuplexConversion.blockPosition);
    public static final FieldAccessor<Integer> sleepTicks = T.nextField("public int sleepTicks");
    
    static {
    	T.skipFieldSignature("public float bI");
    	T.skipFieldSignature("public float bJ");
    }

    public static final TranslatorFieldAccessor<IntVector3> spawnCoord = T.nextFieldSignature("private BlockPosition e").translate(DuplexConversion.blockPosition);
    public static final FieldAccessor<Boolean> spawnForced = T.nextFieldSignature("private boolean f");

    static {
    	T.skipFieldSignature("private BlockPosition g");
    }

    public static final TranslatorFieldAccessor<PlayerAbilities> abilities = T.nextField("public PlayerAbilities abilities").translate(DuplexConversion.playerAbilities);
    public static final FieldAccessor<Integer> expLevel = T.nextField("public int expLevel");
    public static final FieldAccessor<Integer> expTotal = T.nextField("public int expTotal");
    public static final FieldAccessor<Float> exp = T.nextField("public float exp");
    
    static {
        T.skipFieldSignature("private int h");
        T.skipFieldSignature("protected float bO");
        T.skipFieldSignature("protected float bP");
        T.skipFieldSignature("private int bR");
    }
    
    public static final FieldAccessor<GameProfile> gameProfile = T.nextFieldSignature("private final com.mojang.authlib.GameProfile bS");

    public static final FieldAccessor<String> spawnWorld = T.nextField("public String spawnWorld");

    public static boolean canInstaBuild(HumanEntity human) {
        return abilities.get(Conversion.toEntityHandle.convert(human)).canInstantlyBuild();
    }
}
