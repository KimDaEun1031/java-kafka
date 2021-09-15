package org.daeun.producer;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SenderApplication {

    public static void main(String[] args) throws InterruptedException, IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        Sender sender = new Sender();

        args = new String[1];

        System.out.print("topic : ");
        args[0] = bufferedReader.readLine();
        bufferedReader.close();

        for (int i = 0; i < 1000; i++) {
            sender.sendMessage(args[0], args[0], LocalDateTime.now().toString());
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        sender.close();
    }
}
