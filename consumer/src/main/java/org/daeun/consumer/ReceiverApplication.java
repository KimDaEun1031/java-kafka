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

        System.out.print("name : ");
        String name = bufferedReader.readLine();

        bufferedReader.close();

        receive(name, group, topic);
    }

    private static void receive(String name, String group, String topic) {
        Receiver receiver = new Receiver(name, group, topic);

        System.out.println("polling...");
        while (true) {
            receiver.poll();
        }

        //		receiver.close();
    }
}
