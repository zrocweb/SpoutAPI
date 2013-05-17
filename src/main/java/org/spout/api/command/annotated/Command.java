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
package org.spout.api.command.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This allows for annotation-based command registration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	/**
	 * Returns all the aliases this command is recognized under.
	 *
	 * @return aliases of command
	 */
	public String[] aliases();

	/**
	 * Returns the proper usage of the command.
	 *
	 * @return proper usage
	 */
	public String usage() default "";

	/**
	 * Returns a description of the command.
	 *
	 * @return description of the command
	 */
	public String desc();

	/**
	 * Returns the minimum amount of arguments for the command to run.
	 *
	 * @return minimum to run
	 */
	public int min() default 0;

	/**
	 * Returns the maximum amount of arguments for the command to run.
	 *
	 * @return max to run
	 */
	public int max() default -1;
}
