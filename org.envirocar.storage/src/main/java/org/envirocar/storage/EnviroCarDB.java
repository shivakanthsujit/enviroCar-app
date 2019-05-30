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
package org.envirocar.storage;

import org.envirocar.core.entity.Measurement;
import org.envirocar.core.entity.Track;
import org.envirocar.core.exception.MeasurementSerializationException;
import org.envirocar.core.exception.TrackSerializationException;
import org.envirocar.core.util.TrackMetadata;

import java.util.List;

import io.reactivex.Flowable;

/**
 * TODO JavaDoc
 *
 * @author dewall
 */
public interface EnviroCarDB {

    Flowable<Track> getTrack(Track.TrackId trackId);

    Flowable<Track> getTrack(Track.TrackId trackId, boolean lazy);

    /**
     * Returns an Flowable providing all tracks as an {@link List}.
     *
     * @return All tracks as Flowable
     */
    Flowable<List<Track>> getAllTracks();

    /**
     * Returns an Flowable providing all tracks as an {@link List}.
     *
     * @param lazy indicates whether the measurements should be loaded or not.
     * @return all tracks as Flowable.
     */
    Flowable<List<Track>> getAllTracks(boolean lazy);

    Flowable<List<Track>> getAllTracksByCar(String id, boolean lazy);

    Flowable<List<Track>> getAllLocalTracks();

    Flowable<List<Track>> getAllLocalTracks(boolean lazy);

    Flowable<List<Track>> getAllRemoteTracks();

    Flowable<List<Track>> getAllRemoteTracks(boolean lazy);

    Flowable<Void> clearTables();

    void insertTrack(Track track) throws TrackSerializationException;

    Flowable<Track> insertTrackFlowable(Track track);

    boolean updateTrack(Track track);

    Flowable<Track> updateTrackFlowable(Track track);

    boolean updateCarIdOfTracks(String currentId, String newId);

    void deleteTrack(Track.TrackId trackId);

    void deleteTrack(Track track);

    Flowable<Track> deleteTrackFlowable(Track track);

    Flowable<Void> deleteAllRemoteTracks();

    void insertMeasurement(Measurement measurement) throws MeasurementSerializationException;

    void automaticDeleteMeasurements(long time, Track.TrackId trackId);

    Flowable<Void> insertMeasurementFlowable(Measurement measurement);

    void updateTrackRemoteID(Track track, String remoteID);

    Flowable<Void> updateTrackRemoteIDFlowable(Track track, String remoteID);

    Flowable<Track> fetchTracks(Flowable<List<Track>> track, final boolean lazy);

    Flowable<Track> fetchTrack(Flowable<Track> track, final boolean lazy);

    Flowable<Track> getActiveTrackFlowable(boolean lazy);

    void updateTrackMetadata(final Track track, final TrackMetadata trackMetadata) throws
            TrackSerializationException;

    Flowable<TrackMetadata> updateTrackMetadataFlowable(final Track track, final TrackMetadata trackMetadata) throws
            TrackSerializationException;
}
