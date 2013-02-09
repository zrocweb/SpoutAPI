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
package permissions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.spout.api.event.Result;
import org.spout.api.permissions.PermissionDatabase;
import org.spout.api.permissions.PermissionState;
import org.spout.api.permissions.impl.WildcardNodePermissionResolver;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class WildcardNodePermissionResolverTest {
    @Test
    public void testResolveDirectNode() {
        PermissionDatabase testDb = new FixedPermissionDatabase("this.is.a.node");
        Result result = WildcardNodePermissionResolver.INSTANCE.resolve(testDb, "this.is.a.node");
        assertEquals(Result.ALLOW, result);
    }

    @Test
    public void testStarNodeResolution() {
        PermissionDatabase testDb = new FixedPermissionDatabase("*");
        Result result = WildcardNodePermissionResolver.INSTANCE.resolve(testDb, "this.is.a.node");
        assertEquals(Result.ALLOW, result);
    }

    @Test
    public void testResolveUndefined() {
        PermissionDatabase testDb = new FixedPermissionDatabase("this.is.another.node");

        Result result = WildcardNodePermissionResolver.INSTANCE.resolve(testDb, "this.is.a.node");
        assertEquals(Result.DEFAULT, result);
    }

    /**
     * Test for a wildcard node separated by multiple levels ({@code this.is.a.node} matched by {@code this.is.a.*))
     */
    @Test
    public void testSingleLevelWildcard() {
        PermissionDatabase testDb = new FixedPermissionDatabase("this.is.a.*");
        Result result = WildcardNodePermissionResolver.INSTANCE.resolve(testDb, "this.is.a.node");
        assertEquals(Result.ALLOW, result);
    }

    /**
     * Test for a wildcard node separated by multiple levels ({@code this.is.a.node} matched by {@code this.is.*))
     */
    @Test
    public void testMultiLevelWildcard() {
        PermissionDatabase testDb = new FixedPermissionDatabase("this.is.*");
        Result result = WildcardNodePermissionResolver.INSTANCE.resolve(testDb, "this.is.a.node");
        assertEquals(Result.ALLOW, result);
    }

    private static PermissionState toPermissionState(String node) {
        boolean allowed = !node.startsWith("-");
        return new PermissionState(allowed ? node : node.substring(1), allowed);
    }

    private static class FixedPermissionDatabase implements PermissionDatabase {
        private final Multimap<String, PermissionState> nodes;

        private FixedPermissionDatabase(List<PermissionState> states) {
            ImmutableMultimap.Builder<String, PermissionState> builder = ImmutableMultimap.builder();
            for (PermissionState state : states) {
                builder.put(state.getPermission(), state);
            }
            this.nodes = builder.build();
        }

        public FixedPermissionDatabase(String... nodes) {
            ImmutableMultimap.Builder<String, PermissionState> builder = ImmutableMultimap.builder();
            for (String node : nodes) {
                PermissionState state = toPermissionState(node);
                builder.put(state.getPermission(), state);
            }
            this.nodes = builder.build();
        }

        @Override
        public Multimap<String, PermissionState> getNodes() {
            return nodes;
        }

        @Override
        public PermissionState getValue(String node) {
            Collection<PermissionState> states = nodes.get(node);
            return states.size() == 0 ? null : states.iterator().next();
        }

        @Override
        public Iterator<PermissionState> nodeIterator() {
            return nodes.values().iterator();
        }
    }

}
