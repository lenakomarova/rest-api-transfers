package org.backend.test.service;

import lombok.extern.slf4j.Slf4j;
import org.backend.test.events.TransferEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
public class MoneyTransfersService {
    private final Map<String, LinkedList<TransferEvent>> transferEvents = new HashMap<>();

    public BigDecimal getBalance(String accountId) {
        LinkedList<TransferEvent> transfers = transferEvents.get(accountId);
        if (transfers == null) {
            return BigDecimal.ZERO;
        }
        return transfers.getLast().getCurrentBalance();
    }


    private static class MoneyTransfersServiceHolder {
        private static final MoneyTransfersService INSTANCE = new MoneyTransfersService();
    }

    private MoneyTransfersService() {
    }

    public static MoneyTransfersService getInstance() {
        return MoneyTransfersServiceHolder.INSTANCE;
    }


}
