package com.alexshr.baking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.design.internal.NavigationMenuItemView;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;

import timber.log.Timber;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class EspressoUtils {

    public static ViewInteraction matchRecyclerViewItem(int rvId, int pos, String text) {
        /*return onView(withId(rvId))
                .check(matches(atPosition(pos, hasDescendant(withText(text)))));*/

        return onView(RecyclerViewMatcher.withRecyclerView(rvId).atPosition(pos))
                .check(matches(hasDescendant(withText(text))));
    }

    // but sometimes rotation  doesn't work!!!
    public static int rotateScreen() {
        Context context = InstrumentationRegistry.getTargetContext();
        int orientation
                = context.getResources().getConfiguration().orientation;

        int newOrient = orientation == Configuration.ORIENTATION_PORTRAIT ?
                SCREEN_ORIENTATION_LANDSCAPE :
                SCREEN_ORIENTATION_PORTRAIT;

        getCurrentActivity().setRequestedOrientation(newOrient);
        Timber.i("rotating to %s", newOrient == SCREEN_ORIENTATION_LANDSCAPE ? "landscape" : "portrait");
        return newOrient;
    }

    public static ViewInteraction matchViewExists(int viewId) {
        return onView(allOf(withId(viewId)));
    }

    public static ViewInteraction matchToolbarTitle(String title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    public static ViewInteraction matchToolbarSubtitle(String subtitle) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarSubtitle(is(subtitle))));
    }

    public static ViewInteraction matchToolbarEmptySubtitle() {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarEmptySubtitle()));
    }

    public static ViewInteraction matchToolbarTitle(int titleRes) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(getString(titleRes)))));
        //Timber.i("passed");
    }

    private static ViewInteraction matchActionMenuItemVisibility(final int menuItemId, boolean isVisible) {
        return onView(isAssignableFrom(ActionMenuView.class))
                .check(matches(withMenuItem(menuItemId, isVisible)));
    }

    public static ViewInteraction matchActionMenuItemDisplayed(final int menuItemId) {
        return matchActionMenuItemVisibility(menuItemId, true);
    }

    public static ViewInteraction matchActionMenuItemNotDisplayed(final int menuItemId) {
        return matchActionMenuItemVisibility(menuItemId, false);
    }

    public static ViewInteraction clickOnNavigationMenuItem(int titleRes) {
        return onView(allOf(withText(titleRes), withParent(isAssignableFrom(NavigationMenuItemView.class)))).perform(click());
    }

    private static void checkNavigationMenuItems(boolean isVisible, int... titles) {
        for (int title : titles) {
            onView(allOf(withText(title), withParent(isAssignableFrom(NavigationMenuItemView.class))))
                    .check(isVisible ? matches(isDisplayed()) : doesNotExist());
        }
    }

    public static void assertNavigationMenuItemsDisplayed(int... titles) {
        checkNavigationMenuItems(true, titles);
    }

    public static void assertNavigationMenuItemsNotExists(int... titles) {
        checkNavigationMenuItems(false, titles);
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    private static Matcher<Object> withToolbarSubtitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getSubtitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar subtitle: ");
                textMatcher.describeTo(description);
            }
        };
    }

    private static Matcher<Object> withToolbarEmptySubtitle() {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return toolbar.getSubtitle() == null || toolbar.getSubtitle().toString().trim().isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar empty subtitle: ");
            }
        };
    }

    private static Matcher<Object> withMenuItem(final int menuItemId, boolean isVisible) {
        return new BoundedMatcher<Object, ActionMenuView>(ActionMenuView.class) {
            @Override
            public boolean matchesSafely(ActionMenuView menuView) {
                View menuItem = menuView.findViewById(menuItemId);
                return isVisible ? menuItem.getVisibility() == View.VISIBLE : menuItem == null || menuItem.getVisibility() != View.VISIBLE;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar item id: " + menuItemId + " isVisible: " + isVisible);
            }
        };
    }

    public static String getString(int id) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getString(id);
    }

    public static boolean isTablet() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / displayMetrics.density;
        float screenSw = Math.min(widthDp, heightDp);
        return screenSw >= 600;
    }

    public static Activity getCurrentActivity() {

        final Activity[] currentActivity = new Activity[1];

        getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity[0] = resumedActivities.iterator().next();
            }
        });

        return currentActivity[0];
    }
}
