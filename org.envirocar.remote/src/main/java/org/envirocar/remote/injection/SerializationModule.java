package org.envirocar.remote.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.envirocar.core.entity.Announcement;
import org.envirocar.core.entity.Car;
import org.envirocar.core.entity.Fueling;
import org.envirocar.core.entity.Measurement;
import org.envirocar.core.entity.TermsOfUse;
import org.envirocar.core.entity.Track;
import org.envirocar.core.entity.User;
import org.envirocar.core.entity.UserStatistics;
import org.envirocar.remote.serializer.AnnouncementSerializer;
import org.envirocar.remote.serializer.CarListDeserializer;
import org.envirocar.remote.serializer.CarSerializer;
import org.envirocar.remote.serializer.FuelingListSerializer;
import org.envirocar.remote.serializer.FuelingSerializer;
import org.envirocar.remote.serializer.MeasurementSerializer;
import org.envirocar.remote.serializer.RemoteTrackListDeserializer;
import org.envirocar.remote.serializer.TermsOfUseListSerializer;
import org.envirocar.remote.serializer.TermsOfUseSerializer;
import org.envirocar.remote.serializer.TrackSerializer;
import org.envirocar.remote.serializer.UserSerializer;
import org.envirocar.remote.serializer.UserStatisticDeserializer;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Contains the GSON Serialization/Deserialization instances.
 *
 * @author dewall
 */
@Module
public class SerializationModule {

    @Provides
    @Singleton
    protected UserSerializer provideUserSerializer() {
        return new UserSerializer();
    }

    @Provides
    @Singleton
    protected UserStatisticDeserializer provideUserStatisticDeserializer() {
        return new UserStatisticDeserializer();
    }

    @Provides
    @Singleton
    protected TypeToken<List<Car>> provideCarListTypeToken() {
        return new TypeToken<List<Car>>() {
        };
    }

    @Provides
    @Singleton
    protected CarListDeserializer provideCarListDeserializer() {
        return new CarListDeserializer();
    }

    @Provides
    @Singleton
    protected TrackSerializer provideTrackSerializer() {
        return new TrackSerializer();
    }

    @Provides
    @Singleton
    protected MeasurementSerializer provideMeasurementSerializer() {
        return new MeasurementSerializer();
    }

    @Provides
    @Singleton
    protected TypeToken<List<Track>> provideTrackListTypeToken() {
        return new TypeToken<List<Track>>() {
        };
    }

    @Provides
    @Singleton
    protected RemoteTrackListDeserializer provideRemoteTrackListDeserializer() {
        return new RemoteTrackListDeserializer();
    }

    @Provides
    @Singleton
    protected TermsOfUseSerializer provideTermsOfUseSerializer() {
        return new TermsOfUseSerializer();
    }

    @Provides
    @Singleton
    protected TypeToken<List<TermsOfUse>> provideTermsOfUseListTypeToken() {
        return new TypeToken<List<TermsOfUse>>() {
        };
    }

    @Provides
    @Singleton
    protected TermsOfUseListSerializer provideTermsOfUseListSerializer() {
        return new TermsOfUseListSerializer();
    }

    @Provides
    @Singleton
    protected AnnouncementSerializer provideAnnouncementSerializer() {
        return new AnnouncementSerializer();
    }

    @Provides
    @Singleton
    protected FuelingSerializer provideFuelingSerializer() {
        return new FuelingSerializer();
    }

    @Provides
    @Singleton
    protected TypeToken<List<Fueling>> provideFuelingListTypeToken() {
        return new TypeToken<List<Fueling>>() {
        };
    }

    @Provides
    @Singleton
    protected FuelingListSerializer provideFuelingListSerializer() {
        return new FuelingListSerializer();
    }

    @Provides
    @Singleton
    protected Gson provideGson(UserSerializer userSerializer, UserStatisticDeserializer userStatisticDeserializer, TypeToken<List<Car>> carListTypeToken,
                               CarListDeserializer carListDeserializer, TrackSerializer trackSerializer, MeasurementSerializer measurementSerializer,
                               TypeToken<List<Track>> trackListTypeToken, RemoteTrackListDeserializer remoteTrackListDeserializer, TermsOfUseSerializer termsOfUseSerializer,
                               TypeToken<List<TermsOfUse>> termsOfUseListTypeToken, TermsOfUseListSerializer termsOfUseListSerializer, AnnouncementSerializer announcementSerializer,
                               FuelingSerializer fuelingSerializer, TypeToken<List<Fueling>> fuelingListTypeToken, FuelingListSerializer fuelingListSerializer) {
        return new GsonBuilder()
                .registerTypeAdapter(User.class, userSerializer)
                .registerTypeAdapter(UserStatistics.class, userStatisticDeserializer)
                .registerTypeAdapter(Car.class, new CarSerializer())
                .registerTypeAdapter(carListTypeToken.getType(), carListDeserializer)
                .registerTypeAdapter(Track.class, trackSerializer)
                .registerTypeAdapter(Measurement.class, measurementSerializer)
                .registerTypeAdapter(trackListTypeToken.getType(), remoteTrackListDeserializer)
                .registerTypeAdapter(TermsOfUse.class, termsOfUseSerializer)
                .registerTypeAdapter(termsOfUseListTypeToken.getType(), termsOfUseListSerializer)
                .registerTypeAdapter(Announcement.class, announcementSerializer)
                .registerTypeAdapter(Fueling.class, fuelingSerializer)
                .registerTypeAdapter(fuelingListTypeToken.getType(), fuelingListSerializer)
                .create();
    }
}