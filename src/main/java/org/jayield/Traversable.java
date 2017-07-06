/*
 * Copyright (c) 2017, jasync.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jayield;

/**
 * Traverse all elements sequentially in bulk
 * in the current thread, until all elements have
 * been processed or throws an exception.
 *
 * @author Miguel Gamboa
 *         created on 04-06-2017
 */
public interface Traversable<T> {
    void traverse(Yield<T> yield);
}
