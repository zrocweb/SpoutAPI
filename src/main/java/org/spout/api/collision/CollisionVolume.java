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
package org.spout.api.collision;

import org.spout.api.math.Vector3;

/**
 * Defines a Volume that can collide with another Volume
 *
 *
 */
public abstract class CollisionVolume {
	CollisionStrategy strat  = CollisionStrategy.SOLID;
	
	
	public CollisionStrategy getStrategy() {
		return strat;
	}
	
	public void setStrategy(CollisionStrategy strat) {
		this.strat = strat;
	}
	
	
	public abstract Vector3 getPosition();

	public abstract CollisionVolume offset(Vector3 amount);
	
	
	/**
	 * Checks for Intersection
	 *
	 * @param other
	 * @return
	 */
	public abstract boolean intersects(CollisionVolume other);

	/**
	 * Checks for containing
	 *
	 * @param other
	 * @return
	 */
	public abstract boolean contains(CollisionVolume other);


	/**
	 * Checks if the volume contains the other Vector3.
	 *
	 * @param b
	 * @return
	 */
	public abstract boolean containsPoint(Vector3 b);

	/**
	 * Defines a sweep test from one start to an end
	 *
	 * @param other
	 * @return
	 */
	public abstract Vector3 resolve(CollisionVolume other);
}
