package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args)throws InterruptedException {

        BlockChain chain = new BlockChain(14, 0);
        TransactionManager transactionManager = new TransactionManager(chain);
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService minerExecutor = Executors.newFixedThreadPool(threads * 2);
        ExecutorService transactionExecutor = Executors.newSingleThreadExecutor();
        transactionExecutor.execute(transactionManager);
        List<Miner> miners = new ArrayList<>();
        for(int i = 0; i < threads;  i++) {
            miners.add(new Miner(chain, i));
        }
        for(Miner miner : miners) {
            minerExecutor.execute(miner);
        }
        minerExecutor.shutdown();
        transactionExecutor.shutdown();
        minerExecutor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println(chain);
    }
}
