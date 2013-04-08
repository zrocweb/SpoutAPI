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

import java.util.ArrayList;

import org.spout.api.math.Vector3;

/**
 * Defines a Heirachial Collision Volume
 *
 */
public class CollisionModel extends CollisionVolume {
	private CollisionVolume area;
	
	private ArrayList<CollisionModel> children = new ArrayList<CollisionModel>();
	
	private Vector3 origin;
	
	public CollisionModel() {
		area = new BoundingBox();
		
	}
	
	public CollisionModel(CollisionVolume base) {
		if (base instanceof CollisionModel) throw new IllegalArgumentException("Cannot create a collision model with a collision model as an area");
		area = base;
	}

	public void setArea(CollisionVolume area) {
		this.area = area;
	}

	public void addChild(CollisionVolume child) {
		if (child instanceof CollisionModel) {
			area = area.offset(origin); // TODO: What...actually is this called for? It's offsetting the base area by the origin point every time a child is added? What?
			children.add((CollisionModel) child);
		} else {
			CollisionModel c = new CollisionModel(child);
			addChild(c);
		}
	}

	public CollisionVolume getVolume() {
		return area;
	}

	@Override
	public CollisionVolume offset(Vector3 amount) {
		// TODO: This method is broken for now, not updated for immutable collision objects.
		origin = origin.add(amount);
		for (CollisionModel m : children) {
			m.offset(amount);
		}
		return this;
	}

	@Override
	public boolean intersects(CollisionVolume other) {
		if (other instanceof CollisionModel) {
			if (!area.intersects(((CollisionModel) other).getVolume()))
				return false;
		}
		if (!area.intersects(other)) {
			return false; // Return false if this volume doesn't intersect at all
		}
		if (children.size() == 0) {
			return true; // Return true if we have no children, and we intersected above
		}
		for (CollisionModel m : children) {
			if (m.intersects(other)) return true; // Return true if any children intersect
		}
		return false;
	}

	@Override
	public boolean contains(CollisionVolume other) {
		if (other instanceof CollisionModel) {
			if (!area.contains(((CollisionModel)other).getVolume())) return false;
		}
		if (!area.contains(other)) {
			return false; // Return false if this volume doesn't contain the other at all
		}
		if (children.size() == 0) {
			return true; // Return true if we have no children, and we contained the other above
		}
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for (CollisionModel m : children) {
			if (m.contains(other)) return true; // Return true if any children contain the other
		}
		return false;
	}

	@Override
	public boolean containsPoint(Vector3 b) {
		if (!area.containsPoint(b)) {
			return false; // Return false if this volume doesn't contain the point at all
		}
		if (children.size() == 0) {
			return true; // Return true if we have no children, and we contained the point above
		}
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for (CollisionModel m : children) {
			if (m.containsPoint(b)) return true; // Return true if any children contain the point
		}
		return false;
	}

	@Override
	public Vector3 resolve(CollisionVolume other) {
		
		//TODO make this resolve with children
		if (other instanceof CollisionModel) {
			return area.resolve(((CollisionModel)other).getVolume());
		}

		return area.resolve(other);
	}

	@Override
	public Vector3 getPosition() {
		return origin;
	}

	public void setPosition(Vector3 position) {
		this.origin = position;
	}
	
}
