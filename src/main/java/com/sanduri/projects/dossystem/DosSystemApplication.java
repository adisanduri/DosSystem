package com.sanduri.projects.dossystem;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DosSystemApplication {

    private static int maxPerRoute = 2;
    private static int numberOfClients = 10;
    private static int maxConnections = maxPerRoute*numberOfClients;

    // TODO: Change it to random
    private static int numberOfThreads = 20;
    private static final int DEFAULT_KEEP_ALIVE_MILLISECONDS = (5 * 1000);

    public static void main(String[] args) throws InterruptedException, IOException {
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();

        poolingConnManager.setMaxTotal(maxConnections);
        poolingConnManager.setDefaultMaxPerRoute(maxPerRoute);

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(poolingConnManager)
                .build();

        try {

            int clientId = 1;
            HttpGet get1 = new HttpGet("http://localhost:8080/?clientId="+clientId);

            // create a thread for each URI
            MultiHttpClientConnThread[] threads = new MultiHttpClientConnThread[numberOfThreads];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new MultiHttpClientConnThread(client, get1, clientId);
            }

            // start the threads
            for (int i = 0; i < threads.length; i++) {
                threads[i].start();
            }

            // join the threads
            for (int i = 0; i < threads.length; i++) {
                threads[i].join(5000);
            }
        }
        finally {
            client.close();
        }
    }

}
