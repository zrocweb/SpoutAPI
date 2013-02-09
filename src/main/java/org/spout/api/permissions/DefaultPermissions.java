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
package org.spout.api.permissions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.spout.api.Engine;
import org.spout.api.command.CommandSource;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.permissions.impl.AbstractPermissionContext;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

/**
* Handle registering default permissions. Permissions registered here will be applied
* to permissions events if no other plugin has changed the values.
* Wildcards will be checked.
*/

public class DefaultPermissions extends ConfigurationHolderConfiguration implements Listener {
	private final ConfigurationHolder ENABLED = new ConfigurationHolder(true, "enabled");
	private final ConfigurationHolder DEFAULTS = new ConfigurationHolder(Collections.emptyList(), "defaults");

	private final Engine engine;
	private final YamlConfiguration config;
	private final Multimap<String, PermissionState> defaultPermissions = HashMultimap.create();
	private final Multimap<String, PermissionState> pluginDefaultPermissions = HashMultimap.create();
    private final DefaultPermissionContext contextInstance = new DefaultPermissionContext();

	public DefaultPermissions(Engine engine, File configFile) {
		super(null);
		this.engine = engine;

		config = new YamlConfiguration(configFile);
		config.setHeader("This is the configuration file for default server permissions.",
				"If enabled is set to false, by default nobody will have default permissions.",
				"Plugins can set their own default permissions, and server admins can",
				"set default permissions under the defaults section of this file.");
		reload();

		engine.getEventManager().registerEvents(this, this);
	}

	@Override
	public Configuration getConfiguration() {
		return config;
	}

    private class DefaultPermissionContext extends AbstractPermissionContext {

        public DefaultPermissionContext() {
            getPermissions().addLast(new DefaultPermissionDatabase(defaultPermissions));
            getPermissions().addLast(new DefaultPermissionDatabase(pluginDefaultPermissions));
        }

        @Override
        public boolean isApplicable(CommandSource check) {
            return ENABLED.getBoolean();
        }

        @Override
        public String getName() {
            return "Default Permissions";
        }
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getParents().add(contextInstance);
    }

	/**
	 * Reload the user-defined default permissions
	 */
	public void reload() {
		try {
			load();
			save();
		} catch (ConfigurationException e) {
			engine.getLogger().log(Level.SEVERE, "Error loading permissions configuration!", e);
		}
		defaultPermissions.clear();
		for (String node : DEFAULTS.getStringList()) {
            PermissionState value = toPermissionSetting(node);
            defaultPermissions.put(value.getPermission(), value);
        }
	}

    public PermissionState toPermissionSetting(String node) {
        if (node.startsWith("-")) {
            return new PermissionState(node.substring(1), false);
        } else {
            return new PermissionState(node, true);
        }
    }

	/**
	 * Adds a default permission to be applied to PermissionSubjects
	 *
	 * @param node The node to add
	 */
	public void addDefaultPermission(String node) {
		addDefaultPermission(node, true);
	}

    /**
     * Adds a default permission to be applied to PermissionSubjects
     *
     * @param node The node to add
     */
    public void addDefaultPermission(String node, boolean value) {
        addDefaultPermission(new PermissionState(node, value));
    }

    public void addDefaultPermission(PermissionState value) {
        pluginDefaultPermissions.put(value.getPermission(), value);
    }

	/**
	 * Get the current default permissions. The returned collection will be unmodifiable.
	 * If DefaultPermissions is not enabled, the returned set will be empty.
	 *
	 * @return The current default permissions
	 */
	public Set<PermissionState> getDefaultPermissions() {
		if (!ENABLED.getBoolean()) {
			return Collections.emptySet();
		}
        return ImmutableSet.copyOf(Iterables.concat(defaultPermissions.values(), pluginDefaultPermissions.values()));
	}

	/**
	 * Remove a permission from the set of default permissions.
	 * If the permission is not a default permission, nothing happens.
	 * This will remove default permissions from the user-defined list of permissions
	 * if they are not in the plugin-defined list.
	 *
	 * @param node The node to remove
	 */
	public void removeDefaultPermission(String node) {
		if (pluginDefaultPermissions.removeAll(node).size() == 0) {
			if (defaultPermissions.containsKey(node)) {
				defaultPermissions.removeAll(node);
				DEFAULTS.getList().remove(node);
				try {
					save();
				} catch (ConfigurationException ignore) {}
			}
		}
	}

    private static class DefaultPermissionDatabase implements PermissionDatabase {
        private final Multimap<String, PermissionState> data;

        private DefaultPermissionDatabase(Multimap<String, PermissionState> data) {
            this.data = data;
        }

        @Override
        public Multimap<String, PermissionState> getNodes() {
            return data;
        }

        @Override
        public PermissionState getValue(String node) {
            Collection<PermissionState> values = data.get(node);
            if (values.size() == 0) {
                return null;
            }
            return values.iterator().next();
        }

        @Override
        public Iterator<PermissionState> nodeIterator() {
            return data.values().iterator();
        }
    }
}
