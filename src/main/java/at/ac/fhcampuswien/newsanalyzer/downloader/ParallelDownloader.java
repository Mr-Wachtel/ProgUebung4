package at.ac.fhcampuswien.newsanalyzer.downloader;

import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader{ //Strategy Pattern
    @Override
    public int process(List<String> urls) {
        int count = 0;
        // Returns the number of processors available to the Java virtual machine
        int numWorkers = Runtime.getRuntime().availableProcessors();
        // Creates a thread pool that reuses a fixed number of threads
        ExecutorService pool = Executors.newFixedThreadPool(numWorkers);

        List<Callable<String>> callables = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++){ // create tasks dynamically
            int idx = i;
            Callable<String> task = () -> saveUrl2File(urls.get(idx)); // pass the async function as a lambda
            callables.add(task); // pool.submit returns Future objects -> add all Future objects to array
        }

        try {
            List<Future<String>> allFutures = pool.invokeAll(callables);
            for(Future<String> f : allFutures){
                String result = f.get();
                if(result != null)
                    count++;
            }
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        pool.shutdown();
        return count;
    }
}