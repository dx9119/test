package org.ukhanov;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class JustTest {

    public void main(String[] args) throws ExecutionException, InterruptedException {
        some1();
    }

    private void some1() {
        Phaser phaser = new Phaser(1); // 1 регистрируем сразу основной поток как участника
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        List<String> robots = List.of("r1", "r2", "r3");

        // Регистрируем роботов как других участников, у них своя логика
        for (String robot : robots) {
            phaser.register();

            pool.submit(() -> {
                try {
                    phaser.arriveAndAwaitAdvance();
                    System.out.println(robot + " пришел на точку А");

                    phaser.arriveAndAwaitAdvance();
                    System.out.println(robot + " пришел на точку Б");

                    phaser.arriveAndAwaitAdvance();
                    System.out.println(robot + " пришел на точку С");

                } finally {
                    phaser.arriveAndDeregister(); // робот завершает участие
                }
            });
        }

        //продолжаем логику главного потока, Phaser требует что бы все потоки прошли фазы, допроходим их для главного.
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndDeregister();
    }

}
