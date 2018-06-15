

package com.alexshr.baking.ui.recipe.fragments.ingredients;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.binding.DataBoundListAdapter;
import com.alexshr.baking.data.Ingredient;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.databinding.IngredientItemBinding;

import java.util.Objects;

/**
 * A RecyclerView adapter for {@link Recipe} class.
 */
public class IngredientsAdapter extends DataBoundListAdapter<Ingredient, IngredientItemBinding> {

    @Override
    protected IngredientItemBinding createBinding(ViewGroup parent) {
        IngredientItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.ingredient_item,
                        parent, false);

        return binding;
    }

    @Override
    protected void bind(IngredientItemBinding binding, Ingredient item) {
        binding.setIngredient(item);
    }

    @Override
    protected boolean areItemsTheSame(Ingredient oldItem, Ingredient newItem) {
        return Objects.equals(oldItem.getPosition(), newItem.getPosition());
    }

    @Override
    protected boolean areContentsTheSame(Ingredient oldItem, Ingredient newItem) {
        return Objects.equals(oldItem.getIngredientName(), newItem.getIngredientName());
    }
}
