import java.util.Collections;       
import java.util.LinkedList;       
import java.util.Queue;       
import java.util.Scanner;      
import java.util.Stack;       

public class StackJack21{

    static Stack<Cards> deck = new Stack<>();
    static Queue<Player> players = new LinkedList<>();
    static Scanner input = new Scanner(System.in);
    static Player dealer;                                       
    static boolean gameEndedByAutoWin = false;                              
    static boolean swapped = false;
    static final int MAX_PLAYERS = 5; 

    static class Cards{
        String suit;
        String rank;
        int value;

        public Cards(String suit, String rank, int value){
            this.suit = suit;
            this.rank = rank;
            this.value = value;
        }

        public String toString(){
            return rank + " of " + suit;
        }
    }

    static class Player{
        String name;
        LinkedList<Cards> hand;
        boolean troubleCard = false;                    
        boolean luckyLad = false;                       

        public Player(String name){
            this.name = name;
            this.hand = new LinkedList<>();
        }

        public int calcHandValue(){     
            int total = 0;
            int aceCount = 0;

            for(Cards card : hand){
                total += card.value;
                if(card.rank.equals("Ace")) aceCount++;    
            }

            while(total > 21 && aceCount > 0){           
                total -= 10;
                aceCount--;
            }
            return total;
        }

    public void showHand(boolean showAll){
        System.out.println(name + "'s hand:");
        for(int i = 0; i < hand.size(); i++){
            if(!showAll && i == 1 && name.equals("Dealer")){    
                System.out.println("[Hidden Card]");
                }else{
                System.out.println(" - " + hand.get(i));
                }
            }
            if(showAll || !name.equals("Dealer"))
                System.out.println("Total value: " + calcHandValue());
            System.out.println();
        }
    }

    static void initializePlayers(int mode){
        if(mode == 1){          
            int numPlayers = -1;                                   
            while (numPlayers <= 1 || numPlayers > MAX_PLAYERS) {
                System.out.printf("Enter number of players (2-%d): ", MAX_PLAYERS);
                try {
                    numPlayers = getValidatedPositiveInt();
                    if (numPlayers <= 1 || numPlayers > MAX_PLAYERS) {
                        System.out.println("[Error]: Number of players must be 2 - " + MAX_PLAYERS + ".");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage()); 
                }
            }
               
            for(int i = 1; i <= numPlayers; i++){
                String name;
                
                System.out.print("Enter name for Player " + i + ": ");         
                name = input.nextLine();
                if (name.isEmpty()) {
                    System.out.println("[Error]: Input cannot be empty. Please try again.");
                    i -=1;
                } else {
                    players.offer(new Player(name));  
                }
            }
        }else{
            players.offer(new Player("User"));                         
        }
    }
    static void initializeDeck(){
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for(String suit : suits){               
            for(String rank : ranks){
                int value;
                if(rank.equals("Ace")) value = 11;
                else if(rank.equals("Jack") || rank.equals("Queen") || rank.equals("King")) value = 10;
                else value = Integer.parseInt(rank);
                deck.push(new Cards(suit, rank, value));
            }
        }
    Collections.shuffle(deck);      
    }       


    static void dealInitialCards() {                
        for (Player p : players) {
            p.hand.add(deck.pop());
            p.hand.add(deck.pop());

        }
        dealer = new Player("Dealer");              
        dealer.hand.add(deck.pop());
        dealer.hand.add(deck.pop());
    }

    static void showInitialHands(){
        System.out.println("\n--- Dealer's Hand ---");
        dealer.showHand(false);

        for(Player p : players){
            System.out.println("--- " + p.name + "'s Hand ---");
            p.showHand(true);
        }
    }

    static boolean handleJackSpecial(Player p, Cards drawnCard){
        if(!drawnCard.rank.equals("Jack")) return false;   

        switch(drawnCard.suit){
            case "Clubs":
                System.out.println("You drew Jack of Clubs - Lucky Lad! You instantly WIN!");
                p.hand.add(drawnCard);
                p.luckyLad = true;
                gameEndedByAutoWin = true;
                return true;

            case "Diamonds":
                System.out.println("You drew Jack of Diamonds - Trouble Card! Its value becomes 15.");
                Cards troubleCard = new Cards(drawnCard.suit, drawnCard.rank, 15);
                p.hand.add(troubleCard);
                p.troubleCard = true;
                p.showHand(true);
                break;

            case "Hearts":
                String choice = "";
                System.out.println("You drew Jack of Hearts - Another Chance! You can accept this card (Value 10) or reject it (counts as HIT but card discarded).");
                while(true){
                    System.out.print("Accept card? [Y/N]: ");
                    choice = input.nextLine().trim().toUpperCase();

                    if(isInputEmpty(choice)){
                        System.out.println("[Error]: Input cannot be empty.");
                        continue;
                    }

                    if(choice.equals("Y")){
                        System.out.println("Card accepted.");
                        p.hand.add(drawnCard);
                        p.showHand(true);
                        break;
                    } else if(choice.equals("N")){
                        System.out.println("Card rejected. HIT counts but card is discarded");
                        break;
                    } else{
                        System.out.println("[Error]: Invalid input. Please enter Y or N.");
                    }
                }
                break;
            case "Spades":
                System.out.println("You drew Jack of Spades - Swap Card! Your Hand will be swapped with the Dealer's Hand");
                LinkedList<Cards> temp = p.hand;
                p.hand = dealer.hand;
                dealer.hand = temp;
                p.hand.add(drawnCard);
                System.out.println("Hands swapped!");
                p.showHand(true);
                swapped = true;
                break;
        }
        return false;
    }

    //kay joshua
    static void playPlayerTurns(){
        for(Player p : players){
            if(gameEndedByAutoWin) break;                                  
            String choice = "";
            while (true){                                                                           
                System.out.print(p.name + ", do you want to [H]it or [S]tand? ");
                choice = input.nextLine().trim().toUpperCase();    
                         //gagawing uppercase ung input
                if (isInputEmpty(choice)) {
                    System.out.println("[Error]: Input cannot be empty.");
                    continue;
                }
                if(!choice.equals("H") && !choice.equals("S")) {             
                    System.out.println("[Error]: Invalid input. PLease enter 'H' or 'S'.");
                    continue;//continue the loop
                }
                break;
            }

            if(choice.equals("H")){                                       
                
                Cards drawnCard = deck.pop();                              

                boolean autoWin = handleJackSpecial(p, drawnCard);
                if(autoWin){
                    break;
                }   

                if(!(drawnCard.rank.equals("Jack"))){       
                    if(!p.hand.contains(drawnCard)){        
                        p.hand.add(drawnCard);
                        p.showHand(true);
                    }
                }else{
                    System.out.println();
                }


                if(p.calcHandValue() > 21){
                    System.out.println(p.name + " Busted!\n");
                }

            }else {
                System.out.println(p.name + " stands.\n");
            }
        }
    }

    
    static void playDealerTurn() {
        if(gameEndedByAutoWin){
            System.out.println("Dealer turn skipped due to a player's auto win.");
            return;
        }
        
        if (swapped == true){
            System.out.println("--- Dealer Reveals Card ---");
        dealer.showHand(true);
        }
        else{
            System.out.println("--- Dealer Reveals Hidden Card ---");
        dealer.showHand(true);
        }


        if(dealer.calcHandValue() < 17){
            System.out.println("Dealer decides to hit...");
            
            Cards drawnCard = deck.pop();
            System.out.println("Dealer drew: " + drawnCard);

            if(drawnCard.rank.equals("Jack")){
                switch(drawnCard.suit){
                    case "Clubs":
                        System.out.println("Dealer drew Jack of Clubs - Lucky Lad! Dealer instantly WINS!");
                        dealer.luckyLad = true;
                        gameEndedByAutoWin = true;
                        dealer.hand.add(drawnCard);
                        dealer.showHand(true);
                        return;
                    case "Diamonds": 
                        System.out.println("Dealer drew Jack of Diamonds - Trouble Card! Value of drawn card is now 15.");
                        Cards troubleCard = new Cards(drawnCard.suit, drawnCard.rank, 15);
                        dealer.hand.add(troubleCard);
                        dealer.troubleCard = true;
                        break;
                    case "Hearts":
                        if (dealer.calcHandValue() <= 11){
                            System.out.println("Dealer drew Jack of Hearts - Another Chance! Dealer accepts the card.");
                            dealer.hand.add(drawnCard);
                        }
                        else{
                            System.out.println("Dealer drew Jack of Hearts - Another Chance! Declines the card.");
                        }
                        dealer.showHand(true);
                        break;
                    case "Spades":
                        System.out.println("Dealer drew Jack of Spades - Swap Card! Dealer cannot swap with other player.");
                        dealer.hand.add(drawnCard);
                        break;
                }
            } else {
                dealer.hand.add(drawnCard);
            }
            dealer.showHand(true);

            if(dealer.calcHandValue() > 21){
                System.out.println("Dealer BUSTED!\n");
            }

        }else{
            System.out.println("Dealer stands.");
        }
    }

        static void showResults(){
            if(gameEndedByAutoWin){
                System.out.println("\n--- Game Ended by Auto Win! ---");
                for(Player p : players){
                    if (p.luckyLad){
                        System.out.println(p.name + " WINS INSTANTLY with Jack of Clubs!");
                    }
                }
                return;
            }


            int dealerValue = dealer.calcHandValue();
            System.out.println("\n--- Final Results ---");
            for(Player p : players){
                int playerValue = p.calcHandValue();
                System.out.print(p.name + ": ");
                if(playerValue > 21){
                    System.out.println("BUSTED!");
                } else if(dealerValue > 21 || playerValue > dealerValue){
                    System.out.println("WINS!");
                } else if(playerValue == dealerValue){
                    System.out.println("PUSH (TIE)");
                } else{
                    System.out.println("LOSES!");
                }
            }
        }


    public static boolean askToPlayAgain() {
        while (true) {
            String choice = "";
            System.out.print("\nWould you like to play again? [Y/N]: ");
            choice = input.nextLine().toUpperCase().trim();
            if (isInputEmpty(choice)) {
                System.out.println("[Error] Input cannot be empty.");
                continue;
            }
            
            if (choice.equals("Y")) {
                deck.clear();
                players.clear();
                gameEndedByAutoWin = false;
                return true;
            } else if (choice.equals("N")) {
                return false;
            } else {
                System.out.println("[Error]: Invalid input. Please enter 'Y' or 'N'.");
            }
        }
    }


    public static int getValidatedPositiveInt() {
        String inputStr = input.nextLine();  

        if (isInputEmpty(inputStr)) {
            throw new IllegalArgumentException("[Error]: Input cannot be empty.");
        }
        try {
            int num = Integer.parseInt(inputStr.trim());
            if (num < 0) {
                throw new IllegalArgumentException("[Error]: Please enter a positive integer.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[Error]: Invalid input. Please enter a valid integer.");
        }
    }

    public static boolean isInputEmpty(Object input) {
        if (input == null) {
            return true; 
        }
        String str = input.toString().trim(); 
        return str.isEmpty();
    }
    

    public static void main(String[] args) {
        System.out.println("==================================================================================");
        System.out.println("\t             WELCOME TO STACKJACK21 - Blackjack with a Twist!                     ");
        System.out.println("==================================================================================");

        boolean keepPlaying = true;

        while (keepPlaying) {
            initializeDeck();

            System.out.println("\nChoose Game Mode:");
            System.out.println("1. Multiplayer");
            System.out.println("2. Solo vs Dealer");
            int mode = 0;
            while (true) {
                System.out.print("Enter choice (1 or 2): ");
                try {
                    mode = getValidatedPositiveInt();
                    if (mode != 1 && mode != 2) {
                        System.out.println("[Error]: Please enter either 1 or 2.");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            initializePlayers(mode);
            dealInitialCards();
            showInitialHands();
            playPlayerTurns();
            playDealerTurn();
            showResults();
            keepPlaying = askToPlayAgain();
        }
        
        System.out.println("\nThank you for playing StackJack 21! Sayonara!!");
    }
}