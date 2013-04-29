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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.command.filter.CommandFilter;
import org.spout.api.exception.CommandException;
import org.spout.api.util.SpoutToStringStyle;

public class SimpleCommand implements Command {
	private static int UNUSED_ID = -1;
	private final String name;
	private final int id;
	private final List<String> aliases = new ArrayList<String>();
	private final Set<String> permissions = new HashSet<String>();
	private final Set<Command> children = new HashSet<Command>();
	private boolean requiresAllPermissions;
	private String help, usage;
	private int minArgs = 0, maxArgs = -1;
	private Executor executor;
	private Set<CommandFilter> filters = new HashSet<CommandFilter>();

	protected SimpleCommand(String name, String... names) {
		this.name = name;
		id = ++UNUSED_ID;
		aliases.addAll(Arrays.asList(names));
		aliases.add(name);
	}

	@Override
	public void execute(CommandSource source, String... args) throws CommandException {
		execute(source, new CommandArguments(args));
	}

	@Override
	public void execute(CommandSource source, CommandArguments args) throws CommandException {
		if (executor == null) {
			throw new CommandException("Command exists but has no set executor.");
		}

		// check permissions
		if (!hasPermission(source)) {
			throw new CommandException("You do not have permission to execute this command.");
		}

		// check argument count
		int len = args.length();
		if (len < minArgs) {
			source.sendMessage("Not enough arguments. (minimum " + minArgs + ")");
			throw new CommandException(getUsage());
		} else if (maxArgs >= 0 && len > maxArgs) { // -1 signifies infinite arguments
			source.sendMessage("Too many arguments. (maximum " + maxArgs + ")");
			throw new CommandException(getUsage());
		}

		// execute a child if applicable
		if (args.length() > 0) {
			String childRoot = args.getString(0);
			List<String> childArgs = new ArrayList<String>(args.get());
			childArgs.remove(0);
			for (Command child : children) {
				for (String alias : child.getAliases()) {
					if (alias.equalsIgnoreCase(childRoot)) {
						child.execute(source, new CommandArguments(childArgs));
						return;
					}
				}
			}
		}

		// no child found, try to execute
		for (CommandFilter filter : filters) {
			filter.validate(this, source, args);
		}
		executor.execute(source, this, args);
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	@Override
	public Command setExecutor(Executor executor) {
		this.executor = executor;
		return this;
	}

	@Override
	public Set<CommandFilter> getFilters() {
		return filters;
	}

	@Override
	public Command addFilter(CommandFilter... filter) {
		filters.addAll(Arrays.asList(filter));
		return this;
	}

	@Override
	public Set<Command> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	@Override
	public Command getChild(String name, boolean createIfAbsent) {
		for (Command child : children) {
			for (String alias : child.getAliases()) {
				if (alias.equalsIgnoreCase(name)) {
					return child;
				}
			}
		}

		Command command = null;
		if (createIfAbsent) {
			children.add(command = new SimpleCommand(name));
		}

		return command;
	}

	@Override
	public Command getChild(String name) {
		return getChild(name, true);
	}

	@Override
	public List<String> getAliases() {
		return Collections.unmodifiableList(aliases);
	}

	@Override
	public Command addAlias(String... alias) {
		aliases.addAll(Arrays.asList(alias));
		return this;
	}

	@Override
	public Command removeAlias(String... alias) {
		aliases.removeAll(Arrays.asList(alias));
		return this;
	}

	@Override
	public String getHelp() {
		return help;
	}

	@Override
	public Command setHelp(String help) {
		this.help = help;
		return this;
	}

	@Override
	public String getUsage() {
		return usage;
	}

	@Override
	public Command setUsage(String usage) {
		this.usage = usage;
		return this;
	}

	@Override
	public Set<String> getPermissions() {
		return Collections.unmodifiableSet(permissions);
	}

	@Override
	public Command addPermission(String... perm) {
		permissions.addAll(Arrays.asList(perm));
		return this;
	}

	@Override
	public Command removePermission(String... perm) {
		permissions.removeAll(Arrays.asList(perm));
		return this;
	}

	@Override
	public boolean hasPermission(CommandSource source) {
		if (permissions.isEmpty()) {
			return true;
		}

		boolean success = requiresAllPermissions;
		for (String perm : permissions) {
			if (requiresAllPermissions) {
				success &= source.hasPermission(perm);
			} else {
				success |= source.hasPermission(perm);
			}
		}
		return success;
	}

	@Override
	public boolean requiresAllPermissions() {
		return requiresAllPermissions;
	}

	@Override
	public Command setRequiresAllPermissions(boolean requiresAllPermissions) {
		this.requiresAllPermissions = requiresAllPermissions;
		return this;
	}

	@Override
	public Command setArgumentBounds(int min, int max) {
		minArgs = min;
		maxArgs = max;
		return this;
	}

	@Override
	public int getMaxArguments() {
		return maxArgs;
	}

	@Override
	public Command setMaxArguments(int max) {
		maxArgs = max;
		return this;
	}

	@Override
	public int getMinArguments() {
		return minArgs;
	}

	@Override
	public Command setMinArguments(int min) {
		minArgs = min;
		return this;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleCommand)) {
			return false;
		}

		SimpleCommand other = (SimpleCommand) obj;
		return new EqualsBuilder()
				.append(id, other.id)
				.append(name, other.name)
				.build();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(SpoutToStringStyle.INSTANCE)
				.append("id", id)
				.append("name", name)
				.build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.append(id)
				.build();
	}
}
