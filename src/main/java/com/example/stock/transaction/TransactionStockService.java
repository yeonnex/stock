package com.example.stock.transaction;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionStockService {
    private final StockService stockService;

    public void decrease(Long id, Long quantity) {
        startTransaction();
        stockService.decrease(id, quantity);
        endTransaction();
    }

    private void endTransaction() {
    }

    private void startTransaction() {

    }
}
