/**
 * Created by alexshr on 02.05.2017.
 */

package com.alexshr.baking.utils;

import android.os.SystemClock;

import java.util.Date;
import java.util.concurrent.Callable;

import timber.log.Timber;

/**
 * The utility repeats runnable or callable executing until it passes without errors or throws throwable after timeout.
 * It works perfectly for Espresso tests.
 * <p>
 * Suppose the last view interaction (button click) activates some background threads (network, database etc.).
 * As the result, a new screen should appear and we want to check it in our next step,
 * but we don't know when new screen will be ready to be tested.
 * <p>
 * Recommended approach is to force your app to send messages about threads states to your test.
 * Sometimes we can use built-in mechanisms like OkHttp3IdlingResource.
 * In other cases, we should insert code pieces in different places of your app sources (you should known app logic!) for testing support only.
 * Moreover, we should turn off all your animations (although it's a part on ui).
 * <p>
 * The other approach is waiting, e.g. SystemClock.sleep(10000). But we don't known how long to wait and even long delays can't guarantee success.
 * On the other hand your test will last long.
 * <p>
 * My approach is to add time condition to view interaction. E.g. we test that new screen should appear during 10000 mc (timeout).
 * But we don't wait and check new screen as quickly as we want (e.g. every 100 ms).
 * Of course, we block test thread such way, but usually it's just what we need in such cases.
 * <p>
 * Usage:
 * <p>
 * EspressoTracker tracker = new EspressoTracker<ViewInteraction>();
 * <p>
 * ViewInteraction loginButton = onView(withId(R.id.login_btn));
 * loginButton.perform(click());
 * <p>
 * tracker.call(()->onView(allOf(withId(R.id.title),isDisplayed())),2000,100);
 */
public class EspressoTracker<T> {

    public static long PERIOD_DEFAULT = 100;
    //public static long BEFORE_DELAY_DEFAULT = 0;

    //private long mRepeatDelay;//delay between attempts
    //private long mBeforeDelay;//to start attempts after this initial delay only

    //private long mTimeout;//timeout for view interaction

    private T mResult;

    private int mAttemptsCount;

    private Long mStartTime;

    /**
     * call with result
     *
     * @param callable
     * @return callable result
     * or throws RuntimeException (test failure)
     */
    public T call(Callable<T> callable, long timeout, long period) {
        process(callable, timeout, period);
        return mResult;
    }

    public T call(Callable<T> callable, long timeout) {
        process(callable, timeout, null);
        return mResult;
    }

    /**
     * call without result
     *
     * @param runnable
     * @return void
     * or throws RuntimeException (test failure)
     */
    public void call(Runnable runnable, long timeout, long period) {
        process(runnable, timeout, period);
    }

    public void call(Runnable runnable, long timeout) {
        process(runnable, timeout, null);
    }

    /**
     * @param timeout timeout for view interaction
     * @param period  - delay between executing attempts
     */
    private void process(Object obj, long timeout, Long period) {
        period = period == null ? PERIOD_DEFAULT : period;
        mAttemptsCount++;
        long duration = 0;
        try {
            if (mStartTime == null) {
                mStartTime = new Date().getTime();
                //Timber.v("sleep delay= " + before);
                //SystemClock.sleep(before);
            }

            if (obj instanceof Callable) {
                Timber.v("call callable");
                mResult = ((Callable<T>) obj).call();
            } else {
                Timber.v("call runnable");
                ((Runnable) obj).run();
            }
            duration = new Date().getTime() - mStartTime;
            mStartTime = null;
            Timber.i("success; attemptsCount: %d, duration: %d ms", mAttemptsCount, duration);
        } catch (Throwable e) {
            long remain = new Date().getTime() - mStartTime;
            Timber.v("remain time= " + remain);
            if (remain > timeout) {
                Timber.e("error; duration: %d ms", duration);
                mStartTime = null;
                throw new RuntimeException(e);
            } else {
                Timber.d("sleep delay= " + period);
                SystemClock.sleep(period);
                process(obj, timeout, period);
            }
        }
    }
}