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
package org.envirocar.remote.dao;

import com.google.common.base.Preconditions;

import org.envirocar.core.UserManager;
import org.envirocar.core.dao.TrackDAO;
import org.envirocar.core.entity.Track;
import org.envirocar.core.exception.DataCreationFailureException;
import org.envirocar.core.exception.DataRetrievalFailureException;
import org.envirocar.core.exception.NotConnectedException;
import org.envirocar.core.exception.ResourceConflictException;
import org.envirocar.core.exception.UnauthorizedException;
import org.envirocar.core.logging.Logger;
import org.envirocar.remote.service.EnviroCarService;
import org.envirocar.remote.service.TrackService;
import org.envirocar.remote.util.EnvirocarServiceUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * TODO JavaDoc
 *
 * @author dewall
 */
@Singleton
public class RemoteTrackDAO extends BaseRemoteDAO<TrackDAO, TrackService> implements TrackDAO {
    private static final Logger LOG = Logger.getLogger(RemoteTrackDAO.class);

    /**
     * Constructor.
     *
     * @param cacheDao
     * @param service
     * @param userManager
     */
    @Inject
    public RemoteTrackDAO(CacheTrackDAO cacheDao, TrackService service, UserManager userManager) {
        super(cacheDao, service, userManager);
    }

    @Override
    public Track getTrackById(String id) throws DataRetrievalFailureException,
            NotConnectedException, UnauthorizedException {
        LOG.info(String.format("getTrack(%s)", id));
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<Track> trackCall = trackService.getTrack(userManager.getUser().getUsername(), id);

        try {
            // Execute the request call
            Response<Track> trackResponse = executeCall(trackCall);

            // If it was successful, then return the track.
            LOG.debug("getTrack() was successful");
            return trackResponse.body();
        } catch (ResourceConflictException e) {
            throw new DataRetrievalFailureException(e);
        } catch (IOException e) {
            throw new NotConnectedException(e);
        }
    }

    @Override
    public Observable<Track> getTrackByIdObservable(final String id) {
        return Observable.create(subscriber -> {
            try {
                Track remoteTracks = getTrackById(id);
                subscriber.onNext(remoteTracks);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Integer getUserTrackCount() throws DataRetrievalFailureException,
            NotConnectedException, UnauthorizedException {
        LOG.info("getUserTrackCount()");
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<ResponseBody> allTracksCountCall = trackService.getAllTracksCountOfUser(
                userManager.getUser().getUsername());

        try {
            // Execute the request call.
            Response<ResponseBody> allTracksCountResponse = allTracksCountCall.execute();

            // If the request call was not successful, then assert the status code and throw an
            // exceptiom
            if (!allTracksCountResponse.isSuccessful()) {
                EnvirocarServiceUtils.assertStatusCode(allTracksCountResponse.code(),
                        allTracksCountResponse.errorBody().toString());
                return null;
            }

            // Get the page count with a track limit of 1 per page (?limit=1). This corresponds
            // to the number of global tracks and return it.
            int pageCount = EnvirocarServiceUtils.resolvePageCount(allTracksCountResponse);
            LOG.info(String.format("getTotalTrackCount() with a tracksize of %s", "" + pageCount));
            return pageCount;
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (ResourceConflictException e) {
            throw new DataRetrievalFailureException(e);
        }
    }

    @Override
    public List<Track> getTrackinPeriod(Date after, Date before) throws NotConnectedException,
            UnauthorizedException {
        final TrackService trackService = EnviroCarService.getTrackService();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String afterD = formatter.format(after);
        String beforeD = formatter.format(before);
        String during = afterD +","+beforeD;
        LOG.info("calling getTracksinPeriod with following dates: after "+afterD+" before "+beforeD);
        Call<List<Track>> remoteTrackCall = trackService.getTracksInPeriod(userManager.getUser()
                .getUsername(), during);

        try {
            // Execute the call
            Response<List<Track>> remoteTracksResponse = remoteTrackCall.execute();

            if (!remoteTracksResponse.isSuccessful()) {
                LOG.severe("Error while retrieving the list of remote tracks");
                EnvirocarServiceUtils.assertStatusCode(remoteTracksResponse.code(),
                        remoteTracksResponse.message());
            }

            // Return the list of remotetracks.
            LOG.info("Successful Response of getTracksinPeriod: Tracks retrieved "+remoteTracksResponse.body().size());
            return remoteTracksResponse.body();
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (ResourceConflictException e) {
            throw new NotConnectedException(e);
        }
    }

    @Override
    public Observable<List<Track>> getTrackinPeriodObservable(final Date after, final Date before) {
        return Observable.create(
                new Observable.OnSubscribe<List<Track>>() {
                    @Override
                    public void call(Subscriber<? super List<Track>> subscriber) {
                        try {
                            List<Track> remoteTracks = getTrackinPeriod(after, before);
                            LOG.info("Observable created and "+remoteTracks.size()+" tracks present");
                            subscriber.onNext(remoteTracks);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            LOG.error("Error: ",e);
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

    @Override
    public Integer getTotalTrackCount() throws NotConnectedException,
            DataRetrievalFailureException {
        LOG.info("getTotalTrackCount()");
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<ResponseBody> allTracksCountCall = trackService.getAllTracksCount();

        try {
            // Execute the request call.
            Response<ResponseBody> allTracksCountResponse = allTracksCountCall.execute();

            // If the request call was not successful, then assert the status code and throw an
            // exceptiom
            if (!allTracksCountResponse.isSuccessful()) {
                EnvirocarServiceUtils.assertStatusCode(allTracksCountResponse.code(),
                        allTracksCountResponse.errorBody().toString());
                return null;
            }

            // Get the page count with a track limit of 1 per page (?limit=1). This corresponds
            // to the number of global tracks and return it.
            int pageCount = EnvirocarServiceUtils.resolvePageCount(allTracksCountResponse);
            LOG.info(String.format("getTotalTrackCount() with a tracksize of %s", pageCount));
            return pageCount;
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (Exception e) {
            throw new DataRetrievalFailureException(e);
        }
    }

    @Override
    public Track createTrack(Track track) throws DataCreationFailureException,
            NotConnectedException, UnauthorizedException {
        LOG.info("createTrack()");

        // check whether the user is logged in
        if (!userManager.isLoggedIn()) {
            throw new UnauthorizedException("The user is not logged in");
        }

        // Initiate the remoteService and its call
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<ResponseBody> uploadTrackCall =
                trackService.uploadTrack(userManager.getUser().getUsername(), track);

        try {
            Response<ResponseBody> uploadTrackResponse = uploadTrackCall.execute();

            if (!uploadTrackResponse.isSuccessful()) {
                LOG.severe("Error while uploading track: " + uploadTrackResponse.message());
                EnvirocarServiceUtils.assertStatusCode(uploadTrackResponse.code(),
                        uploadTrackResponse.message());
            }

            // Resolve the location where the track is stored.
            String location = EnvirocarServiceUtils.resolveRemoteLocation(uploadTrackResponse);
            LOG.info("Uploaded remote location: " + location);

            // Set the remoteID ...
            track.setRemoteID(location.substring(location.lastIndexOf('/') + 1, location.length()));
            // ... and return the track;
            return track;
        } catch (IOException e) {
            throw new DataCreationFailureException(e);
        } catch (ResourceConflictException e) {
            throw new NotConnectedException(e);
        }
    }

    @Override
    public Observable<Track> createTrackObservable(Track track) {
        return Observable.create(new Observable.OnSubscribe<Track>() {
            @Override
            public void call(Subscriber<? super Track> subscriber) {
                LOG.info("call: creating remote track.");
                subscriber.onStart();
                try {
                    subscriber.onNext(createTrack(track));
                } catch (DataCreationFailureException |
                        NotConnectedException |
                        UnauthorizedException e) {
                    LOG.error(e.getMessage(), e);
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public List<Track> getTrackIds() throws NotConnectedException, UnauthorizedException {
        return getTrackIds(100);
    }

    public List<Track> getTrackIds(int limit) throws NotConnectedException,
            UnauthorizedException {
        return getTrackIds(limit, 1);
    }

    @Override
    public List<Track> getTrackIds(int limit, int page) throws NotConnectedException,
            UnauthorizedException {
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<List<Track>> remoteTrackCall = trackService.getTrackIds(userManager.getUser()
                .getUsername());

        try {
            // Execute the call
            Response<List<Track>> remoteTracksResponse = remoteTrackCall.execute();

            if (!remoteTracksResponse.isSuccessful()) {
                LOG.severe("Error while retrieving the list of remote tracks");
                EnvirocarServiceUtils.assertStatusCode(remoteTracksResponse.code(),
                        remoteTracksResponse.message());
            }

            // Return the list of remotetracks.
            return remoteTracksResponse.body();
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (ResourceConflictException e) {
            throw new NotConnectedException(e);
        }
    }

    @Override
    public Observable<List<Track>> getTrackIdsObservable() {
        return Observable.create(new Observable.OnSubscribe<List<Track>>() {
            @Override
            public void call(Subscriber<? super List<Track>> subscriber) {
                try {
                    List<Track> remoteTrackIds = getTrackIds();
                    subscriber.onNext(remoteTrackIds);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<Track>> getTrackIdsObservable(final int limit, final int page) {
        return Observable.create(
                new Observable.OnSubscribe<List<Track>>() {
                    @Override
                    public void call(Subscriber<? super List<Track>> subscriber) {
                        try {
                            List<Track> remoteTracks = getTrackIds(limit, page);
                            subscriber.onNext(remoteTracks);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

    @Override
    public List<Track> getTrackIdsWithLimit(int limit) throws NotConnectedException,
            UnauthorizedException {
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<List<Track>> remoteTrackCall = trackService.getTrackIdsWithLimit(userManager.getUser()
                .getUsername(), limit);

        try {
            Response<List<Track>> remoteTracksResponse = remoteTrackCall.execute();

            if (!remoteTracksResponse.isSuccessful()) {
                LOG.severe("Error while retrieving the list of remote tracks with limit " + limit);
                EnvirocarServiceUtils.assertStatusCode(remoteTracksResponse.code(),
                        remoteTracksResponse.message());
            }

            return remoteTracksResponse.body();
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (ResourceConflictException e) {
            throw new NotConnectedException(e);
        }
    }

    @Override
    public Observable<List<Track>> getTrackIdsWithLimitObservable(final int limit) {
        return Observable.create(
                new Observable.OnSubscribe<List<Track>>() {
                    @Override
                    public void call(Subscriber<? super List<Track>> subscriber) {
                        try {
                            List<Track> remoteTracks = getTrackIdsWithLimit(limit);
                            subscriber.onNext(remoteTracks);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

    @Override
    public void deleteTrack(Track track) throws
            NotConnectedException, UnauthorizedException {
        Preconditions.checkState(track.getRemoteID() != null, "No RemoteID for this Track.");
        Preconditions.checkState(track.isRemoteTrack(), "Track is not a remote track. Track " +
                "cannot be deleted");
        String remoteID = track.getRemoteID();
        LOG.info(String.format("deleteRemoteTrack(%s)", remoteID));

        // If not logged in, then throw an exception
        if (!userManager.isLoggedIn()) {
            throw new UnauthorizedException("No User logged in.");
        }

        // Init the retrofit remoteService endpoint and the delete call
        final TrackService trackService = EnviroCarService.getTrackService();
        Call<ResponseBody> deleteTrackCall = trackService.deleteTrack(userManager.getUser()
                .getUsername(), remoteID);

        try {
            // Execute the call
            Response<ResponseBody> deleteTrackResponse = deleteTrackCall.execute();

            // Check whether the call was successful or not
            if (!deleteTrackResponse.isSuccessful()) {
                LOG.warn(String.format("deleteLocalTrack(): Error while deleting remote track."));
                EnvirocarServiceUtils.assertStatusCode(deleteTrackResponse.code(),
                        deleteTrackResponse.message());
            }
        } catch (IOException e) {
            throw new NotConnectedException(e);
        } catch (ResourceConflictException e) {
            throw new NotConnectedException(e);
        } catch (Exception e) {
            LOG.warn("WARNING!!!");
            throw e;
        }
    }

}
