package org.hbrs.lzu;

import java.util.Scanner;

public class CLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a command.");
        while (scanner.hasNext()) {
            String[] input = scanner.nextLine().toLowerCase().split("\\s");
            if (input[0].equals("exit")) {
                System.out.println("Bye!");
                return;
            }
            String option = "";
            if (input.length >= 2) {
                option = input[1];
            }
            switch (input[0]) {
                case "add":
                case "start":
                case "stop":
                    delegateCommand(input[0], option);
                    continue;
                case "help":
                default:
                    System.out.println("You can either stop the runtime ('exit') or\n- deploy\n- start \n- stop\n- delete\n a component.");

            }
        }
    }

    public static void delegateCommand(String command, String option) {
        if (option.isEmpty()) {
            System.out.println("Specify either the path or the id of the component to add/start/stop...");
            return;
        }

    }
}
