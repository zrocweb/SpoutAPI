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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.Executor;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.WrappedCommandException;
import org.spout.api.util.ReflectionUtils;

/**
 * Allows for method-registration of commands.
 */
public abstract class AnnotatedCommandExecutor implements Executor {
	private final Map<Command, Method> cmdMap = new HashMap<Command, Method>();

	private static boolean isValidMethod(Method method) {
		return hasValidModifiers(method) && hasValidParameters(method) && hasCommandAnnotation(method);
	}

	private static boolean hasValidModifiers(Method method) {
		int mod = method.getModifiers();
		return !Modifier.isAbstract(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)
				&& !Modifier.isStatic(mod) && Modifier.isPublic(mod);
	}

	private static boolean hasValidParameters(Method method) {
		Class<?>[] params = method.getParameterTypes();
		return params.length == 2 && CommandSource.class.equals(params[0]) && CommandArguments.class.equals(params[1]);
	}

	private static boolean hasCommandAnnotation(Method method) {
		return method.isAnnotationPresent(org.spout.api.command.annotated.Command.class);
	}

	/**
	 * Registers all the defined commands by method in this class.
	 *
	 * @param parent to register commands under
	 */
	public void register(Command parent) {
		for (Method method : getClass().getMethods()) {
			method.setAccessible(true);
			if (!isValidMethod(method)) {
				continue;
			}

			// create the command
			Engine engine = Spout.getEngine();
			org.spout.api.command.annotated.Command a = method.getAnnotation(org.spout.api.command.annotated.Command.class);
			Command command;
			if (parent != null) {
				command = parent.getChild(a.aliases()[0]);
			} else {
				command = engine.getCommandManager().getCommand(a.aliases()[0]);
			}

			command.addAlias(a.aliases());
			command.setHelp(a.desc());
			command.setUsage(a.usage());
			command.setArgumentBounds(a.min(), a.max());

			// add the permissions
			if (method.isAnnotationPresent(CommandPermissions.class)) {
				CommandPermissions perms = method.getAnnotation(CommandPermissions.class);
				command.addPermission(perms.value());
				command.setRequiresAllPermissions(perms.requireAll());
			}

			// add binding
			if (method.isAnnotationPresent(Binding.class) && engine instanceof Client) {
				int max = a.max();
				if (max < 1 && max != -1) {
					throw new IllegalArgumentException("Command binding must allow at least 1 argument.");
				}
				Binding binding = method.getAnnotation(Binding.class);
				org.spout.api.input.Binding b = new org.spout.api.input.Binding(command.getName(), binding.value(), binding.mouse()).setAsync(binding.async());
				((Client) engine).getInputManager().bind(b);
			}

			// add filter
			if (method.isAnnotationPresent(CommandFilter.class)) {
				CommandFilter cfa = method.getAnnotation(CommandFilter.class);
				Class<? extends org.spout.api.command.filter.CommandFilter>[] filterTypes = cfa.value();
				org.spout.api.command.filter.CommandFilter[] filters = new org.spout.api.command.filter.CommandFilter[filterTypes.length];
				for (int i = 0; i < filters.length; i++) {
					try {
						filters[i] = filterTypes[i].newInstance();
					} catch (InstantiationException e) {
						throw new IllegalArgumentException("All CommandFilters must have an empty constructor.");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				command.addFilter(filters);
			}

			// finally set the executor
			command.setExecutor(this);
			cmdMap.put(command, method);
		}
	}

	/**
	 * Registers all the defined commands by method in this class.
	 */
	public void register() {
		register(null);
	}

	@Override
	public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
		Method method = cmdMap.get(command);
		if (method != null) {
			method.setAccessible(true);
			try {
				method.invoke(this, source, args);
			} catch (IllegalAccessException e) {
				throw new WrappedCommandException(e);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				if (cause == null) {
					throw new WrappedCommandException(e);
				}

				if (cause instanceof CommandException) {
					throw (CommandException) cause;
				}

				throw new WrappedCommandException(e);
			}
		}
	}
}
