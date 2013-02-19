package com.bergerkiller.bukkit.common.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

import net.minecraft.server.v1_4_R1.Connection;
import net.minecraft.server.v1_4_R1.Packet;

/**
 * Wraps around another packet to create an undetectable packet type to send to clients undetected
 */
class CommonSilentPacket extends Packet {
	static {
		PacketUtil.registerPacketToId(CommonSilentPacket.class, 0);
	}

	private final Packet packet;

	public CommonSilentPacket(Object packet) {
		this.packet = (Packet) packet;
		PacketFields.DEFAULT.packetID.transfer(packet, this);
	}

	@Override
	public int a() {
		return this.packet.a();
	}

	@Override
	public void a(DataInputStream in) throws IOException {
		throw new UnsupportedOperationException("Can not load a silent packet from a stream");
	}

	@Override
	public void a(DataOutputStream out) throws IOException {
		this.packet.a(out);
	}

	@Override
	public void handle(Connection arg0) {
		// Nothing happens here to avoid problems
	}
}
