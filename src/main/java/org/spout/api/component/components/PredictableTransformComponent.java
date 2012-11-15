/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.component.components;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;

public class PredictableTransformComponent extends TransformComponent {

	private Transform transformRender = null;
	private Transform lastTransform = null;

	public void updateRender(float dt) {
		if(transformRender == null){
			transformRender = getTransformLive().copy();
			return;
		}

		if(getOwner() == ((Client)Spout.getEngine()).getActivePlayer()){
			transformRender = lastTransform;
		}else{
			Point movement = lastTransform.getPosition().subtract(transformRender.getPosition());
			movement = movement.multiply(dt);
			transformRender.setPosition(transformRender.getPosition().add(movement));
		}
	}

	public Transform getRenderTransform(){
		return transformRender; // Don't need to send back a copy, only the render thread call it
	}
	
	@Override
	public void setTransform(Transform transform) {
		super.setTransform(transform);
		lastTransform = transform.copy();
		copySnapshot(); // Why if i don't do that, i keep a freeze ???? There are a lock when the engine load chunk ??? Need info !
	}
	
}