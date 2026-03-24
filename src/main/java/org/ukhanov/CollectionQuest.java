package org.ukhanov;

import java.util.*;
import java.util.concurrent.*;

public class CollectionQuest {


    public void main(String[] args) throws ExecutionException, InterruptedException {
        quest2();
    }

    //1
    private void quest1() throws InterruptedException, ExecutionException {
        List<String> names = new ArrayList<>(Arrays.asList("Anna", "Bob", "Clara", "David"));

        CopyOnWriteArrayList<String> concurentList = new CopyOnWriteArrayList<>(names);
        List<String> concurentListCopy =  Collections.synchronizedList(new ArrayList<>(names));

        Runnable task = () -> {
            concurentList.add(UUID.randomUUID().toString());
            concurentListCopy.add(UUID.randomUUID().toString());
        };

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<?> future = executorService.submit(task);
        Future<?> future2 = executorService.submit(task);
        Future<?> future3 = executorService.submit(task);

        future.get();
        future2.get();
        future3.get();

        executorService.shutdown();

        System.out.println(concurentList.size());
        concurentList.stream().forEach(System.out::println);
        System.out.println(concurentListCopy.size());
        concurentListCopy.stream().forEach(System.out::println);

    }

    //2
    private void quest2() throws ExecutionException, InterruptedException {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("player1", 100);
        scores.put("player2", 250);

        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>(scores);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Future<?> future = executorService.submit(() -> {
           concurrentHashMap.computeIfPresent("player1",( k,v)-> v+50);
        });
        Future<?> future2 = executorService.submit(() -> {
            concurrentHashMap.computeIfPresent("player1",(k,v)->v+50);
        });
        Future<?> future3 = executorService.submit(() -> {
            concurrentHashMap.computeIfPresent("player1",(k,v)->v+50);
        });

        future.get();
        future2.get();
        future3.get();
        executorService.shutdown();

        System.out.println(concurrentHashMap.get("player1"));
    }

    //3
    private void quest3(){
        Queue<String> tasks = new LinkedList<>();

    }
}
