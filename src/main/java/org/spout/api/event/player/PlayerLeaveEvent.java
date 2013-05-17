/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.player;

import java.util.List;

import org.spout.api.entity.Player;
import org.spout.api.event.HandlerList;

/**
 * Called when a player leaves the server, including when they are kicked.
 */
public class PlayerLeaveEvent extends PlayerEvent {
	private static HandlerList handlers = new HandlerList();
	private String message;

	public PlayerLeaveEvent(Player p, String message) {
		super(p);
		this.message = message;
	}

	/**
	 * Gets the message to be sent to all players when leaving.
	 * @return message to be sent.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to be sent to all players when leaving.
	 * @param message to be sent.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets if the player was kicked.
	 * @return True if the player was kicked.
	 */
	public boolean isKick() {
		return false;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
