package com.bergerkiller.bukkit.common.entity;

/**
 * A base class for various kinds of Entity Controllers
 */
public class CommonEntityController<T extends CommonEntity<?>> {
	protected T entity;

	/**
	 * Gets the Common Entity this controller is attached to.
	 * If this controller is not yet attached to something, NULL is returned.
	 * 
	 * @return the Common Entity
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * Called as soon as this Controller is attached to an Entity.
	 * The entity is already fully attached and spawned prior to this method.
	 * This method is the first time you can access the Entity from this Controller.
	 */
	public void onAttached() {
	}

	/**
	 * Called as soon as this Controller is detached from an Entity.
	 * When this method ends, the entity is fully detached.
	 * This method is the last time you can access the Entity from this Controller
	 */
	public void onDetached() {
	}
}
