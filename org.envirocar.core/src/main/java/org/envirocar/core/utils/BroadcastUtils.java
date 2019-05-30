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
package org.envirocar.core.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.envirocar.core.entity.Track;
import org.envirocar.core.exception.DataRetrievalFailureException;
import org.envirocar.core.exception.NotConnectedException;
import org.envirocar.core.exception.UnauthorizedException;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import io.reactivex.functions.Action;
import rx.Subscriber;
//import rx.Subscription;
import rx.exceptions.OnErrorThrowable;
import rx.subscriptions.Subscriptions;
//import rx.functions.Action0;
//import rx.subscriptions.Subscriptions;

/**
 * TODO JavaDoc
 *
 * @author dewall
 */
public class BroadcastUtils {

    public static final Flowable<Intent> createBroadcastFlowable(
            final Context context, final IntentFilter intentFilter) {
        return Flowable.create(new FlowableOnSubscribe<Intent>() {
            @Override
            public void subscribe(FlowableEmitter<Intent> emitter) throws Exception {

                final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        emitter.onNext(intent);
                    }
                };
                //final Subscription subscription =
                final Subscription subscription = Flowable.create(new Action() {
                    @Override
                    public void run() {
                        context.unregisterReceiver(broadcastReceiver);
                    }
                });
                .add
                emitter.add(subscription);
                context.registerReceiver(broadcastReceiver, intentFilter, null, null);
            }
        }, BackpressureStrategy.BUFFER);
    }
}
