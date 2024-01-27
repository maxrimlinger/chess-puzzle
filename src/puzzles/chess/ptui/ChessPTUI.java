package puzzles.chess.ptui;

import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.*;
import java.util.Scanner;

/**
 * A text UI to play a chess puzzle game
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class ChessPTUI implements Observer<ChessModel, String> {
    private ChessModel model;

    /**
     * initializes the model and loads a file
     *
     * @param filename name of file to be loaded
     */
    public void init(String filename) {
        this.model = new ChessModel();
        this.model.addObserver(this);
        File file = new File(filename);
        this.model.load(file);
        displayHelp();
    }

    /**
     * Shows the updated board after any changes the model might have made. Also displays a status message.
     *
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel model, String data) {
        System.out.println(data);
        System.out.println(model);
    }

    /**
     * Show the commands that can be used to interact with the PTUI
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * Runs the main program loop. Takes commands and calls the appropriate methods in the model.
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                }  else if (words[0].startsWith("l")) {
                    File file = new File(words[1]);
                    this.model.load(file);
                } else if (words[0].startsWith("s")) {
                    model.select(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                } else if (words[0].startsWith("r")) {
                    model.reset();
                } else if (words[0].startsWith("h")) {
                    model.hint();
                } else {
                    System.out.println("Invalid Command");
                }
            }
        }
    }

    /**
     * Starts up the PTUI
     *
     * @param args filename of the initial chess puzzle txt file
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            ChessPTUI ptui = new ChessPTUI();
            ptui.init(args[0]);
            ptui.run();
        }
    }
}

