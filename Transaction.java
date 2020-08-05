package blockchain;

import java.io.UnsupportedEncodingException;

public class Transaction {

    private long id;
    private String senderName;
    private long cv;
    private String recipientName;
    private byte[] signature;
    private byte[] publicKey;

    public Transaction(String senderName, long cv, String recipientName ) {
        this.senderName = senderName;
        this.cv = cv;
        this.recipientName = recipientName;
    }

    public void setTransactionId(long id) {
        this.id = id;
    }

    public long getTransactionId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public long getCv() {
        return cv;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getTransactionBytes() {
        byte[] messageBytes = new byte[0];
        try {
            messageBytes = (
                    getSenderName() +
                    + getCv()
                    + getRecipientName()
            ).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return messageBytes;
    }

    @Override
    public String toString() {
        return getSenderName() + " sent " + getCv() + " VC to " + getRecipientName();
    }
}
