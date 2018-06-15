package com.alexshr.baking;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alexshr.baking.db.RecipesDao;
import com.alexshr.baking.db.RecipesDb;
import com.alexshr.baking.ui.MainActivity;
import com.alexshr.baking.utils.EspressoTracker;
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.alexshr.baking.AppConfig.DATABASE_NAME;
import static com.alexshr.baking.AppTest.Constants.APP_TITLE;
import static com.alexshr.baking.AppTest.Constants.INGREDIENTS_AMOUNT;
import static com.alexshr.baking.AppTest.Constants.INGREDIENTS_RV_ID;
import static com.alexshr.baking.AppTest.Constants.RECIPES;
import static com.alexshr.baking.AppTest.Constants.RECIPES_RV_ID;
import static com.alexshr.baking.AppTest.Constants.STEPS;
import static com.alexshr.baking.AppTest.Constants.STEPS_RV_ID;
import static com.alexshr.baking.AppTest.Constants.SUBTITLE;
import static com.alexshr.baking.utils.EspressoUtils.isTablet;
import static com.alexshr.baking.utils.EspressoUtils.matchRecyclerViewItem;
import static com.alexshr.baking.utils.EspressoUtils.matchToolbarEmptySubtitle;
import static com.alexshr.baking.utils.EspressoUtils.matchToolbarSubtitle;
import static com.alexshr.baking.utils.EspressoUtils.matchToolbarTitle;
import static com.alexshr.baking.utils.EspressoUtils.rotateScreen;
import static com.alexshr.baking.utils.RecyclerViewItemCountAssertion.withItemCount;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;

/**
 * Demonstration of some test techniques
 * <p>
 * Using my EspressoTracker:
 * Idling resources and animations turn off is not required!
 * <p>
 * Screen rotation checking
 * <p>
 * Test takes into account the "phone/tablet" difference
 * <p>
 * Such a detail check of list data is for studying (makes real sense with data mock only)
 */
@RunWith(AndroidJUnit4.class)
public class AppTest {

    public interface Constants {
        String APP_TITLE = "Baking recipes";
        String SUBTITLE = "ingredients"; //

        int RECIPES_RV_ID = R.id.recipes;
        int STEPS_RV_ID = R.id.stepsRv;
        int INGREDIENTS_RV_ID = R.id.ingredientsRw;

        String[] RECIPES = new String[]{"Nutella Pie", "Brownies", "Yellow Cake", "Cheesecake"};

        //for RECIPES[0]
        String[] STEPS = new String[]{"0. Recipe Introduction", "1. Starting prep", "2. Prep the cookie crust.", "3. Press the crust into baking form.", "4. Start filling prep", "5. Finish filling prep", "6. Finishing Steps"};

        int INGREDIENTS_AMOUNT = 9;
    }

    EspressoTracker tracker = new EspressoTracker<ViewInteraction>();

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, true);

    //@Rule not workes with room
    //public ClearDatabaseRule clearDatabaseRule = new ClearDatabaseRule();//clear db

    @Rule
    public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();//clear pref before every test

    @Before
    public void setUp() {
        //clearDbData();//having some trouble
    }

    @Test
    public void checkMainActivity() {

        for (int i = 0; i < 3; i++) {
            if (i > 0) rotateScreen();
            tracker.call(this::checkRecipeList, 4000, 100);
            checkMainToolbarTitle();
        }
    }

    @Test
    public void checkRecipeActivity() {

        tracker.call(this::clickFirstRecipe, 4000, 100);
        for (int i = 0; i < 3; i++) {
            if (i > 0) rotateScreen();//sometimes rotation doesn't work!!!
            tracker.call(this::checkStepList, 4000, 100);
            checkIngredientsLinkDisplayed();
            checkRecipeToolbarTitle();
        }
    }

    @Test
    public void checkSingleOrDuoMode() {
        boolean isTablet = isTablet();
        tracker.call(this::clickFirstRecipe, 4000, 100);

        tracker.call(() -> {
            if (isTablet) {
                checkIngredientList();
                checkRecipeToolbarSubtitle();
            } else {
                checkIngredientListNotExists();
                checkRecipeToolbarEmptySubtitle();
            }
        }, 100, 2000);
    }

    private void checkIngredientList() {
        onView(withId(INGREDIENTS_RV_ID)).check(withItemCount(INGREDIENTS_AMOUNT));
    }

    private void checkIngredientListNotExists() {
        assertNotExist(INGREDIENTS_RV_ID);
    }

    private void checkRecipeList() {
        for (int i = 0; i < RECIPES.length; i++) {
            matchRecyclerViewItem(RECIPES_RV_ID, i, RECIPES[i]);
        }
    }

    private void checkStepList() {

        for (int i = 0; i < STEPS.length; i++) {
            matchRecyclerViewItem(STEPS_RV_ID, i, STEPS[i]);
        }
    }

    private void checkIngredientsLinkDisplayed() {
        assertDisplayed("Ingredients");
    }

    private void checkMainToolbarTitle() {
        matchToolbarTitle(APP_TITLE);
    }

    private void checkRecipeToolbarTitle() {
        matchToolbarTitle(RECIPES[0]);
    }

    private void checkRecipeToolbarSubtitle() {
        matchToolbarSubtitle(SUBTITLE);
    }

    private void checkRecipeToolbarEmptySubtitle() {
        matchToolbarEmptySubtitle();
    }

    private void clickFirstRecipe() {
        clickListItem(RECIPES_RV_ID, 0);
    }

    private String getActionBarStepSubtitle(int stepId) {
        return "step " + stepId;
    }

    private void clearDbData() {
        Context context = InstrumentationRegistry.getTargetContext();
        RecipesDao dao = Room.databaseBuilder(context,
                RecipesDb.class,
                DATABASE_NAME)
                .build().recipesDao();
        dao.deleteRecipes();
        Timber.i("executed");
    }
}
