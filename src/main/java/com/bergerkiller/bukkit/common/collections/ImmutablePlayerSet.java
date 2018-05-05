package com.bergerkiller.bukkit.common.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * An immutable set of players. The sets are shared such that the same set of players is cached and re-used.
 * This reduces memory usage when a lot of player sets are created for the same set of players.<br>
 * <br>
 * All standard methods for adding and removing return a new immutable player set with the changed contents.
 * To check whether contents were changed as a result of a call, simply check whether the returned instance
 * is the same as the one the method was called on.<br>
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
public abstract class ImmutablePlayerSet implements Iterable<Player> {
    /**
     * An empty set of players
     */
    public static final ImmutablePlayerSet EMPTY = new ImmutablePlayerSet() {
        @Override
        public Iterator<Player> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public boolean contains(Player player) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<Player> players) {
            return false;
        }

        @Override
        public ImmutablePlayerSet add(Player player) {
            return get(player);
        }

        @Override
        public ImmutablePlayerSet addAll(Iterable<Player> players) {
            return get(players);
        }

        @Override
        public ImmutablePlayerSet remove(Player player) {
            return this;
        }

        @Override
        public ImmutablePlayerSet removeAll(Iterable<Player> players) {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }
    };

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

    /**
     * Checks whether a particular player is contained
     * 
     * @param player
     * @return True if the player is contained in this set
     */
    public abstract boolean contains(Player player);

    /**
     * Checks whether all players specified are contained within this immutable player set
     * 
     * @param players to check
     * @return True if all players are contained
     */
    public abstract boolean containsAll(Collection<Player> players);

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the player specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param player to add
     * @return changed immutable set of players
     */
    public abstract ImmutablePlayerSet remove(Player player);

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the players specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param players to add
     * @return changed immutable set of players
     */
    public abstract ImmutablePlayerSet addAll(Iterable<Player> players);

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the player specified added.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param player to add
     * @return changed immutable set of players
     */
    public abstract ImmutablePlayerSet add(Player player);

    /**
     * Returns a new immutable player set with the contents of this set, with
     * the players specified removed.
     * If no changes occur, the same immutable set instance is returned.
     * 
     * @param players to remove
     * @return changed immutable set of players
     */
    public abstract ImmutablePlayerSet removeAll(Iterable<Player> players);

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
    public abstract int size();

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
        return get(Collections.singleton(player));
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
            return get(Arrays.asList(players));
        }
    }

    /**
     * Gets an unique immutable player set instance for the set of players specified
     * 
     * @param players
     * @return unique immutable player set for this set of players
     */
    public static ImmutablePlayerSet get(Iterable<Player> players) {
        return CommonPlugin.getInstance().getImmutablePlayerSetManager().get(players);
    }
}
