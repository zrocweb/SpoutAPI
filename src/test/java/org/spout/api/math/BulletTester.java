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

import org.junit.Test;

import org.spout.math.Matrix;
import org.spout.math.Quaternion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BulletTester {
	final float eps = 0.01f;

	@Test
	public void testBulletMatrixConvert() {
		Matrix4f bullet = new Matrix4f();
		bullet.setElement(0, 0, 10f);
		Matrix spout = BulletConverter.toMatrix(bullet);
		assertTrue(spout.get(0, 0) == 10f);
	}

	@Test
	public void testBulletToSpoutQuaternionConversion() {
		final Quat4f bulletQuaternion = new Quat4f(1f, -5f, 0f, 1f);
		final Quaternion spoutQuaternion = BulletConverter.toQuaternion(bulletQuaternion);
		assertEquals(bulletQuaternion.w, spoutQuaternion.getW(), eps);
		assertEquals(bulletQuaternion.x, spoutQuaternion.getX(), eps);
		assertEquals(bulletQuaternion.y, spoutQuaternion.getY(), eps);
		assertEquals(bulletQuaternion.z, spoutQuaternion.getZ(), eps);
	}

	@Test
	public void testSpoutToBulletQuaternionConversion() {
		final Quaternion spoutQuaternion = new Quaternion(1f, -5f, 0f, 1f);
		final Quat4f bulletQuaternion = BulletConverter.toQuaternionf(spoutQuaternion);
		assertEquals(spoutQuaternion.getW(), bulletQuaternion.w, eps);
		assertEquals(spoutQuaternion.getX(), bulletQuaternion.x, eps);
		assertEquals(spoutQuaternion.getY(), bulletQuaternion.y, eps);
		assertEquals(spoutQuaternion.getZ(), bulletQuaternion.z, eps);
	}
}
