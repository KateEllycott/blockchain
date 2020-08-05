package blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

    public String hash;
    public String previousHash;
    private int id;
    private long timeStamp;
    private long nonce;
    private long generationTime;
    private String minerName;
    private long magicNumber;
    private final List<Transaction> blockTransactions = new ArrayList<>();

    public Block(int id, String previousHash) {
        this.id = id;
        this.previousHash = previousHash;
        timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public void addTransaction(Transaction transaction) {
        blockTransactions.add(transaction);
    }

    private String getBlockTransactions() {
        if(blockTransactions.isEmpty()) {
            return "no blockTransactions";
       }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < blockTransactions.size(); i++) {
            builder.append(blockTransactions.get(i));
            if(i != blockTransactions.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public List<Transaction> getMessages() {
        return blockTransactions;
    }

    public String calculateHash() {
        String hash = StringUtil.applySha256(previousHash + id + timeStamp);
        return hash;
    }

    public void setMagicNumber(long magicNumber) {
        this.magicNumber = magicNumber;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public void setGenerationTime(long generationTime) {
        this.generationTime = generationTime;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public void setMinerName(String minerName) {
        this.minerName = minerName;
    }

    public String getMinerName() {
        return minerName;
    }

    @Override
    public String toString() {
        return String.format(
                "Block:" +
                "\nCreated by miner # %s" +
                "\n%s gets %d VC" +
                "\nId: %d" +
                "\nTimestamp: %d" +
                "\nMagic number: %d" +
                "\nHash of the previous block:" +
                "\n%s" +
                "\nHash of the block:" +
                "\n%s" +
                "\nBlock data: \n%s" +
                "\nBlock was generating for %d seconds" +
                "\nN was increased to %d", getMinerName(), blockTransactions.get(blockTransactions.size()-1).getRecipientName(), blockTransactions.get(blockTransactions.size()-1).getCv(), getId(), getTimeStamp(), magicNumber,
                getPreviousHash(), getHash(), getBlockTransactions(), getGenerationTime(), getNonce());
    }
}

