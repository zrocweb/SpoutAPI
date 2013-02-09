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

import com.google.common.base.Predicate;
import org.junit.Test;
import org.spout.api.command.CommandSource;
import org.spout.api.permissions.PermissionContext;
import org.spout.api.permissions.impl.AbstractPermissionContext;
import org.spout.api.permissions.impl.ContextVisitors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ContextVisitorTest {
    @Test
    public void testBreadthFirstLookup() {
        PermissionContext rootContext = new TestPermissionContext("one");

        PermissionContext leftContext = new TestPermissionContext("left");
        PermissionContext rightContext = new TestPermissionContext("right");
        rootContext.getParents().add(leftContext);
        rootContext.getParents().add(rightContext);

        PermissionContext leftParentContext = new TestPermissionContext("left-parent");
        leftContext.getParents().add(leftParentContext);

        PermissionContext rightParentContext = new TestPermissionContext("right-parent");
        rightContext.getParents().add(rightParentContext);
        final List<PermissionContext> expectedOrder = Arrays.asList(rootContext, leftContext, rightContext, leftParentContext, rightParentContext);
        final List<PermissionContext> visitedOrder = new ArrayList<PermissionContext>();
        ContextVisitors.BREADTH_FIRST.visit(rootContext, new Predicate<PermissionContext>() {
            @Override
            public boolean apply(@Nullable PermissionContext permissionContext) {
                visitedOrder.add(permissionContext);
                return false; // Continue visiting
            }
        });

        assertEquals(expectedOrder, visitedOrder);
    }

    @Test
    public void testDepthFirstLookup() {
        PermissionContext rootContext = new TestPermissionContext("one");

        PermissionContext leftContext = new TestPermissionContext("left");
        PermissionContext rightContext = new TestPermissionContext("right");
        rootContext.getParents().add(leftContext);
        rootContext.getParents().add(rightContext);

        PermissionContext leftParentContext = new TestPermissionContext("left-parent");
        leftContext.getParents().add(leftParentContext);

        PermissionContext rightParentContext = new TestPermissionContext("right-parent");
        rightContext.getParents().add(rightParentContext);
        final List<PermissionContext> expectedOrder = Arrays.asList(rootContext, leftContext, leftParentContext, rightContext, rightParentContext);
        final List<PermissionContext> visitedOrder = new ArrayList<PermissionContext>();
        ContextVisitors.DEPTH_FIRST.visit(rootContext, new Predicate<PermissionContext>() {
            @Override
            public boolean apply(@Nullable PermissionContext permissionContext) {
                visitedOrder.add(permissionContext);
                return false; // Continue visiting
            }
        });

        assertEquals(expectedOrder, visitedOrder);
    }

    private static class TestPermissionContext extends AbstractPermissionContext {
        private final String name;

        private TestPermissionContext(String name) {
            this.name = name;
        }

        @Override
        public boolean isApplicable(CommandSource check) {
            return true;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "TestPermissionContext{name=" + name + "}";
        }
    }
}
