package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;

/**
 * A GUI to play a chess puzzle using the ChessModel
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {
    /** the model handling the logic of the chess game */
    private ChessModel model;
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;
    /** the stage of the application */
    private Stage stage;
    /** the status label that gets updated throughout the game */
    private Label status;
    /** the current loaded file */
    private File file;
    /** the grid of buttons that makes up the chessboard */
    private GridPane chessboard;
    /** the main container of everything in the GUI */
    private BorderPane mainContainer;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    /** bishop icon */
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    /** pawn icon */
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    /** king icon */
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    /** knight icon */
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    /** queen icon */
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    /** rook icon */
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));

    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));

    /**
     * Initializes the model and prepares the filename
     */
    @Override
    public void init() {
        // get the file name from the command line
        file = new File(getParameters().getRaw().get(0));
        model = new ChessModel();
        model.addObserver(this);
    }

    /**
     * Creates all the GUI elements
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;

        mainContainer = new BorderPane();

        status = new Label("testing");
        BorderPane labelContainer = new BorderPane();
        labelContainer.setCenter(status);
        status.setAlignment(Pos.CENTER);
        mainContainer.setTop(labelContainer);

        chessboard = new GridPane();
        mainContainer.setCenter(chessboard);

        HBox buttonsContainer = new HBox();

        Button loadButton = new Button("Load");
        FileChooser chooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        currentPath += File.separator + "data";
        chooser.setInitialDirectory(new File(currentPath));
        loadButton.setOnAction(e -> {
            File fileToLoad = chooser.showOpenDialog(stage);
            if (fileToLoad != null) {model.load(fileToLoad);}
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> model.reset());
        Button hintButton = new Button("Hint");
        hintButton.setOnAction(e -> model.hint());
        buttonsContainer.getChildren().addAll(loadButton, resetButton, hintButton);
        buttonsContainer.setAlignment(Pos.CENTER);
        mainContainer.setBottom(buttonsContainer);

        model.load(file);
        Scene scene = new Scene(mainContainer);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Rebuild the chessboard to represent what changes (if any) have been made. Also update the status message
     * to reflect what just happened
     *
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel chessModel, String msg) {
        chessboard = new GridPane();
        for (int row = 0; row < chessModel.getRows(); row++) {
            for (int col = 0; col < chessModel.getCols(); col++) {
                Button button = new Button();
                switch (chessModel.getCell(row, col)) {
                    case 'B' -> button.setGraphic(new ImageView(bishop));
                    case 'K' -> button.setGraphic(new ImageView(king));
                    case 'P' -> button.setGraphic(new ImageView(pawn));
                    case 'R' -> button.setGraphic(new ImageView(rook));
                    case 'N' -> button.setGraphic(new ImageView(knight));
                    case 'Q' -> button.setGraphic(new ImageView(queen));
                }
                if ((row + col) % 2 == 0)  {
                    button.setBackground(LIGHT);
                } else {
                    button.setBackground(DARK);
                }
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(e -> chessModel.select(finalRow, finalCol));
                chessboard.add(button, col, row);
            }
        }
        mainContainer.setCenter(chessboard);
        // update all the images on the buttons
        status.setText(msg);
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    /**
     * Start the application
     *
     * @param args filename of the initial chess puzzle txt file
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
