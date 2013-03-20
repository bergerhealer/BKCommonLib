package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * The network controller returned for the default, unchanged types of entity network controller.
 * 
 * @param <T> - Common Entity type
 */
public class DefaultEntityNetworkController<T extends CommonEntity<?>> extends EntityNetworkController<T> {

}
