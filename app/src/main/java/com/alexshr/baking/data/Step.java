package com.alexshr.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

@Entity(tableName = "steps",
        primaryKeys = {"recipeId", "position"},
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE))

public class Step {

    private int recipeId;

    private int position;

    @SerializedName("id")
    private int orderNum;
    @SerializedName("shortDescription")
    private String shortDescription;
    @SerializedName("description")
    private String description;
    @SerializedName("videoURL")
    private String videoUrl;
    @SerializedName("thumbnailURL")
    private String thumbnailUrl;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        //this.thumbnailUrl = thumbnailUrl;

        //fix current data error
        if (thumbnailUrl.endsWith("mp4")) {
            setVideoUrl(thumbnailUrl);
            Timber.i("data was corrected (video instead of image); url= %s", thumbnailUrl);
        } else this.thumbnailUrl = thumbnailUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Step{");
        sb.append("recipeId=").append(recipeId);
        sb.append(", position=").append(position);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", shortDescription='").append(shortDescription).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", videoUrl='").append(videoUrl).append('\'');
        sb.append(", thumbnailUrl='").append(thumbnailUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
