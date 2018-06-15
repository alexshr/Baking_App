package com.alexshr.baking.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alexshr.baking.AppConfig;
import com.alexshr.baking.api.ApiService;
import com.alexshr.baking.db.RecipesDao;
import com.alexshr.baking.db.RecipesDb;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.alexshr.baking.AppConfig.BASE_URL;
import static com.alexshr.baking.AppConfig.DATABASE_NAME;
import static com.alexshr.baking.AppConfig.ETAG_HEADER;
import static com.alexshr.baking.AppConfig.MAX_CONNECTION_TIMEOUT;
import static com.alexshr.baking.AppConfig.MAX_READ_TIMEOUT;
import static com.alexshr.baking.AppConfig.MAX_WRITE_TIMEOUT;

@Module(includes = ViewModelModule.class)
class AppModule {

    //region ================= network providers =================

    @Singleton
    @Provides
    ApiService provideApiService(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApiService.class);
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(createETagInterceptor())
                .connectTimeout(MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    /*@Singleton
    @Provides
    ConnectionChecker provideConnectionChecker(Application app) {
        return new ConnectionChecker(app);
    }*/

    private Interceptor createETagInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                String eTag = Hawk.get(ETAG_HEADER);
                if (eTag != null) {
                    request = request.newBuilder()
                            .addHeader(AppConfig.IF_NONE_MATCH_HEADER, Hawk.get(AppConfig.ETAG_HEADER))
                            .build();
                }
                Response response = chain.proceed(request);
                return response;
            }
        };
    }

    //endregion

    @Singleton
    @Provides
    RecipesDao provideRecipeDao(Context context) {
        return Room.databaseBuilder(context,
                RecipesDb.class,
                DATABASE_NAME)
                .build().recipesDao();
    }

    @Singleton
    @Provides
    Context provideContext(Application app) {
        return app;
    }
}
