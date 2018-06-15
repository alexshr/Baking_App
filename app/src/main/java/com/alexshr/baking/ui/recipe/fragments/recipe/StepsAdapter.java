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

package com.alexshr.baking.ui.recipe.fragments.recipe;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.binding.DataBoundListAdapter;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.data.Step;
import com.alexshr.baking.databinding.StepItemBinding;

import java.util.Objects;

/**
 * A RecyclerView adapter for {@link Recipe} class.
 */
public class StepsAdapter extends DataBoundListAdapter<Step, StepItemBinding> {
    private final StepClickCallback clickCallback;

    public StepsAdapter(StepClickCallback clickCallback, Integer selectedPos) {
        this.clickCallback = clickCallback;
        if (selectedPos != null) setSelectedPos(selectedPos);
    }

    @Override
    protected StepItemBinding createBinding(ViewGroup parent) {
        StepItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.step_item,
                        parent, false);

        return binding;
    }

    @Override
    protected void bind(StepItemBinding binding, Step item) {
        binding.setStep(item);
        binding.getRoot().setOnClickListener(v -> {
            //selectItem(position);
            clickCallback.onStepClick(binding.getStep().getPosition());
        });
    }

    @Override
    protected boolean areItemsTheSame(Step oldItem, Step newItem) {
        return Objects.equals(oldItem.getPosition(), newItem.getPosition());
    }

    @Override
    protected boolean areContentsTheSame(Step oldItem, Step newItem) {
        return Objects.equals(oldItem.getShortDescription(), newItem.getShortDescription());
    }

    public interface StepClickCallback {
        void onStepClick(int position);
    }
}
