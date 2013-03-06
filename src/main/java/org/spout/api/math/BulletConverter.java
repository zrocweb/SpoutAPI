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
package org.spout.api.math;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;

import org.spout.math.Matrix;
import org.spout.math.Quaternion;
import org.spout.math.QuaternionMath;
import org.spout.math.Vector3;
import org.spout.math.VectorMath;

public class BulletConverter {
	public static com.bulletphysics.linearmath.Transform toPhysicsTransform(Transform transform) {
		final com.bulletphysics.linearmath.Transform physicsTransform = new com.bulletphysics.linearmath.Transform();
		final Vector3f worldSpace = toVector3f(transform.getPosition());
		final Quat4f worldRotation = toQuaternionf(transform.getRotation());
		physicsTransform.set(new Matrix4f(worldRotation, worldSpace, 1));
		return physicsTransform;
	}

	public static Transform toSceneTransform(Transform liveState, com.bulletphysics.linearmath.Transform transform) {
		final Matrix4f physicsMatrix = transform.getMatrix(new Matrix4f());
		final Vector3f physicsSpace = new Vector3f();
		physicsMatrix.get(physicsSpace);
		final Quat4f physicsRotation = new Quat4f();
		physicsMatrix.get(physicsRotation);
		final Vector3 sceneSpace = toVector3(physicsSpace);
		final Quaternion sceneRotation = toQuaternion(physicsRotation);
		liveState.setPosition(new Point(sceneSpace, liveState.getPosition().getWorld()));
		liveState.setRotation(sceneRotation);
		return liveState;
	}

	/**
	 * Transforms a vecmath 3D vector to a Spout 3D vector.
	 * @param vector The vecmath 3D vector
	 * @return The vector as a Spout 3D vector
	 */
	public static Vector3 toVector3(Vector3f vector) {
		return new Vector3(vector.x, vector.y, vector.z);
	}

	/**
	 * Transforms a Spout 3D vector to a vecmath 3D vector.
	 * @param vector The Spout 3D vector
	 * @return The vector as a vecmath 3D vector
	 */
	public static Vector3f toVector3f(Vector3 vector) {
		return new Vector3f(vector.getX(), vector.getY(), vector.getZ());
	}

	/**
	 * Transforms a vecmath quaternion to a Spout quaternion.
	 * @param quaternion The spout quaternion
	 * @return The quaternion as a vecmath quaternion
	 */
	public static Quat4f toQuaternionf(Quaternion quaternion) {
		return new Quat4f(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW());
	}

	/**
	 * Transforms a Spout quaternion to a vecmath quaternion.
	 * @param quaternion The vecmath 3D quaternion
	 * @return The quaternion as a spout 3D quaternion
	 */
	public static Quaternion toQuaternion(Quat4f quaternion) {
		return new Quaternion(quaternion.x, quaternion.y, quaternion.z, quaternion.w, false);
	}

	/**
	 * Transforms a vecmath matrix to a Spout matrix.
	 * @param matrix The vecmath matrix
	 * @return The matrix as a Spout matrix
	 */
	public static Matrix toMatrix(Matrix4f matrix) {
		Matrix out = new Matrix(4);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				out.set(x, y, matrix.getElement(x, y));
			}
		}
		return out;
	}
}
