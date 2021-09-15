package org.daeun.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

public class ReceiverApplication {

	public static void main(String[] args) throws IOException {
		System.out.println("kafka receiver start");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		args = new String[2];

		System.out.print("group : ");
		args[0] = bufferedReader.readLine();

		System.out.print("topic : ");
		args[1] = bufferedReader.readLine();
		bufferedReader.close();

		Receiver receiver = new Receiver(args[1], args[0]);
//		receiver.receiverProps(args[0]);

		System.out.println("polling...");
		while (true) {
			receiver.poll();
		}

//		receiver.close();
	}
}
