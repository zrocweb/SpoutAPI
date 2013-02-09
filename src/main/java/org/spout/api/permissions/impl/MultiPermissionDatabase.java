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
package org.spout.api.permissions.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.spout.api.permissions.PermissionDatabase;
import org.spout.api.permissions.PermissionState;

import java.util.Iterator;

/**
 * A permission database that holds multiple permissions databases.
 */
public class MultiPermissionDatabase extends MultiDatabase<PermissionDatabase> implements PermissionDatabase {
    @Override
    public Multimap<String, PermissionState> getNodes() {
        Multimap<String, PermissionState> nodes = HashMultimap.create(getSourcesLive().size() * 50, 2);
        for (PermissionDatabase source : getSourcesLive()) {
            nodes.putAll(source.getNodes());
        }
        return nodes;
    }

    /**
     * Return the first value set for the passed (raw) node. This will lookup the node exactly as it appears in the parameter.
     * NO additional transformations apart from lowercasing should be applied
     *
     * @param node The node to check. Will be lowercased.
     * @return The first value found, or null if no value has been set.
     */
    @Override
    public PermissionState getValue(String node) {
        for (PermissionDatabase source : getSourcesLive()) {
            PermissionState value = source.getValue(node);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<PermissionState> nodeIterator() {
        return new NodeIterator(getSources().iterator());
    }

    private static class NodeIterator implements Iterator<PermissionState> {
        private final Iterator<PermissionDatabase> databases;
        private Iterator<PermissionState> activeIterator = null;

        private NodeIterator(Iterator<PermissionDatabase> databases) {
            this.databases = databases;
        }

        @Override
        public boolean hasNext() {
            if (activeIterator != null && activeIterator.hasNext()) {
                return true;
            } else if (databases.hasNext()) {
                return true;
            }
            return false;
        }

        @Override
        public PermissionState next() {
            if (activeIterator != null && activeIterator.hasNext()) {
                return activeIterator.next();
            } else {
                activeIterator = databases.next().nodeIterator();
                return activeIterator.next();
            }
        }

        @Override
        public void remove() {
            if (activeIterator != null) {
                activeIterator.remove();
            }
        }
    }
}
