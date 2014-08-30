package com.bergerkiller.bukkit.common.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bergerkiller.bukkit.common.protocol.PacketTypeClasses.NMSPacket;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;

public class ProtocolTest {
	private final List<Class<?>> packetClasses = new ArrayList<Class<?>>();
	
	@Test
	public void testPacketsFound() {
		for(PojoClass pojoClass : PojoClassFactory.enumerateClassesByExtendingType("com.bergerkiller.bukkit.common.protocol.PacketTypeClasses", NMSPacket.class, null)) {
			packetClasses.add(pojoClass.getClazz());
		}
		
		Assert.assertTrue(packetClasses.size() > 0);
	}
}