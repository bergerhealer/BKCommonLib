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
	
	@Test
	public void testPacketsFound() {
		for(PojoClass pojoClass : PojoClassFactory.enumerateClassesByExtendingType("com.bergerkiller.bukkit.common.protocol", NMSPacket.class, null)) {
			packetClasses.add(pojoClass.getClazz());
		}
		
		Assert.assertNotEquals("No packets were found while scanning in protocol package!", packetClasses.size(), 0);
		logger.info("Found %d packets.", packetClasses.size());
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
		int packetsFound = 0;
		//Scan for NMS packets
		List<PojoClass> scanned = PojoClassFactory.enumerateClassesByExtendingType("net.minecraft.server", Packet.class, new PojoClassFilter() {

			@Override
			public boolean include(PojoClass pojoClass) {
				return pojoClass.getName().contains("PacketPlay");
			}
		});
		packetsFound = scanned.size();
		
		Assert.assertEquals("Packets missing, expected " + packetsFound + " but found " + packetClasses.size(), packetsFound, packetClasses.size());
	}
	
	public void testMissingMethods() {
		//TODO: Check if packet methods miss.
	}
}