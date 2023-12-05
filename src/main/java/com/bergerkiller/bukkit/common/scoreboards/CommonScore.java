package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundResetScorePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardScoreHandle;

public class CommonScore {

    private CommonScoreboard scoreboard;
    private String name;
    private String objName;
    private int value;
    private boolean created;

    protected CommonScore(CommonScoreboard scoreboard, String name, String objName) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.objName = objName;
    }

    /**
     * Get the unique name of this score
     *
     * @return Unique name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the score value
     *
     * @return Score value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Set the score value
     *
     * @param value to set to
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Update the score
     */
    public void update() {
        if (!this.created) {
            return;
        }
        PacketUtil.sendPacket(scoreboard.getPlayer(), PacketPlayOutScoreboardScoreHandle.createNew(name, objName, value));
    }

    /**
     * Create the score
     */
    protected void create() {
        if (this.created) {
            return;
        }
        this.created = true;
        this.update();
    }

    /**
     * Remove the score
     */
    protected void remove() {
        if (!this.created) {
            return;
        }
        PacketUtil.sendPacket(scoreboard.getPlayer(), ClientboundResetScorePacketHandle.createNew(name, objName));
        this.created = false;
    }

    protected static CommonScore copyFrom(CommonScoreboard board, CommonScore from) {
        CommonScore to = new CommonScore(board, from.name, from.objName);
        to.setValue(from.getValue());
        return to;
    }
}
