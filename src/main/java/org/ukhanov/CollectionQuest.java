package org.ukhanov;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CollectionQuest {


    public void main(String[] args) throws ExecutionException, InterruptedException {
        quest4();
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
    private void quest3() throws ExecutionException, InterruptedException {
        Queue<String> tasks = new LinkedList<>();
        Queue<String> concurrentTasks = new ConcurrentLinkedQueue<>(tasks);

        for (int i=1; i<5; i++){
            Thread.ofVirtual()
                    .name("task-"+i)
                    .unstarted(() -> {
                        String name = Thread.currentThread().getName();
                        concurrentTasks.add(name);
                    })
                    .start();
        }

        concurrentTasks.stream().forEach(System.out::println);
    }

    //4
    private void quest4() throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
        List<Integer> storage = new ArrayList<>();

        Thread producer = Thread.ofVirtual().start(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //цикл помогает не завершать поток
        Thread consumer = Thread.ofVirtual().start(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    queue.drainTo(storage);
                    if (i == 10){
                        System.out.println("consumer: "+storage.toString());
                    }
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        producer.join();
        consumer.join();
    }



}
