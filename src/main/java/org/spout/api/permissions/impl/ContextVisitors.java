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

import com.google.common.base.Predicate;
import org.spout.api.permissions.ContextVisitor;
import org.spout.api.permissions.PermissionContext;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
* Common implementations of {@link ContextVisitor}
*/
public enum ContextVisitors implements ContextVisitor {
    BREADTH_FIRST {
        @Override
        public boolean visit(PermissionContext start, Predicate<PermissionContext> predicate) {
            final Queue<PermissionContext> toVisit = new ArrayDeque<PermissionContext>(start.getParents().size() * 2);
            final Set<PermissionContext> visited = new HashSet<PermissionContext>();
            toVisit.add(start);
            visited.add(start);

            for (PermissionContext context = toVisit.poll(); context != null; context = toVisit.poll()) {
                if (predicate.apply(context)) {
                    return true;
                }

                for (int i = 0; i < context.getParents().size(); ++i) {
                    final PermissionContext parent = context.getParents().get(i);
                    if (!visited.contains(parent)) {
                        visited.add(parent);
                        toVisit.add(parent);
                    }
                }
            }
            return false;
        }
    },
    DEPTH_FIRST {
        @Override
        public boolean visit(PermissionContext start, Predicate<PermissionContext> predicate) {
            final Deque<PermissionContext> toVisit = new LinkedList<PermissionContext>();
            final Set<PermissionContext> visited = new HashSet<PermissionContext>();
            toVisit.addFirst(start);
            visited.add(start);

            for (PermissionContext context = toVisit.pollFirst(); context != null; context = toVisit.pollFirst()) {
                if (predicate.apply(context)) {
                    return true;
                }

                for (int i = context.getParents().size() - 1; i >= 0; --i) {
                    if (!visited.contains(context.getParents().get(i))) {
                        toVisit.push(context.getParents().get(i));
                        visited.add(context.getParents().get(i));
                    }
                }
            }
            return false;
        }
    };

    public abstract boolean visit(PermissionContext start, Predicate<PermissionContext> predicate);
}
