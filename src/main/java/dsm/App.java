package dsm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

class Client implements Runnable{
    int interval;
    int count;
    ArrayList list;
    public Client(int interval, int count, ArrayList<String> list){
        this.interval = interval;
        this.count = count;
        this.list = list;
    }
    public void run(){
        try{

            for ( int i = 0; i < count; i++ ){
                int c =(int)( Math.random()*list.size() );
                String addr = "http://" + list.get(c) + ":1334/st";
                CloseableHttpClient httpClient = HttpClients.createDefault();
                RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(30000)
                    .setSocketTimeout(30000)
                    .build();
                HttpGet httpGet = new HttpGet(addr);
                httpGet.setConfig(config);

                long t1 = System.currentTimeMillis();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                if ( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    long t2 = System.currentTimeMillis();
                    System.out.println( t2 - t1 );
                }else{
                    System.out.println( "err" );
                }

                Thread.sleep(interval);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

public class App 
{

    public static void main( String[] args )
    {

        int p = 1;
        int interval = 1000;
        int count = 1;
        ArrayList<String> list = new ArrayList<String>();

        try{
            p = Integer.parseInt( args[0] );
            interval = Integer.parseInt( args[1] );
            count = Integer.parseInt( args[2] );

            File file = new File("ips");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            while( line != null ){
                list.add( line );
                line = br.readLine();
            } 
        }catch(Exception e){
            e.printStackTrace();
        };
        ExecutorService es = Executors.newFixedThreadPool(p);


        try{
            for ( int i = 0; i < p; i++ ){
                Client client = new Client(interval,count,list);
                es.submit(client);
                Thread.sleep(interval / p);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
