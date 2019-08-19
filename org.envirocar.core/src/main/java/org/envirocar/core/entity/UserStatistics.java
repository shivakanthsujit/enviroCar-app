/**
 * Copyright (C) 2013 - 2019 the enviroCar community
 *
 * This file is part of the enviroCar app.
 *
 * The enviroCar app is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The enviroCar app is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with the enviroCar app. If not, see http://www.gnu.org/licenses/.
 */
package org.envirocar.core.entity;

import java.util.Map;

/**
 * TODO JavaDoc
 *
 * @author dewall
 */
public interface UserStatistics extends BaseEntity<UserStatistics> {

    String KEY_USER_STAT_CONSUMPTION = "Consumption";
    String KEY_USER_STAT_SPEED = "Speed";

    Phenomenon getStatistic(String phenomenon);

    void setStatistic(Phenomenon phenomenon);

    Map<String, Phenomenon> getStatistics();

    void setStatistics(Map<String, Phenomenon> statistics);
}
