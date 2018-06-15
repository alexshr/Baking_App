package com.alexshr.baking.ui.recipe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import timber.log.Timber;

public class FragmentsFlowManager {

    private FragmentManager fm;
    private ViewGroup rc;

    public FragmentsFlowManager(FragmentActivity activity) {
        fm = activity.getSupportFragmentManager();
        rc = activity.findViewById(android.R.id.content);
    }

    public void addFragment(Fragment fragment, Container container, String tag, boolean isBackStack) {

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(container.id, fragment, tag);
        if (isBackStack) transaction.addToBackStack(null);
        transaction.commit();
        fm.executePendingTransactions();
        Timber.d("fragment added: fragment %s, container=%s, tag=%s", fragment, container.name, tag);
    }

    public void replaceFragment(Fragment fragment, Container container, String tag, boolean isBackStack) {
        FragmentTransaction transaction = fm.beginTransaction();
        //transaction.remove(fragment);
        transaction.replace(container.id, fragment, tag);
        if (isBackStack) transaction.addToBackStack(null);
        transaction.commit();
        fm.executePendingTransactions();
        Timber.d("fragment replaced: fragment %s, containerId=%s, tag=%s", fragment, container.name, tag);
    }

    private Fragment getFragment(String tag) {
        return fm.findFragmentByTag(tag);
    }

    private Fragment getFragment(int containerId) {
        return fm.findFragmentById(containerId);
    }

    public boolean isPlaced(Container cont) {
        boolean isPlaced = getFragment(cont.id) != null;
        Timber.d("Fragment isPlaced: %s in container: %s", isPlaced, cont.name);
        return isPlaced;
    }

    public Fragment removeFromFragmentManager(String tag) {
        Fragment fragment = getFragment(tag);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
            fm.executePendingTransactions();
            //fm.executePendingTransactions();
            Timber.d("removed: tag=%s, fragment=%s", tag, fragment);
        } else {
            Timber.d("No Fragment found by tag=%s", tag);
        }
        return fragment;
    }

    /*private void clearBackStack() {
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Timber.d("");
    }*/

    public void popupBackStack() {
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Timber.d("");
    }



    /*public static class FragmentTask<T extends Fragment> {
        Class<T> fragmentClass;
        String tag;
        Boolean isReplace;//add or replace
        Container container;
        boolean isBackStack;

        Fragment fragment;

        public FragmentTask(Class<T> fragmentClass, String tag, Container cont, Boolean isReplace, Boolean isBackStack) {
            this.fragmentClass = fragmentClass;
            this.tag = tag;
            this.isReplace = isReplace;
            this.isBackStack = isBackStack;
            this.container = cont;
            Timber.d("%s", this);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FragmentTask{");
            sb.append("fragmentClass=").append(fragmentClass);
            sb.append(", tag='").append(tag).append('\'');
            sb.append(", isReplace=").append(isReplace);
            sb.append(", container=").append(container);
            sb.append(", fragment=").append(fragment);
            sb.append('}');
            return sb.toString();
        }
    }*/

    public static class Container {

        int id;
        String name;//for debug only

        public Container(int id, String name) {
            this.id = id;
            this.name = name == null ? "no description" : name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Container{");
            sb.append("id=").append(id);
            sb.append(", name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
