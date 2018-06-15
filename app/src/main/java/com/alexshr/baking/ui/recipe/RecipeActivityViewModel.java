package com.alexshr.baking.ui.recipe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alexshr.baking.R;
import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.ui.recipe.fragments.ingredients.IngredientsFragment;
import com.alexshr.baking.ui.recipe.fragments.navigation.NavigationFragment;
import com.alexshr.baking.ui.recipe.fragments.recipe.RecipeFragment;
import com.alexshr.baking.ui.recipe.fragments.step.StepFragment;
import com.alexshr.baking.viewmodel.AbstractViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static android.arch.lifecycle.Transformations.map;
import static com.alexshr.baking.AppConstants.RECIPE_ID_KEY;
import static com.alexshr.baking.AppConstants.STEP_ID_KEY;
import static com.alexshr.baking.AppConstants.STEP_INGREDIENTS;
import static com.alexshr.baking.AppConstants.STEP_NONE;
import static com.alexshr.baking.ui.recipe.FragmentsFlowManager.Container;

public class RecipeActivityViewModel extends AbstractViewModel<Integer> {

    private int recipeId;
    private boolean isDual;

    private FragmentsFlowManager fManager;

    private MutableLiveData<String> actionBarSubtitleData = new MutableLiveData<>();

    private MutableLiveData<Integer> stepIdData = new MutableLiveData<>();

    private MutableLiveData<Boolean> hasNextData = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasPreviousData = new MutableLiveData<>();
    //private LiveData<Integer> stepsAmountData;
    //private LiveData<List<Step>> stepsData;

    //private int stepsAmount;

    //single port
    public static Container SINGLE_FLOW_CONTAINER = new Container(R.id.container, "SINGLE_FLOW_CONTAINER");
    public static Container SINGLE_NAV_CONTAINER = new Container(R.id.navigation, "SINGLE_NAV_CONTAINER");

    //duo
    public static Container DUO_MASTER_CONTAINER = new Container(R.id.master, "DUO_MASTER_CONTAINER");
    public static Container DUO_DETAIL_CONTAINER = new Container(R.id.detail, "DUO_DETAIL_CONTAINER");

    public static String MASTER_TAG = "masterTag";
    public static String DETAIL_TAG = "detailTag";
    public static String NAV_TAG = "navTag";

    private boolean isPortraitSingle;
    private RecipesRepository repository;

    @Inject
    public RecipeActivityViewModel(RecipesRepository rep) {
        super(rep);
        repository = rep;

        Timber.d("RecipeActivityViewModel created %s", this);
    }

    public LiveData<Integer> getStepsAmountData() {
        if (getLiveData() == null) setLiveData(getRepository().getStepsAmount(recipeId));
        return getLiveData();
    }

    private void checkNavigation() {
        if (getStepsAmountData().getValue() != null) {
            int amount = getStepsAmountData().getValue();
            Timber.d("stepId=%d, stepsAmount=%s", getStepId(), amount);
            hasPreviousData.setValue(getStepId() > STEP_INGREDIENTS);

            hasNextData.setValue(getStepId() < amount - 1);
        } else Timber.d("stepId=%d, stepsData is null", getStepId());
    }

    public MutableLiveData<String> getActionBarSubtitleData() {
        return actionBarSubtitleData;
    }

    public LiveData<String> getActionBarTitleData() {
        return map(repository.getRecipeData(recipeId), recipe -> recipe.getName());
    }

    public void onActivityCreate(RecipeActivity activity, Bundle inState) {
        fManager = new FragmentsFlowManager(activity);
        restoreState(activity, inState);
        restoreFragments();
        //stepsData = repository.getStepsData(recipeId);
        //stepsData.observeForever(list -> checkNavigation());
        //stepsAmountData = repository.getStepsAmount(recipeId);
        stepIdData.observeForever(stepId -> {
            checkNavigation();
            postSubtitle();
        });
        getStepsAmountData().observe(activity, amount -> checkNavigation());
    }

    private void postSubtitle() {
        String text = null;
        switch (getStepId()) {
            case STEP_NONE:
                break;
            case STEP_INGREDIENTS: {
                text = "ingredients";
                break;
            }
            default:
                text = "step " + getStepId();
        }
        actionBarSubtitleData.setValue(text);
    }

    private Fragment createDetailFragment() {
        switch (getStepId()) {
            case STEP_NONE: {
                return null;
            }
            case STEP_INGREDIENTS: {
                return new IngredientsFragment();
            }
            default:
                return new StepFragment();
        }
    }

    public void restoreFragments() {

        if (isDual) {
            if (!fManager.isPlaced(DUO_MASTER_CONTAINER)) {
                Fragment detailFragment = fManager.removeFromFragmentManager(DETAIL_TAG);
                if (detailFragment == null) {
                    detailFragment = createDetailFragment();
                } else fManager.popupBackStack();

                Fragment masterFragment = fManager.removeFromFragmentManager(MASTER_TAG);
                if (masterFragment == null) masterFragment = new RecipeFragment();

                fManager.addFragment(masterFragment, DUO_MASTER_CONTAINER, MASTER_TAG, false);
                fManager.addFragment(detailFragment, DUO_DETAIL_CONTAINER, DETAIL_TAG, false);
            }
        } else {
            if (!fManager.isPlaced(SINGLE_FLOW_CONTAINER)) {

                Fragment masterFragment = fManager.removeFromFragmentManager(MASTER_TAG);
                if (masterFragment == null) masterFragment = new RecipeFragment();

                fManager.addFragment(masterFragment, SINGLE_FLOW_CONTAINER, MASTER_TAG, false);

                if (getStepId() != STEP_NONE) {
                    Fragment detailFragment = fManager.removeFromFragmentManager(DETAIL_TAG);
                    if (detailFragment == null) {
                        detailFragment = createDetailFragment();
                    }
                    fManager.replaceFragment(detailFragment, SINGLE_FLOW_CONTAINER, DETAIL_TAG, true);
                }
            }
            if (isPortraitSingle) {
                Fragment navFragment = fManager.removeFromFragmentManager(NAV_TAG);
                if (navFragment == null) navFragment = new NavigationFragment();
                fManager.addFragment(navFragment, SINGLE_NAV_CONTAINER, NAV_TAG, false);
            }
        }
    }

    private void openDetail() {

        Fragment detailFragment = createDetailFragment();

        Container detailContainer = isDual ? DUO_DETAIL_CONTAINER : SINGLE_FLOW_CONTAINER;

        if (getStepId() != STEP_NONE && !isDual) fManager.popupBackStack();

        fManager.replaceFragment(detailFragment, detailContainer, DETAIL_TAG, !isDual);
    }

    public void onDetailOpen(int position) {
        setStepId(position);
        openDetail();
    }

    public void stepNext() {
        if (!hasNextData.getValue()) {
            Timber.e("next replaceDetail is not available");
        } else {
            setStepId(getStepId() + 1);
            openDetail();
        }
    }

    public void stepPrev() {
        if (!hasPreviousData.getValue()) {
            Timber.e("previous replaceDetail is not available");
        } else {
            setStepId(getStepId() - 1);
            openDetail();
        }
    }

    public Bundle fillState(Bundle outState) {
        outState.putInt(RECIPE_ID_KEY, recipeId);
        outState.putInt(STEP_ID_KEY, getStepId());
        return outState;
    }

    private void restoreState(RecipeActivity activity, Bundle inState) {
        recipeId = activity.getIntent().getIntExtra(RECIPE_ID_KEY, 0);
        setStepId(STEP_NONE);
        if (inState != null) {
            recipeId = inState.getInt(RECIPE_ID_KEY);
            setStepId(inState.getInt(STEP_ID_KEY, STEP_NONE));
        }

        isDual = activity.binding.master != null;

        isPortraitSingle = activity.binding.navigation != null;

        if (isDual && getStepId() == STEP_NONE) setStepId(STEP_INGREDIENTS);
    }

    public Integer getStepId() {
        return stepIdData.getValue();
    }

    public void setStepId(int stepId) {
        stepIdData.setValue(stepId);
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void onBackPressed() {
        setStepId(STEP_NONE);
        actionBarSubtitleData.postValue("");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.d(this + "");
    }

    public MutableLiveData<Boolean> getHasNextData() {
        return hasNextData;
    }

    public MutableLiveData<Boolean> getHasPreviousData() {
        return hasPreviousData;
    }

    public MutableLiveData<Integer> getStepIdData() {
        return stepIdData;
    }
}
