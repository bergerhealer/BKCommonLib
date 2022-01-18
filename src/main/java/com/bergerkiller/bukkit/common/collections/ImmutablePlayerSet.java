package com.bergerkiller.bukkit.common.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonListener;

/**
 * An immutable set of players. The sets are shared such that the same set of players is cached and re-used.
 * This reduces memory usage when a lot of player sets are created for the same set of players.<br>
 * <br>
 * All standard methods for adding and removing return a new immutable player set with the changed contents.
 * To check whether contents were changed as a result of a call, simply check whether the returned instance
 * is the same as the one the method was called on. When players log off, any immutable sets that
 * contain this player are purged from the cache to help the garbage collector.<br>
 * <br>
 * For storing things other than players, {@link ImmutableCachedSet} can be used instead.<br>
 * <br>
 * <b>Code sample:</b>
 * <pre>
 * ImmutablePlayerSet players = ImmutablePlayerSet.get(player1);
 * ImmutablePlayerSet players_new = players.add(player2);
 * if (players != players_new) {
 *     players = players_new;

 *     // Player was added
 * }
 * </pre>
 */
public final class ImmutablePlayerSet extends ImmutableCachedSetAbstract<Player, ImmutablePlayerSet> {
    /**
     * An empty set of players
     */
    public static final ImmutablePlayerSet EMPTY = ImmutableCachedSet.createNew(ImmutablePlayerSet::new);
    static {
        CommonListener.registerImmutablePlayerSet(EMPTY);
    }

    private ImmutablePlayerSet(Cache<Player, ImmutablePlayerSet> cache, Set<Player> values, int hashCode) {
        super(cache, values, hashCode);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Checks whether a particular player is contained
     * 
     * @param player
     * @return True if the player is contained in this set
     */
    @Override
    public boolean contains(Player player) {
        return super.contains(player);
    }

    /**
     * Checks whether all players specified are contained within this immutable player set
     * 
     * @param players to check
     * @return True if all players are contained
     */
    @Override
    public boolean containsAll(Collection<Player> players) {
        return super.containsAll(players);
    }

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the player specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param player to add
     * @return changed immutable set of players
     */
    @Override
    public ImmutablePlayerSet remove(Player player) {
        return (ImmutablePlayerSet) super.remove(player);
    }

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the players specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param players to add
     * @return changed immutable set of players
     */
    @Override
    public ImmutablePlayerSet addAll(Iterable<Player> players) {
        return super.addAll(players);
    }

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the player specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param player to add
     * @return changed immutable set of players
     */
    public ImmutablePlayerSet add(Player player) {
        return super.add(player);
    }

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the players specified removed.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param players to remove
     * @return changed immutable set of players
     */
    public ImmutablePlayerSet removeAll(Iterable<Player> players) {
        return super.removeAll(players);
    }

    /**
     * Conditionally adds or removes a player based on a boolean state.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param player to add or remove
     * @param add option, True to add, False to remove
     * @return changed immutable set of players
     */
    public final ImmutablePlayerSet addOrRemove(Player player, boolean add) {
        return add ? add(player) : remove(player);
    }

    /**
     * Gets the number of players stored in this immutable player set
     * 
     * @return player count
     */
    public int size() {
        return super.size();
    }

    /**
     * Simply returns the {@link #EMPTY} immutable player set
     * 
     * @return cleared player set
     */
    public final ImmutablePlayerSet clear() {
        return EMPTY;
    }

    /**
     * Gets an unique immutable player set instance containing only the one player specified
     * 
     * @param player
     * @return unique immutable player set singleton with the player in it
     */
    public static ImmutablePlayerSet get(Player player) {
        return EMPTY.add(player);
    }

    /**
     * Gets an unique immutable player set instance containing all the players specified
     * 
     * @param players array
     * @return immutable set with the players specified
     */
    public static ImmutablePlayerSet get(Player... players) {
        if (players.length == 0) {
            return EMPTY;
        } else {
            return EMPTY.addAll(Arrays.asList(players));
        }
    }

    /**
     * Gets an unique immutable player set instance for the set of players specified
     * 
     * @param players
     * @return unique immutable player set for this set of players
     */
    public static ImmutablePlayerSet get(Iterable<Player> players) {
        return EMPTY.addAll(players);
    }
}
