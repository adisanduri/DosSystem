package com.sanduri.projects.dossystem;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import java.util.concurrent.CancellationException;

public class MultiHttpClientConnThread extends Thread {
    private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;
    private final int id;

    public MultiHttpClientConnThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpget = httpget;
        this.id = id;
    }

    /**
     * Executes the GetMethod and prints some status information.
     */
    @Override
    public void run() {
        try {
            System.out.println(id + " - about to get something from " + httpget.getURI());
            CloseableHttpResponse response = httpClient.execute(httpget, context);

            try {
                synchronized (this) {

                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        byte[] bytes = EntityUtils.toByteArray(entity);
                        System.out.println(bytes.length);
                    }
                }
            } finally {

                httpget.releaseConnection();
                response.close();
            }
        }
        catch (ConnectionShutdownException e) {
            System.out.println(e.getMessage());
            //TODO:
        }
        catch (CancellationException e) {
            System.out.println(e.getMessage());
            //TODO:
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO:
        }
    }

}
