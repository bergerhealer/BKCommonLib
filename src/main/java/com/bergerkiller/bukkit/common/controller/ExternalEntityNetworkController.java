package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * A type of Entity Network Controller that was created externally, not by BKCommonLib.
 * This is the case for plugins that replace the EntityTrackerEntry.
 * 
 * @param <T> - type of Common Entity
 */
public class ExternalEntityNetworkController<T extends CommonEntity<?>> extends EntityNetworkController<T> {

}
