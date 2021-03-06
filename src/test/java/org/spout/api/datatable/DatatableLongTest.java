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
package org.spout.api.datatable;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class DatatableLongTest {
	private static final int LENGTH = 1000;

	private Random r = new Random();

	@Test
	public void testLong() {
		for (int x = 0; x < LENGTH; x++) {
			checkLong(r.nextInt());
		}

		checkLong(0);

		checkLong(1);

		checkLong(-1);
		
		checkLong(Long.MAX_VALUE);
		
		checkLong(Long.MIN_VALUE);
	}

	private void checkLong(long value) {
		int key = r.nextInt();

		LongData i = new LongData(key);

		i.set(value);

		checkLong(i, key, value);

		byte[] compressed = i.compress();

		assertTrue("Compressed array wrong length", compressed.length == 8);

		int key2 = r.nextInt();

		LongData b2 = new LongData(key2);

		b2.decompress(compressed);

		checkLong(b2, key2, value);
	}

	private void checkLong(LongData i, int key, long value) {
		assertTrue("Wrong key, got " + i.hashCode() + ", expected " + key, i.hashCode() == key);

		long v = (Long)i.get();
		assertTrue("Wrong value, got " + v + ", expected: " + value, v == value);
	}
}
