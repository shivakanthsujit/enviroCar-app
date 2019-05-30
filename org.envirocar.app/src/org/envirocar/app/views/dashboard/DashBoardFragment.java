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
package org.envirocar.app.views.dashboard;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Subscribe;

import org.envirocar.app.BuildConfig;
import org.envirocar.app.R;
import org.envirocar.app.handler.BluetoothHandler;
import org.envirocar.app.handler.CarPreferenceHandler;
import org.envirocar.app.handler.DAOProvider;
import org.envirocar.app.handler.LocationHandler;
import org.envirocar.app.handler.PreferencesHandler;
import org.envirocar.app.handler.TermsOfUseManager;
import org.envirocar.app.handler.TrackDAOHandler;
import org.envirocar.app.handler.UserHandler;
import org.envirocar.app.injection.BaseInjectorFragment;
import org.envirocar.app.main.BaseApplicationComponent;
import org.envirocar.app.main.MainActivityComponent;
import org.envirocar.app.main.MainActivityModule;
import org.envirocar.app.services.GPSOnlyConnectionService;
import org.envirocar.app.services.OBDConnectionService;
import org.envirocar.app.views.LoginRegisterActivity;
import org.envirocar.app.views.carselection.CarSelectionActivity;
import org.envirocar.app.views.obdselection.OBDSelectionActivity;
import org.envirocar.app.views.recordingscreen.GPSOnlyTrackRecordingScreen;
import org.envirocar.app.views.recordingscreen.OBDPlusGPSTrackRecordingScreen;
import org.envirocar.app.views.utils.DialogUtils;
import org.envirocar.core.dao.TrackDAO;
import org.envirocar.core.entity.Car;
import org.envirocar.core.events.NewCarTypeSelectedEvent;
import org.envirocar.core.events.bluetooth.BluetoothDeviceSelectedEvent;
import org.envirocar.core.events.bluetooth.BluetoothStateChangedEvent;
import org.envirocar.core.events.gps.GpsStateChangedEvent;
import org.envirocar.core.logging.Logger;
import org.envirocar.core.util.InjectionActivityScope;
import org.envirocar.obd.events.TrackRecordingServiceStateChangedEvent;
import org.envirocar.obd.service.BluetoothServiceState;
import org.reactivestreams.Subscriber;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

import static android.view.View.GONE;

public class DashBoardFragment extends BaseInjectorFragment {
    private static final Logger LOG = Logger.getLogger(DashBoardFragment.class);
    @Inject
    protected UserHandler mUserManager;
    @Inject
    protected DAOProvider mDAOProvider;
    @Inject
    protected TermsOfUseManager mTermsOfUseManager;
    @Inject
    protected TrackDAOHandler mTrackDAOHandler;
    @InjectionActivityScope
    @Inject
    protected Context context;


    @Inject
    protected CarPreferenceHandler mCarPrefHandler;
    @Inject
    protected BluetoothHandler mBluetoothHandler;
    @Inject
    protected LocationHandler mLocationHandler;
    @Inject
    protected CarPreferenceHandler mCarManager;

    @BindView(R.id.userStatisticsContainer)
    protected LinearLayout userStatisticsContainer;
    @BindView(R.id.userLoginSignupButtonContainer)
    protected LinearLayout userLoginSignupButtonContainer;
    @BindView(R.id.userLocalTrackCountTV)
    protected TextView userLocalTrackCountTV;
    @BindView(R.id.userUploadedTrackCountTV)
    protected TextView userUploadedTrackCountTV;
    @BindView(R.id.userGlobalTrackCountTV)
    protected TextView userGlobalTrackCountTV;
    @BindView(R.id.signInInitiatorButton)
    protected Button signInInitiatorButton;
    @BindView(R.id.registerInitiatorButton)
    protected Button registerInitiatorButton;
    @BindView(R.id.dashBoardUserImageView)
    protected ImageView dashBoardUserImageView;
    @BindView(R.id.dashBoardUserName)
    protected TextView dashBoardUserName;

    @BindView(R.id.errorImageBluetooth)
    protected ImageView errorImageBluetooth;
    @BindView(R.id.errorImageOBDAdapter)
    protected ImageView errorImageOBDAdapter;
    @BindView(R.id.errorImageGPS)
    protected ImageView errorImageGPS;
    @BindView(R.id.errorImageCar)
    protected ImageView errorImageCar;

    @BindView(R.id.okImageBluetooth)
    protected ImageView okImageBluetooth;
    @BindView(R.id.okImageOBDAdapter)
    protected ImageView okImageOBDAdapter;
    @BindView(R.id.okImageGPS)
    protected ImageView okImageGPS;
    @BindView(R.id.okImageCar)
    protected ImageView okImageCar;

    @BindView(R.id.bannerBluetoothContainer)
    protected LinearLayout bannerBluetoothContainer;
    @BindView(R.id.bannerOBDAdapterContainer)
    protected LinearLayout bannerOBDAdapterContainer;
    @BindView(R.id.bannerGPSContainer)
    protected LinearLayout bannerGPSContainer;
    @BindView(R.id.bannerCarContainer)
    protected LinearLayout bannerCarContainer;

    @BindView(R.id.disableChangingParametersLayout)
    protected LinearLayout disableChangingParametersLayout;

    @BindView(R.id.dash_board_view_car_selection)
    protected RelativeLayout mCarTypeView;
    @BindView(R.id.dash_board_view_car_selection_text1)
    protected TextView mCarTypeTextView;
    @BindView(R.id.dash_board_view_car_selection_text2)
    protected TextView mCarTypeSubTextView;

    @BindView(R.id.gpsOnlyCarSelectedLayout)
    protected RelativeLayout mGPSOnlyCarTypeView;
    @BindView(R.id.gpsOnlyCarSelectedHeader)
    protected TextView mGPSOnlyCarTypeTextView;
    @BindView(R.id.gpsOnlyCarSelectedSubHeader)
    protected TextView mGPSOnlyCarTypeSubTextView;

    @BindView(R.id.dashboard_view_obd_selection)
    protected RelativeLayout mOBDTypeView;
    @BindView(R.id.dash_board_view_obd_selection_text1)
    protected TextView mOBDTypeTextView;
    @BindView(R.id.dash_board_view_obd_selection_text2)
    protected TextView mOBDTypeSubTextView;
    @BindView(R.id.dashboardSegmentedGroup)
    protected SegmentedGroup dashboardSegmentedGroup;
    @BindView(R.id.obdPlusGPSSettingsContainer)
    protected CardView obdPlusGPSSettingsContainer;
    @BindView(R.id.GPSOnlySettingsContainer)
    protected CardView GPSOnlySettingsContainer;
    @BindView(R.id.fragment_startup_start_button)
    protected View mStartStopButton;
    @BindView(R.id.fragment_startup_start_button_inner)
    protected TextView mStartStopButtonInner;

    private MaterialDialog mConnectingDialog;

    private final Scheduler.Worker mBackgroundWorker = Schedulers
            .newThread().createWorker();
    private final Scheduler.Worker mMainThreadWorker = AndroidSchedulers
            .mainThread().createWorker();
    //trackType = 1 means OBD + GPS
    //trackType = 2 means GPS Only
    private static int trackType = 1;
    private int REQUEST_LOCATION_PERMISSION_REQUEST_CODE = 108;
    private int REQUEST_STORAGE_PERMISSION_REQUEST_CODE = 109;


    @Override
    protected void injectDependencies(BaseApplicationComponent baseApplicationComponent) {
        MainActivityComponent mainActivityComponent =  baseApplicationComponent.plus(new MainActivityModule(getActivity()));
        mainActivityComponent.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!PreferencesHandler.getEnableGPSBasedTrackRecording(context)){
            PreferencesHandler.setPreviouslySelectedRecordingType(context,1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // First inflate the general dashboard view.
        View contentView = inflater.inflate(R.layout.fragment_dashboard_view, container, false);

        ButterKnife.bind(this,contentView);
        userLocalTrackCountTV.setText(PreferencesHandler.getLocalTrackCount(getActivity()) + " ");
        userUploadedTrackCountTV.setText(PreferencesHandler.getUploadedTrackCount(getActivity()) + "");
        userGlobalTrackCountTV.setText(PreferencesHandler.getGlobalTrackCount(getActivity()) + "");

        mCarTypeView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CarSelectionActivity.class);
            getActivity().startActivity(intent);
        });
        mGPSOnlyCarTypeView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CarSelectionActivity.class);
            getActivity().startActivity(intent);
        });
        mOBDTypeView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OBDSelectionActivity.class);
            getActivity().startActivity(intent);
        });

        setCarTypeText(mCarPrefHandler.getCar());
        setOBDTypeText(mBluetoothHandler.getSelectedBluetoothDevice());

        dashboardSegmentedGroup.setOnCheckedChangeListener((radioGroup, i) -> {

            switch (i) {
                case R.id.obdPlusGPSSegmentedButton:
                    DashBoardFragment.this.showOBDPlusGPSSettings();
                    PreferencesHandler.setPreviouslySelectedRecordingType(context.getApplicationContext(), 1);
                    trackType = 1;
                    DashBoardFragment.this.updateStartStopButtonOBDPlusGPS(OBDConnectionService.CURRENT_SERVICE_STATE);
                    break;
                case R.id.GPSOnlySegmentedButton:
                    DashBoardFragment.this.showGPSOnlySettings();
                    PreferencesHandler.setPreviouslySelectedRecordingType(context.getApplicationContext(), 2);
                    trackType = 2;
                    DashBoardFragment.this.updateStartStopButtonGPSOnly(GPSOnlyConnectionService.CURRENT_SERVICE_STATE);
                    DashBoardFragment.this.updateBannerForGPSOnlyType();
                    break;
                default:
                    break;
            }
        });

        if(!PreferencesHandler.getEnableGPSBasedTrackRecording(context)){
            dashboardSegmentedGroup.check( R.id.obdPlusGPSSegmentedButton);
            dashboardSegmentedGroup.setVisibility(GONE);
        }

        if(!checkStoragePermissions())
        {
            requestStoragePermissions();
        }
        return contentView;
    }

    @OnClick(R.id.signInInitiatorButton)
    protected void onLoginInitiatorButtonClicked(){
        Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
        intent.putExtra("from","login");
        startActivity(intent);
    }

    @OnClick(R.id.bannerBluetoothContainer)
    protected void onbannerBluetoothContainerClicked(){
        if(errorImageBluetooth.getVisibility() == View.VISIBLE){
            DialogUtils.createDefaultDialogBuilder(getContext(),
                 R.string.banner_bluetooth_error_title,
                 R.drawable.ic_bluetooth_white_24dp,
                 R.string.banner_bluetooth_error_content)
                 .positiveText(R.string.banner_error_ok)
                 .show();
        }
    }

    @OnClick(R.id.bannerOBDAdapterContainer)
    protected void onbannerOBDAdapterContainerClicked(){
        if(errorImageOBDAdapter.getVisibility() == View.VISIBLE){
            DialogUtils.createDefaultDialogBuilder(getContext(),
                    R.string.banner_obd_adapter_error_title,
                    R.drawable.others_settings,
                    R.string.banner_obd_adapter_error_content)
                    .positiveText(R.string.banner_error_ok)
                    .show();
        }
    }

    @OnClick(R.id.bannerGPSContainer)
    protected void onbannerGPSContainerClicked(){
        if(errorImageGPS.getVisibility() == View.VISIBLE){
            DialogUtils.createDefaultDialogBuilder(getContext(),
                    R.string.banner_gps_error_title,
                    R.drawable.ic_location_on_white_24dp,
                    R.string.banner_gps_error_content)
                    .positiveText(R.string.banner_error_ok)
                    .show();
        }
    }

    @OnClick(R.id.bannerCarContainer)
    protected void onbannerCarContainerClicked(){
        if(errorImageCar.getVisibility() == View.VISIBLE){
            DialogUtils.createDefaultDialogBuilder(getContext(),
                    R.string.banner_car_error_title,
                    R.drawable.img_car,
                    R.string.banner_car_error_content)
                    .positiveText(R.string.banner_error_ok)
                    .show();
        }
    }

    @OnClick(R.id.disableChangingParametersLayout)
    protected void ondisableChangingParametersLayoutClicked(){
        Toast.makeText(context,"You cannot change these values while track recording is in progress",Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.registerInitiatorButton)
    protected void onRegisterInitiatorButtonClicked(){
        Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
        intent.putExtra("from","register");
        startActivity(intent);
    }

    @OnClick(R.id.fragment_startup_start_button)
    public void onStartStopButtonClicked() {
        switch (trackType) {
            case 1:
                if(OBDConnectionService.CURRENT_SERVICE_STATE == BluetoothServiceState.SERVICE_STARTED){
                    Intent intent = new Intent(getActivity(),OBDPlusGPSTrackRecordingScreen.class);
                    startActivity(intent);
                }else{
                    if(checkLocationPermission()){
                        onOBDPlusGPSStartTrackButtonStartClicked();
                    }else{
                        requestLocationPermission();
                    }
                }
                break;
            case 2:
                if(GPSOnlyConnectionService.CURRENT_SERVICE_STATE == BluetoothServiceState.SERVICE_STARTED){
                    Intent intent = new Intent(getActivity(),GPSOnlyTrackRecordingScreen.class);
                    startActivity(intent);
                }else{
                    if(checkLocationPermission()){
                        onGPSOnlyStartTrackButtonStartClicked();
                    }else{
                        requestLocationPermission();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSegmentedView();
        updateUserDetailsView();
        if(trackType == 1){
            updateStartStopButtonOBDPlusGPS(OBDConnectionService.CURRENT_SERVICE_STATE);
        }
        else if(trackType == 2){
            updateStartStopButtonGPSOnly(GPSOnlyConnectionService.CURRENT_SERVICE_STATE);
        }
    }

    private void updateSegmentedView(){
        RadioButton obdRadioButton = dashboardSegmentedGroup.findViewById(R.id.obdPlusGPSSegmentedButton);
        RadioButton gpsRadioButton = dashboardSegmentedGroup.findViewById(R.id.GPSOnlySegmentedButton);

        //index 1 means OBD + GPS recording type
        //index 2 means GPS only recording type
        if(PreferencesHandler.getPreviouslySelectedRecordingType(context.getApplicationContext()) == 1){
            obdRadioButton.setChecked(true);
            showOBDPlusGPSSettings();
            trackType = 1;
        }else{
            gpsRadioButton.setChecked(true);
            showGPSOnlySettings();
            trackType = 2;
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkLocationPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("Requesting Location", "Displaying permission rationale to provide additional context.");

            DialogUtils.createDefaultDialogBuilder(getContext(),
                    R.string.request_location_permission_title,
                    R.drawable.others_settings,
                    R.string.permission_rationale_location)
                    .positiveText(R.string.ok)
                    .onPositive((dialog, which) -> {
                        // Request permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_STORAGE_PERMISSION_REQUEST_CODE);
                    })
                    .show();

        } else {
            Log.i("Permissions", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkStoragePermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            LOG.debug("Requesting Storage. Displaying permission rationale to provide additional context.");

            DialogUtils.createDefaultDialogBuilder(getContext(),
                    R.string.request_storage_permission_title,
                    R.drawable.others_settings,
                    R.string.permission_rationale_file)
                    .positiveText(R.string.ok)
                    .onPositive((dialog, which) -> {
                        // Request permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_STORAGE_PERMISSION_REQUEST_CODE);
                    })
                    .show();

        } else {
            Log.i("Permissions", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LOG.debug("onRequestPermissionResult");
        if (requestCode == REQUEST_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                LOG.debug("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LOG.debug("Permission granted, updates requested, starting the recording procedure");
                onStartStopButtonClicked();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }else if(requestCode == REQUEST_STORAGE_PERMISSION_REQUEST_CODE){
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                LOG.debug("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LOG.debug("Permission granted, updates requested, starting the logging procedure");
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }


        /**
         * Shows a {@link Snackbar}.
         *
         * @param mainTextStringId The id for the string resource for the Snackbar text.
         * @param actionStringId   The text of the action item.
         * @param listener         The listener associated with the Snackbar action.
         */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                getActivity().findViewById(R.id.navigation),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    void showOBDPlusGPSSettings(){
        animateViewTransition(GPSOnlySettingsContainer,R.anim.translate_slide_out_right_card,true);
        animateViewTransition(obdPlusGPSSettingsContainer,R.anim.translate_slide_in_left_card,false);
    }

    void showGPSOnlySettings(){
        animateViewTransition(GPSOnlySettingsContainer,R.anim.translate_slide_in_right,false);
        animateViewTransition(obdPlusGPSSettingsContainer,R.anim.translate_slide_out_left_card,true);
    }

    void updateUserDetailsView(){
        if(mUserManager.isLoggedIn()){
            dashBoardUserName.setVisibility(View.VISIBLE);
            dashBoardUserImageView.setVisibility(View.VISIBLE);
            userStatisticsContainer.setVisibility(View.VISIBLE);
            userLoginSignupButtonContainer.setVisibility(GONE);

            dashBoardUserName.setText(mUserManager.getUser().getUsername());

            // update the local track count.
            mTrackDAOHandler.getLocalTrackCount()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {
                        userLocalTrackCountTV.setText("" + integer);
                        PreferencesHandler.setLocalTrackCount(context,integer);
                    });

            // Update the Gravatar image.
            mUserManager.getGravatarBitmapFlowable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        if (dashBoardUserImageView != null && dashBoardUserImageView.getVisibility() == View.VISIBLE && bitmap != null)
                            dashBoardUserImageView.setImageBitmap(bitmap);
                    });

            // Update the new values of the exp toolbar content.
            mBackgroundWorker.schedule(() -> {
                try {
                    final TrackDAO trackDAO = mDAOProvider.getTrackDAO();
                    final int totalTrackCount = trackDAO.getTotalTrackCount();
                    final int userTrackCount = trackDAO.getUserTrackCount();

                    String.format("%s (%s)", userTrackCount, totalTrackCount);
                    mMainThreadWorker.schedule(() -> {
                        userGlobalTrackCountTV.setText(Integer.toString(totalTrackCount));
                        userUploadedTrackCountTV.setText(Integer.toString(userTrackCount));
                        PreferencesHandler.setUploadedTrackCount(context, userTrackCount);
                        PreferencesHandler.setGlobalTrackCount(context, totalTrackCount);
                    });
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                }
            });

        }else{
            dashBoardUserName.setVisibility(GONE);
            dashBoardUserImageView.setVisibility(GONE);
            userStatisticsContainer.setVisibility(GONE);
            userLoginSignupButtonContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param device
     */
    private void setOBDTypeText(BluetoothDevice device) {
        getActivity().runOnUiThread(() -> {
            if (!mBluetoothHandler.isBluetoothEnabled()) {
                mOBDTypeTextView.setText(R.string.dashboard_bluetooth_disabled);
                mOBDTypeSubTextView.setText(R.string.dashboard_bluetooth_disabled_advise);
                mOBDTypeSubTextView.setVisibility(View.VISIBLE);
            } else if (device == null) {
                mOBDTypeTextView.setText(R.string.dashboard_obd_not_selected);
                mOBDTypeSubTextView.setText(R.string.dashboard_obd_not_selected_advise);
                mOBDTypeSubTextView.setVisibility(View.VISIBLE);
            } else {
                mOBDTypeTextView.setText(device.getName());
                mOBDTypeSubTextView.setText(device.getAddress());
                mOBDTypeSubTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * @param car
     */
    private void setCarTypeText(Car car) {
        if (car != null) {
            mCarTypeTextView.setText(String.format("%s - %s",
                    car.getManufacturer(),
                    car.getModel()));

            mGPSOnlyCarTypeTextView.setText(String.format("%s - %s",
                    car.getManufacturer(),
                    car.getModel()));

            mCarTypeSubTextView.setText(String.format("%s    %s    %s ccm",
                    car.getConstructionYear(),
                    car.getFuelType(),
                    car.getEngineDisplacement()));

            mGPSOnlyCarTypeSubTextView.setText(String.format("%s    %s    %s ccm",
                    car.getConstructionYear(),
                    car.getFuelType(),
                    car.getEngineDisplacement()));

            mCarTypeSubTextView.setVisibility(View.VISIBLE);
            mGPSOnlyCarTypeSubTextView.setVisibility(View.VISIBLE);
        } else {
            mCarTypeTextView.setText(R.string.dashboard_carselection_no_car_selected);
            mCarTypeSubTextView.setText(R.string.dashboard_carselection_no_car_selected_advise);
            mCarTypeSubTextView.setVisibility(View.VISIBLE);

            mGPSOnlyCarTypeTextView.setText(R.string.dashboard_carselection_no_car_selected);
            mGPSOnlyCarTypeSubTextView.setText(R.string.dashboard_carselection_no_car_selected_advise);
            mGPSOnlyCarTypeSubTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Applies an animation on the given view.
     *
     * @param view         the view to apply the animation on.
     * @param animResource the animation resource.
     * @param hide         should the view be hid?
     */
    private void animateViewTransition(final View view, int animResource, boolean hide) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), animResource);
        if (hide) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // nothing to do..
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // nothing to do..
                }
            });
            view.startAnimation(animation);
        } else {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
        }
    }

    /**
     * Receiver method for {@link TrackRecordingServiceStateChangedEvent}s posted on the event bus.
     *
     * @param event the corresponding event type.
     */
    @Subscribe
    public void onReceiveTrackRecordingServiceStateChangedEvent(
            TrackRecordingServiceStateChangedEvent event) {
        LOG.info(String.format("onReceiveTrackRecordingServiceStateChangedEvent(): %s",
                event.toString()));
        if ( event.mState == BluetoothServiceState.SERVICE_STARTED && mConnectingDialog != null) {
            mConnectingDialog.dismiss();
            mConnectingDialog = null;
        }

        mMainThreadWorker.schedule(() -> {
            // Update the start stop button.
            if(trackType == 1){
                if(event.mState == BluetoothServiceState.SERVICE_STARTED){
                    getActivity().startActivity(new Intent(getActivity(), OBDPlusGPSTrackRecordingScreen.class));
                }
                updateStartStopButtonOBDPlusGPS(event.mState);
            }else if(trackType == 2){
                updateStartStopButtonGPSOnly(event.mState);
                if(event.mState == BluetoothServiceState.SERVICE_STARTED){
                    getActivity().startActivity(new Intent(getActivity(), GPSOnlyTrackRecordingScreen.class));
                }
            }
        });
    }

    @Subscribe
    public void onReceiveBluetoothDeviceSelectedEvent(BluetoothDeviceSelectedEvent event) {
        LOG.debug(String.format("Received event: %s", event.toString()));
        setOBDTypeText(event.mDevice);
    }

    @Subscribe
    public void onReceiveBluetoothStateChangedEvent(BluetoothStateChangedEvent event) {
        LOG.info(String.format("onReceiveBluetoothStateChangedEvent(isEnabled=%s)",
                "" + event.isBluetoothEnabled));
        mMainThreadWorker.schedule(() -> {
            if(trackType == 1){
                updateStartStopButtonOBDPlusGPS(OBDConnectionService.CURRENT_SERVICE_STATE);
            }else if(trackType == 2){
                updateStartStopButtonGPSOnly(GPSOnlyConnectionService.CURRENT_SERVICE_STATE);
            }

            setOBDTypeText(mBluetoothHandler.getSelectedBluetoothDevice());
        });
    }

    @Subscribe
    public void onReceiveNewCarTypeSelectedEvent(NewCarTypeSelectedEvent event) {
        LOG.debug(String.format("Received event: %s", event.toString()));
        mMainThreadWorker.schedule(() -> {
            if(trackType == 1){
                updateStartStopButtonOBDPlusGPS(OBDConnectionService.CURRENT_SERVICE_STATE);
            }else if(trackType == 2){
                updateStartStopButtonGPSOnly(GPSOnlyConnectionService.CURRENT_SERVICE_STATE);
            }

            setCarTypeText(event.mCar);
        });
    }

    @Subscribe
    public void onReceiveGpsStatusChangedEvent(GpsStateChangedEvent event) {
        mMainThreadWorker.schedule(() -> {
            if(trackType == 1){
                updateStartStopButtonOBDPlusGPS(OBDConnectionService.CURRENT_SERVICE_STATE);
            }else if(trackType == 2){
                updateStartStopButtonGPSOnly(GPSOnlyConnectionService.CURRENT_SERVICE_STATE);
            }
        });
    }

    private void updateBannerForGPSOnlyType(){
        errorImageBluetooth.setVisibility(GONE);
        errorImageOBDAdapter.setVisibility(GONE);
        okImageBluetooth.setVisibility(GONE);
        okImageOBDAdapter.setVisibility(GONE);

        bannerBluetoothContainer.setAlpha(0.5f);
        bannerOBDAdapterContainer.setAlpha(0.5f);

        if(!mLocationHandler.isGPSEnabled()){
            errorImageGPS.setVisibility(View.VISIBLE);
            okImageGPS.setVisibility(GONE);
        }
        else{
            errorImageGPS.setVisibility(GONE);
            okImageGPS.setVisibility(View.VISIBLE);
        }
        if(mCarManager.getCar() == null){
            errorImageCar.setVisibility(View.VISIBLE);
            okImageCar.setVisibility(GONE);
        }
        else{
            errorImageCar.setVisibility(GONE);
            okImageCar.setVisibility(View.VISIBLE);
        }
    }

    private void updateBannerForOBDPlusGPSType(){
        bannerBluetoothContainer.setAlpha(1f);
        bannerOBDAdapterContainer.setAlpha(1f);
        if(!mBluetoothHandler.isBluetoothEnabled()){
            errorImageBluetooth.setVisibility(View.VISIBLE);
            okImageBluetooth.setVisibility(GONE);
        }
        else{
            errorImageBluetooth.setVisibility(GONE);
            okImageBluetooth.setVisibility(View.VISIBLE);
        }
        if( mBluetoothHandler.getSelectedBluetoothDevice() == null){
            errorImageOBDAdapter.setVisibility(View.VISIBLE);
            okImageOBDAdapter.setVisibility(GONE);
        }
        else{
            errorImageOBDAdapter.setVisibility(GONE);
            okImageOBDAdapter.setVisibility(View.VISIBLE);
        }
        if(!mLocationHandler.isGPSEnabled()){
            errorImageGPS.setVisibility(View.VISIBLE);
            okImageGPS.setVisibility(GONE);
        }
        else{
            errorImageGPS.setVisibility(GONE);
            okImageGPS.setVisibility(View.VISIBLE);
        }
        if(mCarManager.getCar() == null){
            errorImageCar.setVisibility(View.VISIBLE);
            okImageCar.setVisibility(GONE);
        }
        else {
            errorImageCar.setVisibility(GONE);
            okImageCar.setVisibility(View.VISIBLE);
        }
    }

    private void updateStartStopButtonOBDPlusGPS(BluetoothServiceState state) {
        //First update the banner
        updateBannerForOBDPlusGPSType();

        switch (state) {
            case SERVICE_STOPPED:
                disableChangingParametersLayout.setVisibility(GONE);
                if (hasSettingsSelectedFOROBD()) {
                    updateStartStopButton(getResources().getColor(R.color.green_dark_cario),
                            getString(R.string.dashboard_start_track), true);
                } else {
                    updateStartStopButton(Color.GRAY,
                            getString(R.string.dashboard_start_track), false);
                }
                break;
            case SERVICE_STARTED:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                // Update the StartStopButton
                updateStartStopButton(getResources().getColor(R.color.cario_color_primary), getString(R.string.dashboard_goto_track), true);
                // hide the info field when the track is started.
                //mInfoField.setVisibility(View.INVISIBLE);
                break;
            case SERVICE_STARTING:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                updateStartStopButton(Color.GRAY,
                    getString(R.string.dashboard_track_is_starting), false);
                break;
            case SERVICE_STOPPING:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                updateStartStopButton(Color.GRAY,
                    getString(R.string.dashboard_track_is_stopping), false);
            break;
            default:
                break;
        }
    }

    private void updateStartStopButtonGPSOnly(BluetoothServiceState state) {

        //First update the banner
        updateBannerForGPSOnlyType();

        switch (state) {
            case SERVICE_STOPPED:
                disableChangingParametersLayout.setVisibility(GONE);
                if (hasSettingsSelectedFORGPSOnly()) {
                    updateStartStopButton(getResources().getColor(R.color.green_dark_cario),
                            getString(R.string.dashboard_start_track), true);
                } else {
                    updateStartStopButton(Color.GRAY,
                            getString(R.string.dashboard_start_track), false);
                }
                break;
            case SERVICE_STARTED:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                // Update the StartStopButton
                updateStartStopButton(getResources().getColor(R.color.cario_color_primary), getString(R.string.dashboard_goto_track), true);
                // hide the info field when the track is started.
                //mInfoField.setVisibility(View.INVISIBLE);
                break;
            case SERVICE_STARTING:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                updateStartStopButton(Color.GRAY,
                        getString(R.string.dashboard_track_is_starting), false);
                break;
            case SERVICE_STOPPING:
                disableChangingParametersLayout.setVisibility(View.VISIBLE);
                updateStartStopButton(Color.GRAY,
                        getString(R.string.dashboard_track_is_stopping), false);
                break;
            default:
                break;
        }
    }

    private boolean hasSettingsSelectedFOROBD() {
        return mBluetoothHandler.isBluetoothEnabled() &&
                mBluetoothHandler.getSelectedBluetoothDevice() != null &&
                mLocationHandler.isGPSEnabled() &&
                mCarManager.getCar() != null;
    }

    private boolean hasSettingsSelectedFORGPSOnly() {
        return  mLocationHandler.isGPSEnabled() &&
                mCarManager.getCar() != null;
    }

    private void updateStartStopButton(int color, String text, boolean enabled) {
        mMainThreadWorker.schedule(() -> {
            mStartStopButtonInner.setBackgroundColor(color);
            mStartStopButtonInner.setText(text);
            mStartStopButton.setEnabled(enabled);
        });
    }

    private void onOBDPlusGPSStartTrackButtonStartClicked() {
        if (!mBluetoothHandler.isBluetoothEnabled()) {
            Snackbar.make(getView(), R.string.dashboard_bluetooth_disabled_snackbar,
                    Snackbar.LENGTH_LONG);
            return;
        }

        final BluetoothDevice device = mBluetoothHandler.getSelectedBluetoothDevice();

        mBluetoothHandler.startBluetoothDiscoveryForSingleDevice(device)
                .subscribe(new ResourceSubscriber<BluetoothDevice>() {
                    private boolean found = false;
                    private View contentView;
                    private TextView textView;

                    @Override
                    public void onStart() {
                        contentView = getActivity().getLayoutInflater().inflate(
                                R.layout.fragment_dashboard_connecting_dialog, null, false);
                        textView = contentView.findViewById(
                                R.id.fragment_dashboard_connecting_dialog_text);
                        textView.setText(String.format(
                                getString(R.string.dashboard_connecting_find_template),
                                device.getName()));

                        mConnectingDialog = DialogUtils.createDefaultDialogBuilder(getContext(),
                                R.string.dashboard_connecting,
                                R.drawable.ic_bluetooth_searching_white_24dp,
                                contentView)
                                .cancelable(false)
                                .negativeText(R.string.cancel)
                                .onNegative((materialDialog, dialogAction) -> {
                                    // On cancel, first stop the discovery of other
                                    // bluetooth devices.
                                    mBluetoothHandler.stopBluetoothDeviceDiscovery();
                                    if (found) {
                                        // and if the remoteService is already started, then
                                        // stop it.
                                        getActivity().stopService(new Intent
                                                (getActivity(), OBDConnectionService
                                                        .class));
                                    }
                                    found = true;
                                })
                                .show();
                    }

                    @Override
                    public void onComplete() {
                        if (!found) {
                            mConnectingDialog.dismiss();
                            mConnectingDialog = DialogUtils.createDefaultDialogBuilder(getContext(),
                                    R.string.dashboard_dialog_obd_not_found,
                                    R.drawable.ic_bluetooth_searching_white_24dp,
                                    String.format(getString(
                                            R.string.dashboard_dialog_obd_not_found_content_template),
                                            device.getName()))
                                    .negativeText(R.string.ok)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mConnectingDialog.setActionButton(DialogAction.NEGATIVE, "Okay");
                    }

                    @Override
                    public void onNext(BluetoothDevice bluetoothDevice) {
                        found = true;

                        // Stop the Bluetooth discovery such that the connection can be
                        // faster established.
                        mBluetoothHandler.stopBluetoothDeviceDiscovery();

                        // Update the content of the connecting dialog.
                        textView.setText(String.format(getString(
                                R.string.dashboard_connecting_found_template), device.getName()));

                        // Start the background remoteService.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(
                                    new Intent(getActivity(), OBDConnectionService.class));
                        }else{
                            getActivity().startService(
                                    new Intent(getActivity(), OBDConnectionService.class));
                        }
                    }
                });
    }

    private void onGPSOnlyStartTrackButtonStartClicked(){
        // Start the background remoteService.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(
                    new Intent(getActivity(), GPSOnlyConnectionService.class));
        }else{
            getActivity().startService(
                    new Intent(getActivity(), GPSOnlyConnectionService.class));
        }
    }

}
