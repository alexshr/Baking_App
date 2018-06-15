
package com.alexshr.baking.ui;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.binding.DataBoundListAdapter;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.databinding.RecipeItemBinding;

import java.util.Objects;

/**
 * A RecyclerView adapter for {@link Recipe} class.
 */
public class RecipesAdapter extends DataBoundListAdapter<Recipe, RecipeItemBinding> {
    private final RecipeClickCallback clickCallback;

    public RecipesAdapter(RecipeClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    protected RecipeItemBinding createBinding(ViewGroup parent) {
        RecipeItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.recipe_item,
                        parent, false);
        binding.getRoot().setOnClickListener(v -> {
            clickCallback.onRecipeClick(binding.getRecipe().getId());
        });
        return binding;
    }

    @Override
    protected void bind(RecipeItemBinding binding, Recipe item) {
        binding.setRecipe(item);
    }

    @Override
    protected boolean areItemsTheSame(Recipe oldItem, Recipe newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    protected boolean areContentsTheSame(Recipe oldItem, Recipe newItem) {
        return Objects.equals(oldItem.getName(), newItem.getName());
    }

    public interface RecipeClickCallback {
        void onRecipeClick(int recipeId);
    }
}
