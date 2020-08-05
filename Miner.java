package blockchain;

import java.time.Duration;
import java.time.Instant;

public class Miner implements Runnable {

    private int id;
    private BlockChain chain;
    private String currentNonce;

    public Miner(BlockChain chain, int id) {
        this.chain = chain;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void createBlockAndApplyToChain() {
        Block block  = chain.newBlock(getId());
        Instant start = Instant.now();
        block.setHash(proofOfWork(block));
        Instant end = Instant.now();
        Duration generationTime = Duration.between(start, end);
        block.setGenerationTime(generationTime.toSeconds());
        try {
            if(chain.getHeadId() >= block.getId()) {
                return;
            }

            chain.addAndValidate(block);
        } catch (RuntimeException e) {
            System.out.println("Exception");
        }
    }

    private String proofOfWork(Block block) {
        block.setNonce(currentNonce.length());
        long magicNumber = 0;
        boolean nonceFound = false;
        String nonceHash = "";
        String message = block.getPreviousHash() + block.getId() + block.getTimeStamp();

        while (!nonceFound) {
            nonceHash = StringUtil.applySha256(message + magicNumber);
            nonceFound = nonceHash.substring(0, currentNonce.length()).equals(currentNonce);
            magicNumber++;
        }

        block.setMagicNumber(magicNumber);
        return nonceHash;
    }

    @Override
    public void run() {
        while (!chain.isMiningOver()) {
            currentNonce = chain.getNonceKey();
            createBlockAndApplyToChain();
        }
    }
}
