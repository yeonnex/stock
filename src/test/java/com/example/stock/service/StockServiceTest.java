package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {
    public static final long PRODUCT_ID = 1L;
    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    void before() {
        Stock stock = new Stock(PRODUCT_ID, 100L);
        stockRepository.save(stock);
    }

    @AfterEach
    void after() {
        stockRepository.deleteAll();
    }

    @Test
    void stock_decrease() {
        stockService.decrease(PRODUCT_ID, 1L);
        Stock stock = findStockByProductId(PRODUCT_ID);

        assertThat(stock.getQuantity()).isEqualTo(99);
    }

    @Test
    void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    String threadName = Thread.currentThread().getName();
                    System.out.printf("[%s] running ...\n", threadName);
                    stockService.decrease(PRODUCT_ID, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = findStockByProductId(PRODUCT_ID);
        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    private Stock findStockByProductId(Long productId) {
        return stockRepository.findById(productId).orElseThrow();
    }
}