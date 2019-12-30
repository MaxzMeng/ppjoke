package me.maxandroid.ppjoke.utils;

import android.content.ComponentName;

import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;

import me.maxandroid.ppjoke.model.Destination;

public class NavGraphBuilder {
    public static void build(NavController controller) {
        NavigatorProvider provider = controller.getNavigatorProvider();

        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));
        HashMap<String, Destination> destConfig = AppConfig.getsDestConfig();
        for (Destination value : destConfig.values()) {
            if (value.isFragment) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setId(value.id);
                destination.setClassName(value.className);
                destination.addDeepLink(value.pageUrl);
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.id);
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), value.className));
                destination.addDeepLink(value.pageUrl);
                navGraph.addDestination(destination);
            }

            if (value.asStarter) {
                navGraph.setStartDestination(value.id);
            }
        }

        controller.setGraph(navGraph);
    }
}
