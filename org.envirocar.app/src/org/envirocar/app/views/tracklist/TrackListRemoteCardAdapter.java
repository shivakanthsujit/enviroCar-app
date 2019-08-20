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
package org.envirocar.app.views.tracklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.maps.MapView;

import org.envirocar.app.R;
import org.envirocar.core.entity.Track;
import org.envirocar.core.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dewall
 */
public class TrackListRemoteCardAdapter extends AbstractTrackListCardAdapter<
        AbstractTrackListCardAdapter.RemoteTrackCardViewHolder> {
    private static final Logger LOG = Logger.getLogger(TrackListRemoteCardAdapter.class);

    /**
     * Constructor.
     *
     * @param tracks   the list of tracks to show cards for.
     * @param callback
     */
    public TrackListRemoteCardAdapter(Context context, List<Track> tracks,
                                      OnTrackInteractionCallback callback) {
        super(tracks, callback);
    }

    protected List<MapView> mapViews = new ArrayList<>();

    @Override
    public RemoteTrackCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the content view of the card.
        View remoteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tracklist_cardlayout, parent, false);

        // and create a new viewholder.
        RemoteTrackCardViewHolder temp = new RemoteTrackCardViewHolder(remoteView);
        mapViews.add(temp.mMapView);
        return temp;
    }

    @Override
    public void onBindViewHolder(RemoteTrackCardViewHolder holder, int position) {
        LOG.info("onBindViewHolder()");

        final Track remoteTrack = mTrackDataset.get(position);

        // Reset the most important settings of the views.
        holder.mTitleTextView.setText(remoteTrack.getName());
        holder.mDownloadButton.setOnClickListener(null);
        holder.mMapView.onCreate(null);
        holder.mMapView.removeOnDidFailLoadingMapListener(holder.failLoadingMapListener);
        holder.mToolbar.getMenu().clear();
        // Depending on the tracks state
        switch (remoteTrack.getDownloadState()) {
            case REMOTE:
                holder.mDownloadButton.show();
                holder.mDownloadButton.setOnClickListener(v -> {
                    holder.mDownloadButton.setOnClickListener(null);
                    holder.mDownloadButton.hide();
                    mTrackInteractionCallback.onDownloadTrackClicked(remoteTrack, holder);
                });
                holder.mDownloadNotification.setVisibility(View.GONE);
                bindTrackViewHolder(holder, remoteTrack,false);
                break;
            case DOWNLOADING:
                holder.mDownloadNotification.setVisibility(View.VISIBLE);
                break;
            case DOWNLOADED:
                holder.mDownloadButton.hide();
                holder.mDownloadNotification.setVisibility(View.GONE);
                bindTrackViewHolder(holder, remoteTrack, true);
                break;
        }
        //holder.mMapView.postInvalidate();
    }

    public void onLowMemory() {
        for (MapView mapView : mapViews) {
            mapView.onLowMemory();
        }
    }

    public void onDestroy() {
        for (MapView mapView : mapViews) {
            mapView.onPause();
            mapView.onStop();
            mapView.onDestroy();
        }
    }

}
