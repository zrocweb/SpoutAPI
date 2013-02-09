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

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent data structure for our database implementations that group several database together.
 */
public class MultiDatabase<T> {
    private final List<T> sources = new ArrayList<T>();

    public void addFirst(T source) {
        sources.add(0, source);
    }

    public void addLast(T source) {
        sources.add(sources.size() - 1, source);
    }

    public void addBefore(T existing, T toAdd) {
        int index = sources.indexOf(existing);
        sources.add(index == -1 ? 0 : index, toAdd);
    }

    public void addAfter(T existing, T toAdd) {
        int index = sources.indexOf(existing);
        sources.add(index == -1 ? sources.size() - 1 : index, toAdd);
    }

    public List<T> getSources() {
        return ImmutableList.copyOf(sources);
    }

    protected List<T> getSourcesLive() {
        return sources;
    }

}
