package org.spout.api.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.btBoxShape;

public class ShapeConstants {
	/**
	 * Shape designed for your basic one meter cubed voxel.
	 */
	public static final btBoxShape CUBIC_BOX_SHAPE = new btBoxShape(new Vector3(1f, 1f, 1f));
}
