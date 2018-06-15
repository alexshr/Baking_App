package com.alexshr.baking.binding;

import android.content.res.Configuration;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.alexshr.baking.glide.GlideApp;
import com.alexshr.baking.ui.recipe.fragments.step.ExoPlayerManager;
import com.alexshr.baking.ui.recipe.fragments.step.StepFragment;
import com.google.android.exoplayer2.ui.PlayerView;

import timber.log.Timber;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Binding adapters that work with a fragment instance.
 */
public class StepFragmentBindingAdapters {
    final StepFragment fragment;
    ExoPlayerManager playerManager;

    public StepFragmentBindingAdapters(StepFragment fragment, ExoPlayerManager playerManager) {
        this.fragment = fragment;
        this.playerManager = playerManager;
    }

    @BindingAdapter("imageUrl")
    public void bindImage(ImageView imageView, String url) {
        int visibility = GONE;
        if (url != null && !url.isEmpty()) {
            GlideApp.with(fragment)
                    .load(url)
                    .into(imageView);
            visibility = VISIBLE;
        }
        imageView.setVisibility(visibility);
    }

    @BindingAdapter("videoUrl")
    public void bindVideo(PlayerView playerView, String url) {
        int visibility = GONE;
        if (url != null && !url.isEmpty()) {
            Timber.d("bindVideo url=%s", url);
            Configuration config = fragment.getResources().getConfiguration();
            if (config.orientation == ORIENTATION_LANDSCAPE
                    && config.smallestScreenWidthDp < 600) {
                fragment.setupFullScreenVideoMode();
            }
            playerManager.prepare(fragment, url);
            visibility = VISIBLE;
        }
        playerView.setVisibility(visibility);
    }
}
