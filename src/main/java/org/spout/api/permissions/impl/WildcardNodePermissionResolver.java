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

import org.spout.api.event.Result;
import org.spout.api.permissions.PermissionDatabase;
import org.spout.api.permissions.PermissionResolver;
import org.spout.api.permissions.PermissionState;

/**
 * Permission resolver that looks up nodes based on wildcards.
 */
public enum WildcardNodePermissionResolver implements PermissionResolver {
    INSTANCE {
        @Override
        public Result resolve(PermissionDatabase database, String node) {
            Result result;

            if ((result = doCheck(database, node)) != Result.DEFAULT) {
                return result;
            }

            //Checks all the parent nodes of this node
            //If this method is called with node equal
            //to this.is.a.perm.node, it will check the
            //nodes this.is.a.perm.*, this.is.a.*, this.is.*, this.*, and *
            String[] split = node.split("\\.");
            for (int i = split.length - 1; i >= 0; --i) {

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < i; j++) {
                    sb.append(split[j]);
                    sb.append(".");
                }
                sb.append("*");

                if ((result = doCheck(database, sb.toString())) != Result.DEFAULT) {
                    break;
                }
            }

            return result;
        }

        private Result doCheck(PermissionDatabase nodes, String node) {
            PermissionState value = nodes.getValue(node);
            if (value == null) {
                return Result.DEFAULT;
            }

            return value.getValue() ? Result.ALLOW : Result.DENY;
        }
    }
}
