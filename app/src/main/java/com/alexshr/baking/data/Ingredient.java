package com.alexshr.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "ingredients",
        primaryKeys = {"recipeId", "position"},
        indices = {@Index("recipeId")},
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE))

public class Ingredient {

    private int position;
    private int recipeId;

    @SerializedName("quantity")
    private float quantity;
    @SerializedName("measure")
    private String measure;
    @SerializedName("ingredient")
    private String ingredientName;

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ingredient{");
        sb.append("position=").append(position);
        sb.append(", recipeId=").append(recipeId);
        sb.append(", quantity=").append(quantity);
        sb.append(", measure='").append(measure).append('\'');
        sb.append(", ingredientName='").append(ingredientName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
