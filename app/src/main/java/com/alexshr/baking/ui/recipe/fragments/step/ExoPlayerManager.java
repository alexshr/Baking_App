package com.alexshr.baking.ui.recipe.fragments.step;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class ExoPlayerManager extends Player.DefaultEventListener {

    private String videoUrl;
    private StepFragment fragment;
    private MediaSessionCompat mediaSession;
    private SimpleExoPlayer player;
    private PlaybackStateCompat.Builder stateBuilder;

    public void prepare(StepFragment fragment, String url) {
        this.fragment = fragment;
        videoUrl = url;
        Timber.d("fragment: %s; url: %s;", fragment, url);
        //ExoPlayerManager playerManager = new ExoPlayerManager(fragment, url);
        addLifecycleObserver();
    }

    @Inject
    public ExoPlayerManager() {

    }

    private void addLifecycleObserver() {

        fragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                Timber.d("StepFragment lifecycle: onStart -  %s,", fragment);
                if (Util.SDK_INT > 23) {
                    if (player == null) {
                        setup();
                    } else {
                        activate();
                    }
                }
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                Timber.d("StepFragment lifecycle: onResume -  %s,", fragment);

                if ((Util.SDK_INT <= 23 || player == null)) {
                    if (player == null) {
                        setup();
                    } else {
                        activate();
                    }
                }
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                Timber.d("StepFragment lifecycle: onPause -  %s,", fragment);

                if (Util.SDK_INT <= 23) {
                    if (mediaSession.isActive()) {
                        deactivate();
                    }
                }
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                Timber.d("StepFragment lifecycle: onStop -  %s,", fragment);

                if (Util.SDK_INT > 23) {
                    if (mediaSession.isActive()) {
                        deactivate();
                    }
                }
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (player != null) {
                    if (!fragment.isRotating()) {
                        release();
                    }
                }
                fragment.getLifecycle().removeObserver(this);
            }
        });
    }

    private void release() {
        Timber.i("release");
        releaseSession();
        releasePlayer();
    }

    private void deactivate() {
        Timber.i("deactivate");
        fragment.setPlayPosition(player.getCurrentPosition());
        fragment.setPlayWhenReady(player.getPlayWhenReady());
        mediaSession.setActive(false);
        player.setPlayWhenReady(false);
    }

    private void activate() {
        Timber.i("activate");
        mediaSession.setActive(true);
        player.seekTo(fragment.getPlayPosition());
        player.setPlayWhenReady(fragment.isPlayWhenReady());
        fragment.setPlayer(player);
    }

    private void setup() {
        Timber.i("setup");
        initMediaSession();
        initPlayer(videoUrl);
        fragment.setPlayer(player);
    }

    private void initMediaSession() {

        mediaSession = new MediaSessionCompat(fragment.getContext(), "RecipeStepSinglePageFragment");

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPause() {
                super.onPause();
                player.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                player.seekTo(0);
            }
        });
        mediaSession.setActive(true);
    }

    private void initPlayer(String url) {
        Timber.d("initPlayer url=%s, player: %s, playPosition=%s, isPlayWhenReady=%s", url, player, fragment.getPlayPosition(), fragment.isPlayWhenReady());

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(fragment.getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        player.addListener(this);

        MediaSource mediaSource = buildMediaSource(url);
        player.prepare(mediaSource);
    }

    private MediaSource buildMediaSource(String url) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("baking_app"))
                .createMediaSource(Uri.parse(url));
    }

    private void releasePlayer() {
        Timber.d("releasePlayer : %s", player);

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void releaseSession() {
        Timber.d("releaseSession : %s", mediaSession);

        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession = null;
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1f);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }
}
