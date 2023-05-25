package com.digitalasset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.daml.ledger.rxjava.DamlLedgerClient;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        System.out.println("Hello World!");
        String ledgerhost = "localhost";
        int ledgerport = 6865;

        // Create a client object to access services on the ledger.
        DamlLedgerClient client = DamlLedgerClient.newBuilder(ledgerhost, ledgerport).build();

        // Connects to the ledger and runs initial validation.
        client.connect();

        System.out.println("Connected.");

        try {
            client.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        System.out.println("Disconnected.");
    }
}
