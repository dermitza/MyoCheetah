/**
 * This file is part of MyoCheetah. Copyright (C) 2014 K. Dermitzakis
 * <dermitza@gmail.com>
 *
 * MyoCheetah is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * MyoCheetah is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with MyoCheetah. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dermitza.myocheetah.canopen;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public interface PDOIF {
    
    public static final short COB_PDO1 = 0x180; // Position and speed
    public static final short COB_PDO2 = 0x280; // Current and (TODO) foot switch
    
}
