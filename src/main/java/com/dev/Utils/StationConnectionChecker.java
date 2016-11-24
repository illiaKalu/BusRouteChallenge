package com.dev.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sonicmaster on 23.11.16.
 */
public class StationConnectionChecker implements Callable<Boolean> {

    List<String> routesToCheck = new ArrayList<>();
    int dep, arr;

    Matcher matcherDep;
    Matcher matcherArr;
    Pattern patternDep;
    Pattern patternArr;

    public StationConnectionChecker(ArrayList routesBunch, int dep, int arr) {

        this.routesToCheck = routesBunch;
        this.dep = dep;
        this.arr = arr;

        String patternStringDep = "(\\s|^)" + dep + "([\\s]|$)";
        String patternStringArr = "(\\s|^)" + arr + "([\\s]|$)";

        patternDep = Pattern.compile(patternStringDep);
        patternArr = Pattern.compile(patternStringArr);



    }

    @Override
    public Boolean call() throws Exception {

        // searching in line with stations needed station ids
        for (String routeIds : routesToCheck) {
            matcherDep = patternDep.matcher(routeIds);
            matcherArr = patternArr.matcher(routeIds);
            if (matcherDep.find() && matcherArr.find()) return true;
        }

        return false;
    }
}
