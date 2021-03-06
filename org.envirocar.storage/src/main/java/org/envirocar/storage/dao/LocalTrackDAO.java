/**
 * Copyright (C) 2013 - 2015 the enviroCar community
 * <p>
 * This file is part of the enviroCar app.
 * <p>
 * The enviroCar app is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * The enviroCar app is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with the enviroCar app. If not, see http://www.gnu.org/licenses/.
 */
package org.envirocar.storage.dao;

import org.envirocar.core.dao.TrackDAO;
import org.envirocar.core.entity.Track;
import org.envirocar.core.exception.DataCreationFailureException;
import org.envirocar.core.exception.DataRetrievalFailureException;
import org.envirocar.core.exception.DataUpdateFailureException;
import org.envirocar.core.exception.NotConnectedException;
import org.envirocar.core.exception.ResourceConflictException;
import org.envirocar.core.exception.UnauthorizedException;
import org.envirocar.storage.EnviroCarDB;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * TODO JavaDoc
 *
 * @author dewall
 */
public class LocalTrackDAO implements TrackDAO {

    protected final EnviroCarDB database;

    /**
     * Injectable constructor.
     *
     * @param database the database instance to be injected.
     */
    @Inject
    public LocalTrackDAO(EnviroCarDB database) {
        this.database = database;
    }

    @Override
    public Track getTrackById(String id) throws DataRetrievalFailureException,
            NotConnectedException, UnauthorizedException {
        return database.getTrack(new Track.TrackId(Long.parseLong(id)))
                .take(1)
                .toBlocking()
                .first();
    }

    @Override
    public Observable<Track> getTrackByIdObservable(String id) {
        return database.getTrack(new Track.TrackId(Long.parseLong(id)))
                .take(1);
    }

    @Override
    public List<Track> getTrackIds() throws DataRetrievalFailureException, NotConnectedException,
            UnauthorizedException {
        return null;
    }

    @Override
    public List<Track> getTrackIds(int limit, int page) throws NotConnectedException,
            UnauthorizedException {
        return null;
    }

    @Override
    public Observable<List<Track>> getTrackIdsObservable() {
        return null;
    }

    @Override
    public Observable<List<Track>> getTrackIdsObservable(int limit, int page) {
        return null;
    }

    @Override
    public Integer getUserTrackCount() throws DataRetrievalFailureException,
            NotConnectedException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer getTotalTrackCount() throws DataRetrievalFailureException,
            NotConnectedException {
        return null;
    }

    @Override
    public Track createTrack(Track track) throws DataCreationFailureException,
            NotConnectedException, ResourceConflictException, UnauthorizedException {
        return null;
    }

    @Override
    public Observable<Track> createTrackObservable(Track track) {
        return database.insertTrackObservable(track);
    }

    @Override
    public void deleteTrack(Track track) throws DataUpdateFailureException,
            NotConnectedException, UnauthorizedException {
        database.deleteTrack(track);
    }
}
