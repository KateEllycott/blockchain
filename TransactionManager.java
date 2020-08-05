package blockchain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.Scanner;

public class TransactionManager implements Runnable {
    private static final Path TRANSACTION_FILE = Paths.get("messages.txt");

    private final BlockChain blockChain;
    private KeyPairGenerator keyGen;
    private SecureRandom random;
    private KeyPair pair;
    private PrivateKey priv;
    private static PublicKey pub;
    private static Signature dsa;

    public TransactionManager(BlockChain blockChain) {
        this.blockChain = blockChain;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            pair = keyGen.generateKeyPair();
            priv = pair.getPrivate();
            pub = pair.getPublic();
            dsa = Signature.getInstance("SHA1withDSA", "SUN");
            dsa.initSign(priv);
        }
        catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try(BufferedReader reader = Files.newBufferedReader(TRANSACTION_FILE, StandardCharsets.UTF_8)) {
            String transactionData = reader.readLine();
            while(!(blockChain.isMiningOver()) && transactionData != null) {
                Scanner scanner = new Scanner(transactionData);
                String sender = scanner.next();
                long cv = scanner.nextLong();
                String recipient = scanner.next();
                Transaction transaction = new Transaction(sender, cv, recipient);
                blockChain.addTransaction(transaction);
                transactionData = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Can't open the message file or thread has been interrupted");
        }
    }

    public static Transaction signTransaction(Transaction transaction) {
        try {
            byte[] transactionBytes = transaction.getTransactionBytes();
            dsa.update(transactionBytes);
            transaction.setSignature(dsa.sign());
            transaction.setPublicKey(pub.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transaction;
    }
}
