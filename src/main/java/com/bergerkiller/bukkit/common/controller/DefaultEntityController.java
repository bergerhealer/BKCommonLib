package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * Does nothing but redirect to the default entity behaviour
 * 
 * @param <T> - type of Common Entity
 */
public final class DefaultEntityController<T extends CommonEntity<?>> extends EntityController<T> {

}
