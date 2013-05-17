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
package org.spout.api;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import org.spout.api.command.Command;
import org.spout.api.command.CommandSource;
import org.spout.api.command.CommandManager;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.EventManager;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.inventory.recipe.RecipeManager;
import org.spout.api.permissions.DefaultPermissions;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.plugin.PluginManager;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.Named;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * Represents the core of an implementation of an engine (powers a game).
 */
public interface Engine extends Named {
	/**
	 * The permissions to be used for standard broadcasts. Implementations should register
	 * this permissions with {@link org.spout.api.permissions.DefaultPermissions}
	 */
	public static final String STANDARD_BROADCAST_PERMISSION = "spout.broadcast.standard";
	public static final String STANDARD_CHAT_PREFIX = "spout.chat.receive.";

	/**
	 * Returns the String version of the API.
	 * 
	 * @return version
	 */
	public String getAPIVersion();

	/**
	 * Gets the name of this game's implementation
	 * @return name of the implementation
	 */
	@Override
	public String getName();

	/**
	 * Gets the version of this game's implementation
	 * @return build version
	 */
	public String getVersion();

	/**
	 * Returns a Set of all permissions subjects with the provided node. Plugins wishing
	 * to modify the result of this event should listen to the {@link org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent} event.
	 * @param permission The permission to check
	 * @return Every {@link PermissionsSubject} with the specified node
	 */
	public Set<PermissionsSubject> getAllWithNode(String permission);

	/**
	 * Gets singleton instance of the plugin manager, used to interact with
	 * other plugins and register events.
	 * @return plugin manager instance.
	 */
	public PluginManager getPluginManager();

	/**
	 * Gets the {@link Logger} instance that is used to write to the console.
	 * @return logger
	 */
	public Logger getLogger();

	/**
	 * Gets the update folder. The update folder is used to safely update
	 * plugins at the right moment on a plugin load.
	 * <p/>
	 * The update folder name is relative to the plugins folder.
	 * @return {@link File} of the update folder
	 */
	public File getUpdateFolder();

	/**
	 * Gets the configuration folder for the game
	 * @return {@link File} of the configuration folder
	 */
	public File getConfigFolder();

	/**
	 * Gets the folder which contains world, entity and player data.
	 * @return {@link File} of the data folder.
	 */
	public File getDataFolder();

	/**
	 * Gets the folder which contains plugins.
	 * @return {@link File} of the plugin folder.
	 */
	public File getPluginFolder();

	/**
	 * Creates a new Session
	 * @param channel the associated channel
	 * @return the session
	 */
	public Session newSession(Channel channel);

	/**
	 * Gets the {@link Entity} with the matching unique id
	 * <br/> <br/>
	 * Performs a search on each world and then searches each world respectively
	 * for the entity, stopping when it is found, or after all the worlds have
	 * been searched upon failure.
	 * @param uid to search and match
	 * @return {@link Entity} that matched the uid, or null if none was found
	 */
	@SnapshotRead
	public Entity getEntity(UUID uid);

	/**
	 * Returns all player names that have ever played on this Game, whether they are online or not.
	 * @return all the player names
	 */
	public List<String> getAllPlayers();

	/**
	 * Gets the {@link Player} by the given username. <br/>
	 * <br/>
	 * If searching for the exact name, this method will iterate and check for
	 * exact matches. <br/>
	 * <br/>
	 * Otherwise, this method will iterate over over all players and find the closest match
	 * to the given name, by comparing the length of other player names that
	 * start with the given parameter. <br/>
	 * <br/>
	 * This method is case-insensitive.
	 * @param name to look up
	 * @param exact Whether to use exact lookup
	 * @return Player if found, else null
	 */
	public Player getPlayer(String name, boolean exact);

	/**
	 * Matches the given username to all players that contain it in their name.
	 * <p/>
	 * If no matches are found, an empty collection will be returned. The return
	 * will always be non-null.
	 * @param name to match
	 * @return Collection of all possible matches
	 */
	public Collection<Player> matchPlayer(String name);

	/**
	 * Searches for an actively loaded world that exactly matches the given
	 * name. <br/>
	 * <br/>
	 * If searching for the exact name, this method will iterate and check for
	 * exact matches. <br/>
	 * <br/>
	 * Otherwise, this method will iterate over over all worlds and find the closest match
	 * to the given name, by comparing the length of other player names that
	 * start with the given parameter. <br/>
	 * <br/>
	 * <p/>
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 * @param name of the world to search for
	 * @param exact Whether to use exact lookup
	 * @return world if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(String name, boolean exact);

	/**
	 * Searches for an actively loaded world that exactly matches the given
	 * name. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getName()}. <br/>
	 * <br/>
	 * <p/>
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 * @param name of the world to search for
	 * @return {@link World} if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(String name);

	/**
	 * Searches for actively loaded worlds that matches the given
	 * name. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getName()} <br/>
	 * <br/>
	 * <p/>
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 * @param name of the world to search for, or part of it
	 * @return a collection of worlds that matched the name
	 */
	@LiveRead
	@SnapshotRead
	public Collection<World> matchWorld(String name);

	/**
	 * Searches for an actively loaded world has the given {@link UUID}. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getUID()}. <br/>
	 * <br/>
	 * <p/>
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 * @param uid of the world to search for
	 * @return {@link World} if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(UUID uid);

	/**
	 * Gets a List of all currently loaded worlds
	 * <p/>
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 * @return {@link Collection} of actively loaded worlds
	 */
	@LiveRead
	@SnapshotRead
	public Collection<World> getWorlds();

	/**
	 * Loads a {@link World} with the given name and {@link WorldGenerator}<br/>
	 * If the world doesn't exist on disk, it creates it.<br/>
	 * <br/>
	 * if the world is already loaded, this functions the same as {@link #getWorld(String)}
	 * @param name Name of the world
	 * @param generator World Generator
	 * @return {@link World} loaded or created.
	 */
	@LiveRead
	public World loadWorld(String name, WorldGenerator generator);

	/**
	 * Unloads this world from memory. <br/>
	 * <br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 * @param name of the world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(String name, boolean save);

	/**
	 * Unloads this world from memory. <br/>
	 * <br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 * @param world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(World world, boolean save);

	/**
	 * Initiates a save of the server state, including configuration files. <br/>
	 * <br/>
	 * It will save the state of the world, if specificed, and the state of
	 * players, if specified.
	 * @param worlds true to save the state of all active worlds
	 * @param players true to save the state of all active players
	 */
	public void save(boolean worlds, boolean players);

	/**
	 * Registers the recipe with the recipe database. <br/>
	 *
	 * @param recipe to register
	 * @return true if the recipe was registered, false if there was a conflict
	 *         with an existing recipe.
	 */
	//public boolean registerRecipe(Recipe recipe);

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.<br/>
	 * <br/>
	 * Players will be sent a default disconnect message.
	 * @return true for for the first stop
	 */
	public boolean stop();

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.
	 * <br/>
	 * If any players are connected, will kick them with the given reason.
	 * @param reason for stopping the game instance
	 * @return true for for the first stop
	 */
	public boolean stop(String reason);

	/**
	 * Gets the world folders which match the world name.
	 * @param worldName to match the world folders with
	 * @return the world folders that match the world name
	 */
	public Collection<File> matchWorldFolder(String worldName);

	/**
	 * Gets all the individual world folders where world data is stored. <br/>
	 * <br/>
	 * This includes offline worlds.
	 * @return a list of available world folders
	 */
	public List<File> getWorldFolders();

	/**
	 * Gets the folder that contains the world save data. <br/>
	 * <br/>
	 * If the folder is unusued, the file path will be '.'
	 * @return world folder
	 */
	public File getWorldFolder();

	/**
	 * Returns the game's {@link EventManager} Event listener registration and
	 * calling is handled through this.
	 * @return Our EventManager instance
	 */
	public EventManager getEventManager();

	/**
	 * Returns the {@link Platform} that the game is currently running on.
	 * @return current platform type
	 */
	public Platform getPlatform();

	/**
	 * Gets the network channel group.
	 * @return The {@link ChannelGroup}.
	 */
	public ChannelGroup getChannelGroup();

	/**
	 * Gets the session registry.
	 * @return The {@link SessionRegistry}.
	 */
	public SessionRegistry getSessionRegistry();

	/**
	 * Gets the default world generator for this game. Specific generators can be specified when loading new worlds.
	 * @return default world generator.
	 */
	public WorldGenerator getDefaultGenerator();

	/**
	 * Gets the scheduler
	 * @return the scheduler
	 */
	public Scheduler getScheduler();

	/**
	 * Gets the task manager responsible for parallel region tasks.
	 * <br/>
	 * All tasks are submitted to all loaded regions at the start of the next tick.<br/>
	 * <br/>
	 * Repeating tasks are also submitted to all new regions when they are created.<br/>
	 * Repeated tasks are NOT guaranteed to happen in the same tick for all regions,
	 * as each task is submitted individually to each Region.<br/>
	 * <br/>
	 * This task manager does not support async tasks.
	 * <br/>
	 * If the Runnable for the task is a ParallelRunnable, then a new instance of the Runnable will be created for each region.
	 * @return the parallel {@link TaskManager} for the engine
	 */
	public TaskManager getParallelTaskManager();

	/**
	 * Returns the bootstrap protocol for {@code address}
	 * @param address The address
	 * @return The protocol
	 */
	public Protocol getProtocol(SocketAddress address);

	/**
	 * Gets the service manager
	 * @return ServiceManager
	 */
	public ServiceManager getServiceManager();

	/**
	 * Gets the recipe manager
	 * @return RecipeManager
	 */
	public RecipeManager getRecipeManager();

	/**
	 * Returns true if the game is running in debug mode <br/>
	 * <br/>
	 * To start debug mode, start Spout with -debug
	 * @return true if server is started with the -debug flag, false if not
	 */
	public boolean debugMode();

	/**
	 * Gets the main thread that is used to manage all execution on the server. <br/>
	 * <br/>
	 * Note: Interrupting the main thread will lead to undetermined behavior.
	 * @return main thread
	 */
	public Thread getMainThread();

	/**
	 * Sets the default world.
	 * <p/>
	 * The first loaded world will be set as the default world automatically.
	 * <p/>
	 * New players start in the default world.
	 * @param world the default world
	 * @return true on success
	 */
	@DelayedWrite
	public boolean setDefaultWorld(World world);

	/**
	 * Gets the default {@link World}.
	 * @return the default world
	 */
	@SnapshotRead
	public World getDefaultWorld();

	/**
	 * Gets the name of the server's log file
	 * @return the log filename
	 */
	public String getLogFile();

	/**
	 * Gets an abstract representation of the engine Filesystem.
	 * <p/>
	 * The Filesystem handles the loading of all resources.
	 * <p/>
	 * On the client, loading a resource will load the resource from the harddrive.
	 * On the server, it will notify all clients to load the resource, as well as provide a representation of that resource.
	 */
	public FileSystem getFilesystem();

	/**
	 * Gets the command source that prints to the console<br/>
	 * Can be used to print colored text to the console
	 * @return the console command source
	 */
	public CommandSource getCommandSource();

	/**
	 * Gets the default permissions handler for the engine
	 * @return The default permissions handler
	 */
	public DefaultPermissions getDefaultPermissions();

	/**
	 * Returns the engine's {@link CommandManager}.
	 *
	 * @return command manager
	 */
	public CommandManager getCommandManager();
}
