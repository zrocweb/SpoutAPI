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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.event.Result;
import org.spout.api.geo.World;
import org.spout.api.permissions.PermissionContext;
import org.spout.api.permissions.PermissionResolver;
import org.spout.api.permissions.PermissionSubject;

import java.util.concurrent.ExecutionException;

/**
 * @author zml2008
 */
public class PermissionSubjectImpl extends AbstractPermissionContext implements PermissionSubject {
    private final String name;
    private final LoadingCache<String, Result> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Result>() {
                @Override
                public Result load(String permission) {
                    return getLivePermission(permission);
                }
            });

    public PermissionSubjectImpl(String name) {
        this.name = name;
    }

    // TODO implement these/decide what to do with options
    @Override
    public ValueHolder getData(String node) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ValueHolder getData(World world, String node) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasData(String node) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasData(World world, String node) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(String permission) {
        try {
            return cache.get(permission).getResult();
        } catch (ExecutionException e) {
            throw new RuntimeException(e); // This shouldn't occur, if it does why not send it screaming to the user
        }
    }

    @Override
    public boolean hasPermission(final String permission, final PermissionResolver resolver) {
        ResolvingVisitor predicate = new ResolvingVisitor(permission, resolver);
        ContextVisitors.BREADTH_FIRST.visit(this, predicate);

        return predicate.getResult().getResult();
    }

    private Result getLivePermission(final String permission) {
        ResolvingVisitor predicate = new ResolvingVisitor(permission, getDefaultResolver());
        ContextVisitors.BREADTH_FIRST.visit(this, predicate);
        return predicate.getResult();
    }

    static class ResolvingVisitor implements Predicate<PermissionContext> {
        private final String node;
        private final PermissionResolver resolver;
        private Result result;

        private ResolvingVisitor(String node, PermissionResolver resolver) {
            this.node = node;
            this.resolver = resolver;
        }

        @Override
        public boolean apply(PermissionContext permissionContext) {
            result = resolver.resolve(permissionContext.getPermissions(), node);
            return result != Result.DEFAULT;
        }

        public Result getResult() {
            return result;
        }
    }

    @Override
    public void setDefaultResolver(PermissionResolver resolver) {
        if (getDefaultResolver() != resolver) {
            cache.invalidateAll();
        }
        super.setDefaultResolver(resolver);
    }

    @Override
    public boolean isApplicable(CommandSource check) {
        return check == this;
    }

    @Override
    public String getName() {
        return name;
    }
}
