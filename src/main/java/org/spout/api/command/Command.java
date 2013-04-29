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

import java.util.List;
import java.util.Set;

import org.spout.api.command.filter.CommandFilter;
import org.spout.api.exception.CommandException;
import org.spout.api.util.Named;

/**
 * Represents a command sent by a {@link CommandSource}.
 */
public interface Command extends Named {
	/**
	 * Executes the command in the executor it is currently set to.
	 *
	 * @param source that sent the command
	 * @param args command arguments
	 * @throws CommandException if the command executor is null or if
	 * {@link Executor#execute(CommandSource, Command, CommandArguments)}
	 * throws a CommandException.
	 */
	public void execute(CommandSource source, String... args) throws CommandException;

	/**
	 * Executes the command in the executor it is currently set to.
	 *
	 * @param source that sent the command
	 * @param args command arguments
	 * @throws CommandException if the command executor is null or if
	 * {@link Executor#execute(CommandSource, Command, CommandArguments)}
	 * throws a CommandException.
	 */
	public void execute(CommandSource source, CommandArguments args) throws CommandException;

	/**
	 * Returns the {@link Executor} associated with this command.
	 *
	 * @return command's executor
	 */
	public Executor getExecutor();

	/**
	 * Sets the {@link Executor} associated with this command.
	 *
	 * @param executor to set
	 * @return this command
	 */
	public Command setExecutor(Executor executor);

	/**
	 * Returns the filter to be run before execution.
	 *
	 * @return filter to run
	 */
	public Set<CommandFilter> getFilters();

	/**
	 * Sets the filter to be run before execution.
	 *
	 * @param filter to run
	 * @return this command
	 */
	public Command addFilter(CommandFilter... filter);

	/**
	 * Returns a set of all the command's children.
	 *
	 * @return children commands
	 */
	public Set<Command> getChildren();

	/**
	 * Returns a child of this command with the specified. Will create a new
	 * unless otherwise specified.
	 *
	 * @param name name of command
	 * @param createIfAbsent true if should create command if non-existent
	 * @return new child or existing child
	 */
	public Command getChild(String name, boolean createIfAbsent);

	/**
	 * Returns a child of this command with the specified. Will create a new
	 * unless otherwise specified.
	 *
	 * @param name name of command
	 * @return new child or existing child
	 */
	public Command getChild(String name);


	/**
	 * Returns all the names that the command is recognized under.
	 *
	 * @return list of names the command is called
	 */
	public List<String> getAliases();

	/**
	 * Adds a name that the command is recognized under.
	 *
	 * @param alias to add
	 * @return this command
	 */
	public Command addAlias(String... alias);

	/**
	 * Removes the names that a command is recognized under.
	 *
	 * @param alias to remove
	 * @return this command
	 */
	public Command removeAlias(String... alias);

	/**
	 * Returns the command's help information.
	 *
	 * @return help info
	 */
	public String getHelp();

	/**
	 * Sets the command's help specification.
	 *
	 * @param help to display
	 * @return this command
	 */
	public Command setHelp(String help);

	/**
	 * Returns the correct usage for this plugin.
	 *
	 * @return command usage
	 */
	public String getUsage();

	/**
	 * Sets the Command's correct usage
	 *
	 * @param usage of command
	 * @return this command
	 */
	public Command setUsage(String usage);

	/**
	 * Returns all the permissions associated with this command.
	 *
	 * @return all permissions
	 */
	public Set<String> getPermissions();

	/**
	 * Adds permissions to the command.
	 *
	 * @param perm to add
	 * @return this command
	 */
	public Command addPermission(String... perm);

	/**
	 * Removes permissions from this command.
	 *
	 * @param perm to remove
	 * @return this command
	 */
	public Command removePermission(String... perm);

	/**
	 * Returns true if the specified source has permissions to execute this
	 * command.
	 *
	 * @param source to check
	 * @return true if specified source has permission
	 */
	public boolean hasPermission(CommandSource source);

	/**
	 * Returns true if this command requires all the permissions specified by
	 * {@link #getPermissions()} to run for a {@link CommandSource}.
	 *
	 * @return true if command requires all permissions
	 */
	public boolean requiresAllPermissions();

	/**
	 * Sets if this command requires all the permissions specified by
	 * {@link #getPermissions()} to run for a {@link CommandSource}.
	 *
	 * @param requiresAllPermissions true if all perms are reuquired
	 * @return this command
	 */
	public Command setRequiresAllPermissions(boolean requiresAllPermissions);

	/**
	 * Sets the minimum and maximum arguments in which this command can operate.
	 *
	 * @param min minimum amount of arguments
	 * @param max maximum amount of arguments (-1 for no limit)
	 * @return this command
	 */
	public Command setArgumentBounds(int min, int max);

	/**
	 * Returns the maximum amount of arguments for this command.
	 *
	 * @return maximum amount of arguments
	 */
	public int getMaxArguments();

	/**
	 * Sets the maximum arguments for this command.
	 *
	 * @param max maximum amount of arguments (-1 for no limit)
	 * @return this command
	 */
	public Command setMaxArguments(int max);

	/**
	 * Returns the minimum amount of arguments for this command.
	 *
	 * @return minimum amount of arguments.
	 */
	public int getMinArguments();

	/**
	 * Sets the minimum arguments for this command.
	 *
	 * @param min minimum amount of arguments
	 * @return this command
	 */
	public Command setMinArguments(int min);

	/**
	 * Returns this command's unique identifier.
	 *
	 * @return identifier
	 */
	public int getId();
}
