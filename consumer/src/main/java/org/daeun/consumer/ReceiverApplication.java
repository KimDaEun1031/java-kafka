package org.daeun.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReceiverApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("kafka receiver start");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("group : ");
        String group = bufferedReader.readLine();

        System.out.print("topic : ");
        String topic = bufferedReader.readLine();

        bufferedReader.close();

        receive(group, topic);
    }

    private static void receive(String group, String topic) {
        Receiver receiver = new Receiver(group, topic);

        System.out.println("polling...");
        while (true) {
            receiver.poll();
        }

        //		receiver.close();
    }
}
