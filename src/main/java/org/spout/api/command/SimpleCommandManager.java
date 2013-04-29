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
package org.spout.api.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleCommandManager implements CommandManager {
	private final Set<Command> commands = new HashSet<Command>();

	@Override
	public Set<Command> getCommands() {
		return Collections.unmodifiableSet(commands);
	}

	@Override
	public Command getCommand(String cmd, boolean createIfAbsent) {
		for (Command c : commands) {
			for (String alias : c.getAliases()) {
				if (cmd.equalsIgnoreCase(alias)) {
					return c;
				}
			}
		}

		Command command = null;
		if (createIfAbsent) {
			commands.add(command = new SimpleCommand(cmd));
		}

		return command;
	}

	@Override
	public Command getCommand(String cmd) {
		return getCommand(cmd, true);
	}

	@Override
	public Command getCommand(int id) {
		for (Command c : commands) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}
}
