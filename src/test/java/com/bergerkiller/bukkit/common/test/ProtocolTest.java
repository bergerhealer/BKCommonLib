package com.bergerkiller.bukkit.common.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.Packet;

import org.junit.Assert;
import org.junit.Test;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PacketTypeClasses.NMSPacket;
import com.openpojo.log.Logger;
import com.openpojo.log.LoggerFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.impl.PojoClassFactory;

public class ProtocolTest {
	private final Logger logger = LoggerFactory.getLogger("TEST");
	private final List<Class<?>> packetClasses = new ArrayList<Class<?>>();
	
	public ProtocolTest() {
		//Scan for API packets
		for(PojoClass pojoClass : PojoClassFactory.enumerateClassesByExtendingType("com.bergerkiller.bukkit.common.protocol", NMSPacket.class, null)) {
			packetClasses.add(pojoClass.getClazz());
		}
	}
	
	@Test
	public void testPacketsLoad() {
		int loaded = 0;
		for(Class<?> clazz : packetClasses) {
			try {
				getClass().getClassLoader().loadClass(clazz.getName());
				loaded += 1;
			} catch (Exception e) {
				logger.error("Failed to load packet named %s!", clazz.getSimpleName());
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
			logger.fatal("Failed to init PacketType.java");
		}
	}
	
	@Test
	public void testMissingPackets() {
		Assert.assertFalse("No API packets were found!", packetClasses.isEmpty());
		int expected = 0;
		//Scan for NMS packets
		List<PojoClass> scanned = PojoClassFactory.enumerateClassesByExtendingType("net.minecraft.server", Packet.class, new PojoClassFilter() {

			@Override
			public boolean include(PojoClass pojoClass) {
				return pojoClass.getName().contains("PacketPlay");
			}
		});
		expected = scanned.size();
		int actual = packetClasses.size();
		Assert.assertEquals("Packets missing, expected " + expected + " but found " + actual, expected, actual);
	}
	
	public void testMissingMethods() {
		//TODO: Check if packet methods miss.
	}
}