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
package org.envirocar.app.main;

import com.squareup.sqlbrite.SqlBrite;

import org.envirocar.app.handler.DAOProvider;
import org.envirocar.app.handler.TrackRecordingHandler;
import org.envirocar.app.services.AutomaticTrackRecordingService;
import org.envirocar.app.services.GPSOnlyConnectionService;
import org.envirocar.app.services.OBDConnectionService;
import org.envirocar.app.services.TrackUploadService;
import org.envirocar.app.views.LoginRegisterActivity;
import org.envirocar.app.views.OthersFragment;
import org.envirocar.app.views.carselection.CarSelectionActivity;
import org.envirocar.app.views.carselection.CarSelectionAddCarFragment;
import org.envirocar.app.views.logbook.LogbookActivity;
import org.envirocar.app.views.logbook.LogbookAddFuelingFragment;
import org.envirocar.app.views.obdselection.OBDSelectionActivity;
import org.envirocar.app.views.obdselection.OBDSelectionFragment;
import org.envirocar.app.views.preferences.BluetoothDiscoveryIntervalPreference;
import org.envirocar.app.views.preferences.BluetoothPairingPreference;
import org.envirocar.app.views.preferences.SelectBluetoothPreference;
import org.envirocar.app.views.preferences.Tempomat;
import org.envirocar.app.views.preferences.TrackTrimDurationPreference;
import org.envirocar.app.views.settings.AutoConnectSettingsFragment;
import org.envirocar.app.views.settings.SettingsActivity;
import org.envirocar.app.views.trackdetails.TrackDetailsActivity;
import org.envirocar.app.views.trackdetails.TrackStatisticsActivity;
import org.envirocar.remote.dao.CacheAnnouncementsDAO;
import org.envirocar.remote.dao.CacheCarDAO;
import org.envirocar.remote.dao.CacheFuelingDAO;
import org.envirocar.remote.dao.CacheTermsOfUseDAO;
import org.envirocar.remote.dao.CacheTrackDAO;
import org.envirocar.remote.dao.CacheUserDAO;
import org.envirocar.remote.dao.RemoteAnnouncementsDAO;
import org.envirocar.remote.dao.RemoteCarDAO;
import org.envirocar.remote.dao.RemoteFuelingDAO;
import org.envirocar.remote.dao.RemoteTermsOfUseDAO;
import org.envirocar.remote.dao.RemoteTrackDAO;
import org.envirocar.remote.dao.RemoteUserDAO;
import org.envirocar.remote.dao.RemoteUserStatisticsDAO;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * @author Sai Krishna
 */
@Singleton
@Component(
        modules = BaseApplicationModule.class
)
public interface BaseApplicationComponent {

    void inject(BaseApplication baseApplication);
    void inject(TrackRecordingHandler trackRecordingHandler);
    void inject(OBDConnectionService obdConnectionService);
    void inject(AutomaticTrackRecordingService automaticTrackRecordingService);
    void inject(TrackUploadService trackUploadService);
    void inject(CarSelectionActivity carSelectionActivity);
    void inject(CarSelectionAddCarFragment carSelectionAddCarFragment);
    void inject(LogbookActivity logbookActivity);
    void inject(LogbookAddFuelingFragment logbookAddFuelingFragment);
    void inject(OBDSelectionActivity obdSelectionActivity);
    void inject(OBDSelectionFragment obdSelectionFragment);
    void inject(BluetoothDiscoveryIntervalPreference bluetoothDiscoveryIntervalPreference);
    void inject(BluetoothPairingPreference bluetoothPairingPreference);
    void inject(SelectBluetoothPreference selectBluetoothPreference);
    void inject(Tempomat tempomat);
    void inject(AutoConnectSettingsFragment autoConnectSettingsFragment);
    void inject(SettingsActivity settingsActivity);
    void inject(TrackDetailsActivity trackDetailsActivity);
    void inject(TrackStatisticsActivity trackStatisticsActivity);
    void inject(LoginRegisterActivity loginRegisterActivity);
    void inject(DAOProvider daoProvider);
    void inject(OthersFragment othersFragment);
    void inject(GPSOnlyConnectionService gpsOnlyConnectionService);
    void inject(TrackTrimDurationPreference trackTrimDurationPreference);

    MainActivityComponent plus(MainActivityModule mainActivityModule);


    CacheCarDAO getCacheCarDAO();
    RemoteCarDAO getRemoteCarDAO();
    RemoteTrackDAO getRemoteTrackDAO();
    CacheTrackDAO getCacheTrackDAO();
    RemoteUserDAO getRemoteUserDAO();
    CacheUserDAO getCacheUserDAO();
    RemoteUserStatisticsDAO getRemoteUserStatisticsDAO();
    RemoteFuelingDAO getRemoteFuelingDAO();
    CacheFuelingDAO getCacheFuelingDAO();
    RemoteTermsOfUseDAO getRemoteTermsOfUseDAO();
    CacheTermsOfUseDAO getCacheTermsOfUseDAO();
    RemoteAnnouncementsDAO getRemoteAnnouncementsDAO();
    CacheAnnouncementsDAO getCacheAnnouncementsDAO();
    Retrofit provideRetrofit();
    SqlBrite provideSqlBrite();


}