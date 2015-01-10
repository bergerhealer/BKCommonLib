package com.bergerkiller.bukkit.common.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R1.Packet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PacketTypeClasses.NMSPacket;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;

public class ProtocolTest {

    private final List<PojoClass> packetClasses = new ArrayList<PojoClass>();
    private final List<PojoClass> nmsPacketClasses = new ArrayList<PojoClass>();

    public ProtocolTest() {
        //Scan for API packets
        packetClasses.addAll(PojoClassFactory.enumerateClassesByExtendingType("com.bergerkiller.bukkit.common.protocol", NMSPacket.class, null));

        //Sctan for NMS packets
        nmsPacketClasses.addAll(PojoClassFactory.enumerateClassesByExtendingType("net.minecraft.server", Packet.class, new PojoClassFilter() {

            @Override
            public boolean include(PojoClass pojoClass) {
                return pojoClass.getName().contains("PacketPlay");
            }
        }));
    }

    @Test
    public void testPacketsLoad() {
        int loaded = 0;
        for (PojoClass pojoClass : packetClasses) {
            try {
                getClass().getClassLoader().loadClass(pojoClass.getClazz().getName());
                loaded += 1;
            } catch (Exception e) {
                Assert.assertTrue("Failed to load packet named " + pojoClass.getClazz().getSimpleName() + "!", false);
            }
        }

        int expected = packetClasses.size();
        Assert.assertEquals("Some packets failed to load, expected " + expected + " but loaded " + loaded, expected, loaded);
    }

    @Test
    public void testPacketTypes() {
        //Don't know if this test makes sense lol.
        try {
            getClass().getClassLoader().loadClass(PacketType.class.getName());
        } catch (Exception e) {
            Assert.assertTrue("Failed to init PacketType.java", false);
        }
    }

    @Test
    public void testMissingPackets() {
        Assert.assertFalse("No API packets were found!", packetClasses.isEmpty());
        int expected = nmsPacketClasses.size();
        int actual = packetClasses.size();
        Assert.assertEquals("Packets missing, expected " + expected + " but found " + actual, expected, actual);
    }

    @Ignore
    @Test
    public void testMissingMethods() {
        for (PojoClass nmsPacket : nmsPacketClasses) {
            try {
                String apiPacketName = "NMS" + nmsPacket.getClazz().getSimpleName();
                PojoClass apiPacket = PojoClassFactory.getPojoClass(Class.forName("com.bergerkiller.bukkit.common.protocol.PacketTypeClasses$" + apiPacketName));
                List<PojoField> expectedFields = nmsPacket.getPojoFields();
                List<PojoField> actualFields = apiPacket.getPojoFields();
                for (PojoField pojoField : expectedFields) {
                    if (!pojoField.getName().equals("timestamp") && !pojoField.isStatic()) {
                        PojoField actual = getFromList(actualFields, pojoField.getName(), apiPacket);
                        Assert.assertNotNull("Method missing, could not find method named " + pojoField.getName() + " for " + nmsPacket.getClazz().getSimpleName(), actual);
                        if (actual != null) {
                            Assert.assertEquals("Method types dont match, method " + pojoField.getName() + " from " + nmsPacket.getClazz().getSimpleName() + " doesn't match API type.", pojoField.getType(), actual.getType());
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
    }

    private PojoField getFromList(List<PojoField> list, String name, PojoClass apiPacket) {
        Object instance = apiPacket.getPojoConstructors().get(0).invoke(null);
        for (PojoField pojoField : list) {
            Object field = pojoField.get(instance);
            if (field instanceof SafeField) {
                SafeField<?> safeField = (SafeField<?>) field;
                if (safeField.getName().equals(name)) {
                    return pojoField;
                }
            }
        }

        return null;
    }
}
