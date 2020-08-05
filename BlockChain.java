package blockchain;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;


class BlockChain {

    private final long size;
    private volatile int nonce;
    private volatile long transactionIdCounter = 0L;
    public static ArrayList<Block> chain = new ArrayList<>();
    private volatile String nonceKey;
    private volatile boolean isMiningOver = false;
    private ConcurrentLinkedDeque<Transaction> transactions = new ConcurrentLinkedDeque<>();

    public BlockChain(long size, int nonce) {
        this.size = size;
        this.nonce = nonce;
        calculateNonceKey();
    }

    private void calculateNonceKey() {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < nonce; i++) {
            builder.append("0");
        }
        this.nonceKey = builder.toString();
    }

    public synchronized Block newBlock(int minerId) {
        Block block;
        int count = chain.size();
        String previousHash = "0";
        if (count > 0) {
            previousHash = blockChainHash();
        }
        block = new Block(count, previousHash);
        block.setMinerName("miner" + minerId);
        return block;
    }

    public synchronized void addAndValidate(Block block) {
        if(getHeadId() == size) {
            isMiningOver = true;
            return;
        }

        if(block.getId() <= getHeadId()) {
            return;
        }

        Block current = block;
        if(chain.size() > 1) {
            for (int i = chain.size() - 1; i >= 0; i--) {
                Block b = chain.get(i);
                if (b.getHash().equals(current.getPreviousHash())
                        && (current.getId() - b.getId()) == 1) {
                    current = b;
                } else {
                   return;
                }
            }
        }

        if (!validateProofOfWork(block)) {
            return;
        }

        fillBlockWithTransactions(block);

        this.chain.add(block);


        /*if(block.getGenerationTime() < 5 && nonce < 32) {
            System.out.println("GENERATION TIME: " + block.getGenerationTime());
            nonce++;
            calculateNonceKey();

        } else if(block.getGenerationTime() > 60 && nonce > 0) {
            System.out.println("GENERATION TIME: " + block.getGenerationTime());
            nonce--;
            calculateNonceKey();
        }*/

        if(getHeadId() == size) {
            isMiningOver = true;
        }
    }

    public int getHeadId() {
        if(chain.size() == 0) {
            return -1;
        }
        else if (chain.size() == 1){
            return chain.get(0).getId();
        }
        return chain.get(chain.size() - 1).getId();
    }

    private boolean validateProofOfWork(Block block) {
        String hash = block.getHash();
        if(hash.substring(0, nonceKey.length()).equals(nonceKey)) {
            return true;
        }
        return false;
    }

    private void fillBlockWithTransactions(Block block) {
        Transaction transaction;
        for (int i = 0; i < 5; i++) {
            transaction = transactions.pollFirst();
            if(transaction != null && validateTransaction(block, transaction)) {
                transaction.setTransactionId(getTransactionIdCounterAndIncrement());
                block.addTransaction(TransactionManager.signTransaction(transaction));
            }
        }
        Transaction awardTransaction = new Transaction("blockchain", 100, block.getMinerName());
        awardTransaction.setTransactionId(getTransactionIdCounterAndIncrement());
        block.addTransaction(TransactionManager.signTransaction(awardTransaction));
    }

    long getTransactionIdCounterAndIncrement() {
        return transactionIdCounter++;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean isMiningOver() {
        return isMiningOver;
    }

    public String getNonceKey() {
        return nonceKey;
    }

    public String blockChainHash() {
        return  getHead().getHash();
    }

    public Block getHead() {
        if (chain.size() == 0) {
            throw new RuntimeException("No Block's have been added to chain...");
        } else {
            return chain.get(chain.size() - 1);
        }
    }

    private long getBalance(Block filledBlock, String senderName) {
        long balance = 100;
        for(Block block : chain) {
            for(Transaction t : block.getMessages()) {
                if(t.getSenderName().equals(senderName)) {
                    balance = balance - t.getCv();
                }
                if(t.getRecipientName().equals(senderName)) {
                    balance = balance + t.getCv();
                }
            }
        }
        if(!filledBlock.getMessages().isEmpty()) {
            for(Transaction t : filledBlock.getMessages()) {
                if(t.getSenderName().equals(senderName)) {
                    balance = balance - t.getCv();
                }
                if(t.getRecipientName().equals(senderName)) {
                    balance = balance + t.getCv();
                }
            }
        }
        return balance;
    }

    private boolean validateTransaction(Block block, Transaction transaction) {
        long cv = transaction.getCv();
        String sender = transaction.getSenderName();
        if(getBalance(block, sender) >= cv) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for(int i = 0; i < chain.size(); i++) {
            stringBuilder.append(chain.get(i));
            if(i < chain.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }
        return stringBuilder.toString();
    }
}