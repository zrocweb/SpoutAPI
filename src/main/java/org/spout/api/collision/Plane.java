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

public class Plane extends CollisionVolume {
	private final Vector3 point;

	private final Vector3 normal;

	public Plane(Vector3 point, Vector3 normal) {
		this.point = point;
		this.normal = normal;
	}

	public Plane(Vector3 point) {
		this(point, Vector3.UP);
	}

	public Plane() {
		this(Vector3.ZERO, Vector3.UP);
	}

	public Vector3 getNormal() {
		return normal;
	}

	public float distance(Vector3 b) {
		return b.subtract(point).dot(normal);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(b, this);
	}

	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

	@Override
	public boolean contains(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return containsBoundingBox((BoundingBox) other);
		} else if (other instanceof BoundingSphere) {
			return containsBoundingSphere((BoundingSphere) other);
		} else if (other instanceof Plane) {
			return containsPlane((Plane) other);
		} else if (other instanceof Ray) {
			return containsRay((Ray) other);
		} else if (other instanceof Segment) {
			return containsSegment((Segment) other);
		}
		return false;
	}

	public boolean containsBoundingBox(BoundingBox b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsBoundingSphere(BoundingSphere b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsPlane(Plane b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsRay(Ray b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsSegment(Segment b) {
		return CollisionHelper.contains(this, b);
	}

	@Override
	public boolean containsPoint(Vector3 b) {
		return CollisionHelper.contains(this, b);
	}

	public static Plane fromTwoVectors(Vector3 a, Vector3 b) {
		return new Plane(a, a.cross(b));
	}

	@Override
	public boolean intersects(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return intersects((BoundingBox) other);
		}
		if (other instanceof BoundingSphere) {
			return intersects((BoundingSphere) other);
		}
		if (other instanceof Segment) {
			return intersects((Segment) other);
		}
		return other instanceof Plane && intersects((Plane) other);
	}

	@Override
	public Vector3 resolve(CollisionVolume start) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollisionVolume offset(Vector3 ammount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3 getPosition() {
		return point;
	}
}
