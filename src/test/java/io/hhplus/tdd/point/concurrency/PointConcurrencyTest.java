package io.hhplus.tdd.point.concurrency;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.hhplus.tdd.common.pagination.PageRequest;
import io.hhplus.tdd.point.domain.PointHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.UserPointRepository;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private PointService pointService;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("10개의 스레드가 동시에 포인트 충전 요청을 보내도 잔액과 내역이 일관성을 유지한다.")
    void givenConcurrentAccess_whenChargeUserPoint_thenAllConsistent() throws InterruptedException {
        // given
        long userId = 0L;
        userPointRepository.save(new UserPoint(userId, 0L, System.currentTimeMillis()));

        int threadCount = 10;
        long amount = 500L;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        // when
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        CopyOnWriteArrayList<Throwable> failures = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            threadPool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    pointService.chargeUserPoint(userId, amount);
                } catch (Throwable t) {
                    failures.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();

        threadPool.shutdown();

        // then
        assertThat(failures).isEmpty();

        UserPoint userPoint = userPointRepository.findById(userId);
        assertThat(userPoint.point()).isEqualTo(amount * threadCount);

        long useCount = pointHistoryRepository.findAllByUserId(userId, new PageRequest(0, threadCount)).stream()
                .filter(history -> history.type() == TransactionType.CHARGE)
                .count();
        assertThat(useCount).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("10개의 스레드가 동시에 포인트 사용 요청을 보내도 잔액과 내역이 일관성을 유지한다.")
    void givenConcurrentAccess_whenUseUserPoint_thenAllConsistent() throws InterruptedException {
        // given
        long userId = 1L;
        userPointRepository.save(new UserPoint(userId, 5_000L, System.currentTimeMillis()));

        int threadCount = 10;
        long amount = 500L;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        // when
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        CopyOnWriteArrayList<Throwable> failures = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            threadPool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    pointService.useUserPoint(userId, amount);
                } catch (Throwable t) {
                    failures.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();

        threadPool.shutdown();

        // then
        assertThat(failures).isEmpty();

        UserPoint userPoint = userPointRepository.findById(userId);
        assertThat(userPoint.point()).isEqualTo(0L);

        long useCount = pointHistoryRepository.findAllByUserId(userId, new PageRequest(0, threadCount)).stream()
                .filter(history -> history.type() == TransactionType.USE)
                .count();
        assertThat(useCount).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("충전/사용이 섞여서 동시에 실행되어도 잔액과 내역이 일관성을 유지한다.")
    void givenMixedConcurrentAccess_whenChargeAndUseUserPoint_thenAllConsistent() throws InterruptedException {
        // given
        long userId = 2L;
        long initAmount = 5_000L;
        userPointRepository.save(new UserPoint(userId, initAmount, System.currentTimeMillis()));

        int useThreadCount = 10;
        int chargeThreadCount = 10;
        int threadCount = useThreadCount + chargeThreadCount;
        long amount = 200L;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        // when
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        List<Runnable> tasks = new ArrayList<>(threadCount);
        for (int i = 0; i < useThreadCount; i++) {
            tasks.add(() -> pointService.useUserPoint(userId, amount));
        }
        for (int i = 0; i < chargeThreadCount; i++) {
            tasks.add(() -> pointService.chargeUserPoint(userId, amount));
        }
        Collections.shuffle(tasks, new Random(0));

        CopyOnWriteArrayList<Throwable> failures = new CopyOnWriteArrayList<>();

        for (Runnable task : tasks) {
            threadPool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    task.run();
                } catch (Throwable e) {
                    failures.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();

        threadPool.shutdown();

        // then
        assertThat(failures).isEmpty();

        UserPoint userPoint = userPointRepository.findById(userId);
        assertThat(userPoint.point()).isEqualTo(initAmount);

        long useCount = pointHistoryRepository.findAllByUserId(userId, new PageRequest(0, threadCount)).stream()
                .filter(history -> history.type() == TransactionType.USE)
                .mapToLong(PointHistory::amount)
                .sum();
        assertThat(useCount).isEqualTo(useThreadCount * amount);

        long chargeCount = pointHistoryRepository.findAllByUserId(userId, new PageRequest(0, threadCount)).stream()
                .filter(history -> history.type() == TransactionType.CHARGE)
                .mapToLong(PointHistory::amount)
                .sum();
        assertThat(chargeCount).isEqualTo(chargeThreadCount * amount);
    }
}
