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

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.envirocar.app.main.BaseApplication;
import org.envirocar.app.main.BaseApplicationComponent;
import org.envirocar.app.main.MainActivityComponent;
import org.envirocar.app.main.MainActivityModule;
import org.envirocar.app.R;
import org.envirocar.app.injection.BaseInjectorFragment;
import org.envirocar.core.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * @author dewall
 */
public class TrackListPagerFragment extends BaseInjectorFragment {
    private static final Logger LOG = Logger.getLogger(TrackListPagerFragment.class);

    @BindView(R.id.trackListSegmentedGroup)
    protected SegmentedGroup trackListSegmentedGroup;
    @BindView(R.id.fragment_tracklist_layout_viewpager)
    protected ViewPager mViewPager;
    @BindView(R.id.filterButton)
    protected Button filterButton;
    @BindView(R.id.sortButton)
    protected Button sortButton;

    private TrackListPagerAdapter trackListPageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        LOG.info("onCreateView()");
        View content = inflater.inflate(R.layout.fragment_tracklist_layout, container, false);
        ButterKnife.bind(this, content);

        trackListPageAdapter = new TrackListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(trackListPageAdapter);
        trackListSegmentedGroup.check(R.id.localSegmentedButton);

        trackListSegmentedGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.localSegmentedButton:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.uploadedSegmentedButton:
                    mViewPager.setCurrentItem(1);
                    break;
                default:
                    break;
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog filterDialog = new FilterDialog(getContext(), getActivity());
                filterDialog.show();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortDialog sortDialog = new SortDialog(getContext(), getActivity());
                sortDialog.show();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                // Nothing to do..
            }

            @Override
            public void onPageSelected(int position) {
                LOG.info("Page selected=" + position);
                if (position == 0) {
                    trackListPageAdapter.localCardFragment.loadDataset();
                    trackListSegmentedGroup.check(R.id.localSegmentedButton);
                } else if (position == 1) {
                    trackListPageAdapter.remoteCardFragment.loadDataset();
                    trackListSegmentedGroup.check(R.id.uploadedSegmentedButton);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Nothing to do..
            }
        });

        return content;
    }

    @Override
    public void onResume() {
        LOG.info("onResume()");
        super.onResume();
        if (mViewPager.getCurrentItem() == 0) {
            trackListPageAdapter.localCardFragment.loadDataset();
        } else {
            trackListPageAdapter.remoteCardFragment.loadDataset();
        }
    }

    @Override
    public void onDestroyView() {
        LOG.info("onDestroyView()");
        super.onDestroyView();

        trackListPageAdapter.localCardFragment.onDestroyView();
        trackListPageAdapter.remoteCardFragment.onDestroyView();
    }

    @Override
    protected void injectDependencies(BaseApplicationComponent baseApplicationComponent) {
        MainActivityComponent mainActivityComponent =  BaseApplication.get(getActivity()).getBaseApplicationComponent().plus(new MainActivityModule(getActivity()));
        mainActivityComponent.inject(this);
    }


    /**
     * @author dewall
     */
    class TrackListPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_PAGES = 2;

        private TrackListLocalCardFragment localCardFragment;
        private TrackListRemoteCardFragment remoteCardFragment;

        /**
         * Constructor.
         *
         * @param fm the fragment manager of the application's current scope.
         */
        public TrackListPagerAdapter(FragmentManager fm) {
            super(fm);

            remoteCardFragment = new TrackListRemoteCardFragment();
            localCardFragment = new TrackListLocalCardFragment();
            localCardFragment.setOnTrackUploadedListener(remoteCardFragment);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return localCardFragment;
            } else {
                return remoteCardFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
}
