package org.hbrs.lzu;

import java.util.Scanner;
import java.util.UUID;

public class CLI {
    private final RuntimeEnvironment rte;

    public CLI(RuntimeEnvironment rte) {
        this.rte = rte;
    }
    public void delegateCommand(String command, String option) throws Exception {
        switch (command) {
            case "deploy":
                new DeployCommand(this.rte, option).execute();
                break;
            case "start":
                UUID compId = UUID.fromString(option); // Assuming option is componentId
                new StartCommand(this.rte, compId).execute();
                break;
            // Implement cases for other commands
            case "stop":
                UUID id = UUID.fromString(option); // Assuming option is componentId -> IllegalArgument
                new StopCommand(this.rte, id).execute();
                break;
            case "delete":
                UUID uuid = UUID.fromString(option);
                new DeleteCommand(this.rte, uuid).execute();
                System.out.println("Component " + uuid + " deleted!");
                break;
            case "help":
                System.out.println("You can either stop the runtime ('exit') or\n- deploy\n- start \n- stop\n- delete\n a component.");
                break;
            default:
                System.out.println("Something wrong with your command...");
        }

    }

    public static void main(String[] args) {
        RuntimeEnvironment rte = RuntimeEnvironment.getInstance();
        CLI cli = new CLI(rte);

        System.out.println("Enter a command.");
        Scanner scanner = new Scanner(System.in);
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
            try {
                cli.delegateCommand(input[0], option);
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
            }
        }
    }
}

