package com.dev.Controllers;

import com.dev.Utils.FileLoader;
import com.dev.Utils.StationConnectionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sonicmaster on 23.11.16.
 */

@Controller
public class RouteCheckController {

    // optimal number for 1 processor ( by my research )
    private static final int ROUTES_LIMIT_FOR_1_PROCESSOR = 12000;
    FileLoader fileLoader;

    @Autowired
    public void setFileLoader(FileLoader fileLoader){
        this.fileLoader = fileLoader;
    }

    @RequestMapping("/api/direct")
    public String uploadFilesPage(@RequestParam("dep_sid") int dep, @RequestParam("arr_sid") int arr, Model model) {

        // default values for model
        model.addAttribute("dep", dep);
        model.addAttribute("arr", arr);
        model.addAttribute("checkResult", false);

        // reg exp patterns
        String patternStringDep = "(\\s|^)" + dep + "([\\s]|$)";
        String patternStringArr = "(\\s|^)" + arr + "([\\s]|$)";

        /*
        I did a little research in finding number in string. Results :
        1) reg exp. The most efficient way. Gives me results ( for 100 000 lines, each 1000 symbols ) 23462 milliseconds
        2) Knuth–Morris–Pratt algorithm. Pretty same results ( a little bit longer ). Implementation is way harder
        1) parse into array data structure and find integers
            1.1 non sorted array. the worst time
            1.2 sorted array. binary search is fast but parsing and sorting takes time
         */
        Pattern patternDep = Pattern.compile(patternStringDep);
        Pattern patternArr = Pattern.compile(patternStringArr);

        Matcher matcherDep;
        Matcher matcherArr;

        // loading file which contains all of the routes
        File routesMap = fileLoader.loadFile();

        String bufferString;

        // read first line of file, depending on result choosing strategy how to parse it
        // Scanner implements AutoClosable, so try-with-resources will close scanner automatically
        try (Scanner scanner = new Scanner(routesMap)) {

            int numberOfBusRoutes = Integer.parseInt(scanner.nextLine());
            int numberOfAvailableProcessors = Runtime.getRuntime().availableProcessors();

            if (numberOfBusRoutes > ROUTES_LIMIT_FOR_1_PROCESSOR && numberOfAvailableProcessors > 1) {

                // creating executor for tasks which will check for stations connectivity
                ExecutorService executor = Executors.newFixedThreadPool(numberOfAvailableProcessors);

                List<Callable<Boolean>> tasksWhichCheckRoutes = makeListOfCheckingTasks(scanner, dep,
                        arr, numberOfAvailableProcessors, numberOfBusRoutes);

                // execute all tasks and wait for result
                executor.invokeAll(tasksWhichCheckRoutes)
                        .stream()
                        .map(future -> {
                            try {
                                return future.get();
                            }
                            catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        })
                        .forEach( (executeResult) -> {
                            // if any task returned true result - set checkResult flag and shutdown other executor
                            if (executeResult == true) {
                                model.addAttribute("checkResult", true);
                                shutDownExecutor(executor);
                            }
                        });

            }else {

                while (scanner.hasNext()) {

                    // read next line from routes file
                    // create regex matcher for read line
                    bufferString = removeRouteId(scanner.nextLine());
                    matcherDep = patternDep.matcher(bufferString);
                    matcherArr = patternArr.matcher(bufferString);

                    // if connection found, break iteration and set checkResult flag
                    if ( matcherDep.find() && matcherArr.find() ) {
                        model.addAttribute("checkResult", true);
                        break;
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "checkResult";
    }

    private void shutDownExecutor(ExecutorService executor) {
        try {
            //attempt to shutdown executor
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    private List<Callable<Boolean>> makeListOfCheckingTasks(Scanner scanner, int dep, int arr, int numberOfAvailableProcessors, int numberOfBusRoutes) {

        // list of tasks which will check stations connectivity
        List<Callable<Boolean>> tasksToExecute = new LinkedList<>();
        // calculate size for each chunk of data
        int bunchSize = numberOfBusRoutes / numberOfAvailableProcessors;
        // represents chunk of data for each list
        ArrayList routesBunch = new ArrayList<>(bunchSize);
        int counter = 0;

        while ( scanner.hasNext() ) {
            // read some amount of route ids
            routesBunch.add(removeRouteId(scanner.nextLine()));

            // if needed chunk of data already read, create new task which will parse this chunk
            if (counter % bunchSize == 0 ) {
                tasksToExecute.add(new StationConnectionChecker(routesBunch, dep, arr));
                routesBunch.clear();
            }

            counter++;
        }

        // "flush" for remaining routes ids
        if (!routesBunch.isEmpty())
        tasksToExecute.add(new StationConnectionChecker(routesBunch, dep, arr));

        return tasksToExecute;
    }

    // remove route id to get just stations ids
    private String removeRouteId(String s) {
        return s.substring(s.indexOf(" "));
    }

}
