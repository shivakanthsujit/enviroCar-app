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
package org.envirocar.app.views.carselection;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.envirocar.app.R;
import org.envirocar.core.entity.Car;
import org.envirocar.core.logging.Logger;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * @author dewall
 */
public class    CarSelectionAdapter extends RecyclerView.Adapter<CarSelectionAdapter.CarViewHolder> {
    private static final Logger LOG = Logger.getLogger(CarSelectionAdapter.class);

    private final String SELECTED_COLOR = "#0166A0";
    private final String UNSELECTED_COLOR = "#757575";

    /**
     * Simple callback interface for the action types of the car list entries.
     */
    public interface OnCarListActionCallback {
        /**
         * Called whenever a car has been selected to be the used car.
         *
         * @param car the selected car
         */
        void onSelectCar(Car car);

        /**
         * Called whenever a car should be deleted.
         *
         * @param car the selected car.
         */
        void onDeleteCar(Car car);
    }

    /**
     * Context of the current scope.
     */
    private final Context mContext;

    /**
     * Callback
     */
    private final OnCarListActionCallback mCallback;

    private Car mSelectedCar;
    private ImageView mSelectedImageView;
    private final List<Car> mCars;

    /**
     * Constructor.
     *
     * @param context     the context of the current scope.
     * @param selectedCar the car for which the radio button gets checked.
     * @param values      the values to show in the list.
     * @param callback    the callback for list actions
     */
    public CarSelectionAdapter(Context context, Car selectedCar, List<Car> values,
                               OnCarListActionCallback callback) {
        this.mContext = context;
        this.mCars = values;
        this.mCallback = callback;
        this.mSelectedCar = selectedCar;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_car_selection_layout_carlist_entry, parent, false);

        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        final Car car = mCars.get(position);
        // set the views
        holder.mFirstLineText.setText(String.format("%s - %s", car.getManufacturer(), car
                .getModel()));
        holder.mYearText.setText(Integer.toString(car.getConstructionYear()));
        holder.mGasolineText.setText(car.getFuelType().toString());
        holder.mEngineText.setText(String.format("%s ccm",
                Integer.toString(car.getEngineDisplacement())));
        holder.mIconView.setImageTintList(ColorStateList.valueOf(Color.parseColor(UNSELECTED_COLOR)));

        // If this car is the selected car, then set the radio button checked.
        if (mSelectedCar != null && mSelectedCar.equals(car)) {
            mSelectedImageView = holder.mIconView;
            mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(SELECTED_COLOR)));
        }

        final CarViewHolder tmpHolder = holder;
        // set the onClickListener of the row
        holder.carLayout.setOnClickListener(view -> {
            if(mSelectedCar != null && mSelectedCar.equals(car))
                return;

            if (mSelectedCar == null) {
                mSelectedCar = car;
                mSelectedImageView = holder.mIconView;
            }

            if (mSelectedImageView != null) {
                mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(UNSELECTED_COLOR)));
            }

            mSelectedCar = car;
            mSelectedImageView = holder.mIconView;
            mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(SELECTED_COLOR)));
            mCallback.onSelectCar(mSelectedCar);
        });

        // Set the onLongClickListener for the row row.
        holder.carLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onDeleteCar(car);
                /*new MaterialDialog.Builder(mContext)
                        .items(R.array.car_list_option_items)
                        .itemsCallback((materialDialog, view, i, charSequence) -> {
                            switch (i) {
                                case 0:
                                    if (car.equals(mSelectedCar))
                                        return;

                                    // Uncheck the currently checked car.
                                    if (mSelectedImageView != null) {
                                        mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(UNSELECTED_COLOR)));
                                    }

                                    // Set the new car as selected car type.
                                    mSelectedCar = car;
                                    mSelectedImageView = tmpHolder.mIconView;
                                    mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(SELECTED_COLOR)));

                                    // Call the callback in order to react accordingly.
                                    mCallback.onSelectCar(car);
                                    break;
                                case 1:
                                    // Uncheck the the previously checked radio button and update the
                                    // references accordingly.
                                    if (car.equals(mSelectedCar)) {
                                        mSelectedCar = null;
                                        mSelectedImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(UNSELECTED_COLOR)));
                                        mSelectedImageView = null;
                                    }

                                    // Call the callback
                                    mCallback.onDeleteCar(car);
                                    break;
                                default:
                                    LOG.warn("No action selected!");
                            }
                        })
                        .show();
                        */
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    /**
     * Adds a new {@link Car} to the list and finally invalidates the lsit.
     *
     * @param car the car to add to the list
     */
    protected void addCarItem(Car car) {
        this.mCars.add(car);
        notifyDataSetChanged();
    }

    /**
     * Removes a {@link Car} from the list and finally invalidates the list.
     *
     * @param car the car to remove from the list.
     */
    protected void removeCarItem(Car car) {
        if (mCars.contains(car)) {
            mCars.remove(car);
            notifyDataSetChanged();
        }
    }

    /**
     * Static view holder class that holds all necessary views of a list-row.
     */
    static class CarViewHolder extends RecyclerView.ViewHolder{

        protected final View mCoreView;

        @BindView(R.id.car_layout)
        protected ConstraintLayout carLayout;
        @BindView(R.id.activity_car_selection_layout_carlist_entry_icon)
        protected ImageView mIconView;
        @BindView(R.id.activity_car_selection_layout_carlist_entry_firstline)
        protected TextView mFirstLineText;

        @BindView(R.id.activity_car_selection_layout_carlist_entry_engine)
        protected TextView mEngineText;
        @BindView(R.id.activity_car_selection_layout_carlist_entry_gasoline)
        protected TextView mGasolineText;
        @BindView(R.id.activity_car_selection_layout_carlist_entry_year)
        protected TextView mYearText;

        /**
         * Constructor.
         *
         * @param view
         */
        CarViewHolder(View view) {
            super(view);
            this.mCoreView = view;
            ButterKnife.bind(this, view);
        }
    }
}