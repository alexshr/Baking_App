package com.alexshr.baking;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public interface AppConstants {

    String RECIPE_ID_KEY = "recipeIdKey";

    String STEP_ID_KEY = "stepIdKey";

    //for ExoPlayer
    String PLAY_READY_KEY = "PLAY_READY_KEY";
    String PLAY_POSITION_KEY = "PLAY_POSITION_KEY";
    String WIDGET_PREFIX = "widget";
    String WIDGET_IDS_KEY = "widgetIds";

    int STEP_INGREDIENTS = NO_POSITION;
    int STEP_NONE = -2;
}
