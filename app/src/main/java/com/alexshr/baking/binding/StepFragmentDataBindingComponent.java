/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexshr.baking.binding;

import android.databinding.DataBindingComponent;

import com.alexshr.baking.ui.recipe.fragments.step.ExoPlayerManager;
import com.alexshr.baking.ui.recipe.fragments.step.StepFragment;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * A Data Binding Component implementation for fragments.
 */
public class StepFragmentDataBindingComponent implements DataBindingComponent {
    private final StepFragmentBindingAdapters adapter;

    @Inject
    public StepFragmentDataBindingComponent(StepFragment fragment, ExoPlayerManager playerManager) {
        this.adapter = new StepFragmentBindingAdapters(fragment, playerManager);
        Timber.d("created %s", this);
    }

    @Override
    public StepFragmentBindingAdapters getStepFragmentBindingAdapters() {
        return adapter;
    }
}
