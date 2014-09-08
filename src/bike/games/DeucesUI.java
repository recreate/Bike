package bike.games;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BorderWidths;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Pair;

import bike.table.Card;
import bike.table.Player;
import bike.util.DeucesServer;
import bike.util.DeucesClient;

public class DeucesUI extends Application {
	// TODO: Replace with a 4 element Player array that maintains the order?
	// TODO: Or use a Map between player number and player position?
	private Player p0;
	private Player p1;
	private Player p2;
	private Player p3;
	
	private static final Pair<Integer,Integer> P0_HAND_POSITION = new Pair<Integer,Integer>(2, 3);
	private static final Pair<Integer,Integer> P1_HAND_POSITION = new Pair<Integer,Integer>(3, 2);
	private static final Pair<Integer,Integer> P2_HAND_POSITION = new Pair<Integer,Integer>(2, 1);
	private static final Pair<Integer,Integer> P3_HAND_POSITION = new Pair<Integer,Integer>(1, 2);
	
	protected Deuces game;
	protected GridPane table;
	protected Button playButton;
	protected Button passButton;
	protected ScrollPane historyContainer;
	protected TextFlow history;
	protected ProgressBar playerJoiningProgress;
	protected List<Integer> currentPlayIndicies;
	protected DeucesServer server;
	protected DeucesClient client;
	protected static final int CARD_WIDTH = 64;
	protected static final int CARD_HEIGHT = 128;
	private final String RESOURCES_DIR;
	
	public DeucesUI() {
		this.game = new Deuces();
		this.currentPlayIndicies = new ArrayList<Integer>();
		this.server = null;
		this.client = null;
		this.RESOURCES_DIR = this.getClass().getResource("/resources").toString();
		
		this.p0 = null;
		this.p1 = null;
		this.p2 = null;
		this.p3 = null;
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Two");
		
		this.table = new GridPane();
		//this.table.setGridLinesVisible(true);
		this.table.setAlignment(Pos.CENTER);
		this.table.setHgap(10);
		this.table.setVgap(10);
		this.table.setPadding(new Insets(25, 25, 25, 25));
		
		RowConstraints cardRows = new RowConstraints();
		RowConstraints centerRow = new RowConstraints();
		RowConstraints historyRow = new RowConstraints();
		cardRows.setMinHeight(150);
		cardRows.setValignment(VPos.BOTTOM);
		centerRow.setMinHeight(300);
		historyRow.setMinHeight(150);
		historyRow.setMaxHeight(150);
		historyRow.setValignment(VPos.TOP);
		this.table.getRowConstraints().addAll(
			new RowConstraints(), cardRows, centerRow, cardRows, new RowConstraints(), historyRow
		);
		
		ColumnConstraints otherCols = new ColumnConstraints();
		ColumnConstraints centerCol = new ColumnConstraints();
		centerCol.setMinWidth(600);
		centerCol.setHalignment(HPos.CENTER);
		otherCols.setHalignment(HPos.CENTER);
		this.table.getColumnConstraints().addAll(
			otherCols, otherCols, centerCol, otherCols, otherCols
		);
		
		MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		Menu optionsMenu = new Menu("Options");
		Menu helpMenu = new Menu("Help");
		
		MenuItem hostGame = new MenuItem("Host a Game");
		MenuItem joinGame = new MenuItem("Join a Game");
		MenuItem disconnect = new MenuItem("Disconnect");
		MenuItem exit = new MenuItem("Exit");
		MenuItem about = new MenuItem("About");
		
		hostGame.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				DeucesUI.this.hostGameDialogBox();
			}
		});
		joinGame.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				DeucesUI.this.joinGameDialogBox();
			}
		});
		
		fileMenu.getItems().addAll(hostGame, joinGame, disconnect, exit);
		helpMenu.getItems().addAll(about);
		menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);
		
		VBox rootNode = new VBox();
		rootNode.getChildren().addAll(menuBar, this.table);
		
		Scene scene = new Scene(rootNode, 1200, 900);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(RESOURCES_DIR + "/deuces.css");
		
		primaryStage.show();
	}
	
	private List<Text> getPlayCardsText(String playerName, List<Card> cards) {
		List<Text> playerTexts = new ArrayList<Text>();
		playerTexts.add(new Text(playerName + ":\n\t"));
		
		for (Card c : cards) {
			Text cardText = new Text(c.prettyPrint3());
			if (c.getSuit() == Card.Suit.DIAMOND || c.getSuit() == Card.Suit.HEART) {
				cardText.setFill(Color.RED);
			} else {
				cardText.setFill(Color.BLACK);
			}
			playerTexts.add(cardText);
		}
		
		playerTexts.add(new Text("\n"));
		return playerTexts;
	}
	
	protected void hostGameDialogBox() {
		final Stage hostGameStage = new Stage();
		hostGameStage.initModality(Modality.WINDOW_MODAL);
		hostGameStage.setTitle("Host a Game");
		
		GridPane layout = new GridPane();
		//layout.setGridLinesVisible(true);
		layout.setAlignment(Pos.CENTER);
		layout.setHgap(10);
		layout.setVgap(10);
		layout.setPadding(new Insets(25, 25, 25, 25));
		
		layout.add(new Label("Name:"), 0, 0);
		TextField nameField = new TextField();
		layout.add(nameField, 1, 0);
		
		layout.add(new Label("Host IP:"), 0, 1);
		TextField hostIP = new TextField();
		layout.add(hostIP, 1, 1);
		
		layout.add(new Label("Host Port:"), 0, 2);
		TextField hostPort = new TextField();
		layout.add(hostPort, 1, 2);
		
		final ComboBox<Player.Type> p0Dropdown = new ComboBox<Player.Type>();
		final ComboBox<Player.Type> p1Dropdown = new ComboBox<Player.Type>();
		final ComboBox<Player.Type> p2Dropdown = new ComboBox<Player.Type>();
		final ComboBox<Player.Type> p3Dropdown = new ComboBox<Player.Type>();
		p0Dropdown.getItems().addAll(Player.Type.COMPUTER, Player.Type.HUMAN);
		p1Dropdown.getItems().addAll(Player.Type.COMPUTER, Player.Type.HUMAN);
		p2Dropdown.getItems().addAll(Player.Type.COMPUTER, Player.Type.HUMAN);
		p3Dropdown.getItems().addAll(Player.Type.COMPUTER, Player.Type.HUMAN);
		
		layout.add(new Label("Player 0"), 0, 3);
		layout.add(p0Dropdown, 1, 3);
		layout.add(new Label("Player 1"), 0, 4);
		layout.add(p1Dropdown, 1, 4);
		layout.add(new Label("Player 2"), 0, 5);
		layout.add(p2Dropdown, 1, 5);
		layout.add(new Label("Player 3"), 0, 6);
		layout.add(p3Dropdown, 1, 6);
		
		Button submit = new Button("Host");
		submit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Player hostPlayer = null;
				try {
					hostPlayer = new Player(nameField.getText().toString(), 
						Player.Type.HUMAN,
						InetAddress.getByName(hostIP.getText().toString()),
						Integer.parseInt(hostPort.getText().toString()));
				} catch (UnknownHostException e) {
					return; // Invalid ip address
				} catch (NumberFormatException e) {
					return; // Invalid port
				}
				
				hostGameStage.close();
				hostGame(hostPlayer, p1Dropdown.getValue(), p2Dropdown.getValue(), p3Dropdown.getValue());
			}
		});
		layout.add(submit, 1, 7);
		
		hostGameStage.setScene(new Scene(layout));
		hostGameStage.show();
	}
	
	protected void joinGameDialogBox() {
		final Stage joinGameStage = new Stage();
		joinGameStage.initModality(Modality.WINDOW_MODAL);
		joinGameStage.setTitle("Join a Game");
		
		GridPane layout = new GridPane();
		//layout.setGridLinesVisible(true);
		layout.setAlignment(Pos.CENTER);
		layout.setHgap(10);
		layout.setVgap(10);
		layout.setPadding(new Insets(25, 25, 25, 25));
		
		layout.add(new Label("Name:"), 0, 0);
		TextField nameField = new TextField();
		layout.add(nameField, 1, 0);
		
		layout.add(new Label("Server IP:"), 0, 1);
		TextField serverIP = new TextField();
		layout.add(serverIP, 1, 1);
		
		layout.add(new Label("Server Port:"), 0, 2);
		TextField serverPort = new TextField();
		layout.add(serverPort, 1, 2);
		
		layout.add(new Label("Client IP:"), 0, 3);
		TextField clientIP = new TextField();
		layout.add(clientIP, 1, 3);
		
		layout.add(new Label("Client Port:"), 0, 4);
		TextField clientPort = new TextField();
		layout.add(clientPort, 1, 4);
		
		Button submit = new Button("Join Game");
		submit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Player clientPlayer = null;
				try {
					clientPlayer = new Player(nameField.getText().toString(), 
						Player.Type.HUMAN,
						InetAddress.getByName(clientIP.getText().toString()),
						Integer.parseInt(clientPort.getText().toString())
					);
					
					DeucesUI.this.client = new DeucesClient(
						InetAddress.getByName(serverIP.getText().toString()),
						Integer.parseInt(serverPort.getText().toString())
					);
					new Thread(DeucesUI.this.client).start();
					
					try {Thread.sleep(1000);} catch (InterruptedException e) {}
					
					DeucesUI.this.client.sendJoinRequest(
						clientPlayer.getName(), 
						clientIP.getText().toString(), 
						clientPlayer.getPlayerPort() + ""
					);
				} catch (UnknownHostException e) {
					return; // Invalid ip address
				} catch (NumberFormatException e) {
					return; // Invalid port
				}
				
				joinGameStage.close();
				
				DeucesUI.this.waitForPlayersToJoin();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Player players[] = DeucesUI.this.client.getPlayerInformation();
						for (int i = 0; i < 4; i++) {
							if (players[i].equals(DeucesUI.this.p0)) {
								DeucesUI.this.p1 = players[(i+1)%4];
								DeucesUI.this.p2 = players[(i+2)%4];
								DeucesUI.this.p3 = players[(i+3)%4];
							}
						}
						
						DeucesUI.this.playerJoiningProgress.setProgress(1.0);
					}
				}).start();
			}
		});
		layout.add(submit, 1, 5);
		
		joinGameStage.setScene(new Scene(layout));
		joinGameStage.show();
	}
	
	protected void hostGame(Player hostPlayer, Player.Type b, Player.Type c, Player.Type d) {
		int numHumanConnections = 0;
		
		this.p0 = hostPlayer;
		
		if (b == Player.Type.COMPUTER) {
			this.p1 = new Player("MICHAEL FARADAY", Player.Type.COMPUTER, null, 0);
		} else if (b == Player.Type.HUMAN) {
			numHumanConnections++;
		}
		
		if (c == Player.Type.COMPUTER) {
			this.p2 = new Player("LEONHARD EULER", Player.Type.COMPUTER, null, 0);
		} else if (c == Player.Type.HUMAN) {
			numHumanConnections++;
		}
		
		if (d == Player.Type.COMPUTER) {
			this.p3 = new Player("KEANU REEVES", Player.Type.COMPUTER, null, 0);
		} else if (d == Player.Type.HUMAN) {
			numHumanConnections++;
		}
		
		if (numHumanConnections > 0) {
			this.server = new DeucesServer(this.p0.getPlayerIP(), this.p0.getPlayerPort(), numHumanConnections);
			new Thread(this.server).start();
		}
		
		this.waitForPlayersToJoin();
		
		new Thread(new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				// Wait for players to connect
				while (DeucesUI.this.p1 == null || DeucesUI.this.p2 == null || DeucesUI.this.p3 == null) {
					Player p = DeucesUI.this.server.processJoinRequest();
					
					if (p != null) {
						if (DeucesUI.this.p1 == null) {
							DeucesUI.this.p1 = p;
						} else if (DeucesUI.this.p2 == null) {
							DeucesUI.this.p2 = p;
						} else if (DeucesUI.this.p3 == null) {
							DeucesUI.this.p3 = p;
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						continue;
					}
				}
				
				DeucesUI.this.playerJoiningProgress.setProgress(1.0);
				return new Integer(1);
			}
		}).start();
	}
	
	protected void waitForPlayersToJoin() {
		final Stage waitingStage = new Stage();
		waitingStage.initModality(Modality.WINDOW_MODAL);
		waitingStage.setTitle("Waiting for players...");
		
		this.playerJoiningProgress = new ProgressBar(0.0);
		this.playerJoiningProgress.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if ((Double)newValue >= 0.99) {
					waitingStage.close();
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// Start the game, either as the host or a client
							if (DeucesUI.this.client == null) {
								DeucesUI.this.beginGameAsHost();
							} else {
								DeucesUI.this.beginGameAsClient();
							}
						}
					});
				}
			}
		});
		
		GridPane layout = new GridPane();
		layout.setAlignment(Pos.CENTER);
		layout.add(this.playerJoiningProgress, 0, 0);
		
		waitingStage.setScene(new Scene(layout, 200, 150));
		waitingStage.show();
	}
	
	protected void beginGameAsHost() {
		this.game.startGame(this.p0, this.p1, this.p2, this.p3);
		
		this.server.broadcastPlayerInformation(this.p0, this.p1, this.p2, this.p3);
		
		this.setTable();
		
		this.playButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				DeucesUI.this.playButton.setDisable(true);
				DeucesUI.this.passButton.setDisable(true);
				DeucesUI.this.playCardsFromCurrentPlayer();
				DeucesUI.this.displayHand(2, 3, DeucesUI.this.game.getCurrentPlayer().cards);
			}
		});
		this.passButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				DeucesUI.this.playButton.setDisable(true);
				DeucesUI.this.passButton.setDisable(true);
				DeucesUI.this.passCurrentPlayerTurn();
			}
		});
	}
	
	protected void beginGameAsClient() {
		this.setTable();
		
		this.playButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				
			}
		});
		this.passButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				DeucesUI.this.client.sendPassRequest(DeucesUI.this.p0.getName());
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected void setTable() {
		Text p0Name = new Text(this.p0.getName());
		Text p1Name = new Text(this.p1.getName());
		Text p2Name = new Text(this.p2.getName());
		Text p3Name = new Text(this.p3.getName());
		
		p1Name.setRotate(90);
		p3Name.setRotate(-90);
		
		this.table.add(p0Name, 2, 4);
		this.table.add(p1Name, 4, 2);
		this.table.add(p2Name, 2, 0);
		this.table.add(p3Name, 0, 2);
		
		this.playButton = new Button("Play");
		this.passButton = new Button("Pass");
		this.table.add(playButton, 4, 4);
		this.table.add(passButton, 0, 4);
		
		this.history = new TextFlow();
		this.history.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, null)));
		this.history.heightProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if (newValue instanceof Double)
					DeucesUI.this.historyContainer.setVvalue((Double)newValue);
			}
		});
		
		this.historyContainer = new ScrollPane(this.history);
		this.historyContainer.setFitToHeight(true);
		this.historyContainer.setFitToWidth(true);
		this.historyContainer.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		this.historyContainer.setBorder(new Border(new BorderStroke(
			Color.FIREBRICK, BorderStrokeStyle.NONE, new CornerRadii(0.5), new BorderWidths(5.0)
		)));
		this.table.add(this.historyContainer, 2, 5);
		
		displayFaceDownCards(3, 2, this.p1.cards.size(), true);
		displayFaceDownCards(2, 1, this.p2.cards.size(), false);
		displayFaceDownCards(1, 2, this.p3.cards.size(), true);
		
		displayHand(2, 3, this.p0.cards);
	}
	
	private void advanceGame() {
		// TODO: Offer to restart the game
		Player winner = this.game.checkForWinner();
		if (winner != null) {
			this.history.getChildren().addAll(new Text(winner.getName() + " has won the game."));
			return;
		}
		
		Player currentPlayer = this.game.getCurrentPlayer();
		if (currentPlayer.getPlayerType() == Player.Type.COMPUTER) {
			// Random
			List<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < currentPlayer.cards.size(); i++) {
				indices.add(i);
			}
			
			int handSize = this.game.examinePile().size();
			if (handSize == 0) { // free play
				this.currentPlayIndicies.add(0);
				boolean isValid = this.game.isValidPlay(this.currentPlayIndicies);
				if (isValid) {
					this.playCardsFromCurrentPlayer();
				} else {
					this.passCurrentPlayerTurn();
				}
			} else if (currentPlayer.cards.size() >= handSize) {
				boolean isValid = false;
				for (int iter = 0; iter < 100; iter++) {
					this.currentPlayIndicies.clear();
					Collections.shuffle(indices, new Random());
					
					for (int i = 0; i < handSize; i++) {
						this.currentPlayIndicies.add(indices.get(i));
					}
					
					isValid = this.game.isValidPlay(this.currentPlayIndicies);
					if (isValid) {
						this.playCardsFromCurrentPlayer();
						break;
					}
				}
				
				if (!isValid) {
					this.passCurrentPlayerTurn();
				}
			} else {
				this.passCurrentPlayerTurn();
			}
			
			this.currentPlayIndicies.clear();
			
			if (currentPlayer.equals(this.p1)) {
				displayFaceDownCards(3, 2, currentPlayer.cards.size(), true);
			} else if (currentPlayer.equals(this.p2)) {
				displayFaceDownCards(2, 1, currentPlayer.cards.size(), false);
			} else if (currentPlayer.equals(this.p3)) {
				displayFaceDownCards(1, 2, currentPlayer.cards.size(), true);
			}
			
		} else if (currentPlayer.equals(this.p0)) {
			this.playButton.setDisable(false);
			this.passButton.setDisable(false);
		} else {
			// Wait on other human players
		}
	}
	
	protected void playCardsFromCurrentPlayer() {
		Player currentPlayer = this.game.getCurrentPlayer();
		List<Card> playedCards = new ArrayList<Card>();
		for (int i = 0; i < this.currentPlayIndicies.size(); i++) {
			playedCards.add(currentPlayer.cards.get(
				this.currentPlayIndicies.get(i)
			));
		}
		
		boolean isValidPlay = this.game.playCards(this.currentPlayIndicies);
		if (isValidPlay) {
			displayHand(2, 2, playedCards);
			this.history.getChildren().addAll(this.getPlayCardsText(currentPlayer.getName(), playedCards));
			this.currentPlayIndicies.clear();
			this.advanceGame();
		}
	}
	
	protected void passCurrentPlayerTurn() {
		Text pass = new Text(this.game.getCurrentPlayer().getName() + " has Passed\n");
		pass.setFill(Color.ORANGERED);
		this.history.getChildren().addAll(pass);
		this.currentPlayIndicies.clear();
		this.game.passTurn();
		this.advanceGame();
	}
	
	protected void removeElementFromTable(int x, int y) {
		Node result = null;
		for(Node node : this.table.getChildren()) {
			if (this.table.getRowIndex(node) == null || this.table.getColumnIndex(node) == null)
				continue; // ???
			
			if(this.table.getRowIndex(node) == y && this.table.getColumnIndex(node) == x) {
				result = node;
				break;
			}
		}
		
		this.table.getChildren().remove(result);
	}
	
	protected void displayFaceDownCards(int x, int y, int n, boolean rotate) {
		removeElementFromTable(x, y);
		
		Group cards = new Group();
		for (int i = 0; i < n; i++) {
			ImageView card = new ImageView(new Image(RESOURCES_DIR + "/images/PNGCards/card_back.png"));
			card.setPreserveRatio(true);
			card.setFitHeight(128);
			if (rotate) {
				card.setX(0);
				card.setY(i*16);
				card.setRotate(90.0);
			} else {
				card.setX(i*32);
				card.setY(0);
			}
			cards.getChildren().add(card);
		}
		GridPane.setHalignment(cards, HPos.CENTER);
		
		this.table.add(cards, x, y);
	}
	
	protected void displayHand(int x, int y, List<Card> cards) {
		removeElementFromTable(x, y);
		
		Group cardsGroup = new Group();
		for (int i = 0; i < cards.size(); i++) {
			ImageView card = new ImageView(getImageForCard(cards.get(i)));
			card.setPreserveRatio(true);
			card.setFitHeight(128);
			card.setX(i*32);
			card.setId("" + i); // what a hack
			card.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent t) {
					int index = Integer.parseInt(((ImageView)t.getTarget()).idProperty().getValue());
					System.out.println("Clicked on card: " + index);
					ImageView cardImage = (ImageView)t.getTarget();
					if (DeucesUI.this.currentPlayIndicies.contains(index)) {
						cardImage.setTranslateY(0);
						cardImage.setEffect(null);
						DeucesUI.this.currentPlayIndicies.remove(new Integer(index));
					} else {
						cardImage.setTranslateY(-10);
						cardImage.setEffect(new javafx.scene.effect.SepiaTone());
						DeucesUI.this.currentPlayIndicies.add(index);
					}
				}
			});
			
			cardsGroup.getChildren().add(card);
		}
		GridPane.setHalignment(cardsGroup, HPos.CENTER);
		
		this.table.add(cardsGroup, x, y);
	}
	
	protected Image getImageForCard(Card card) {
		return new Image(String.format("%s/images/PNGCards/%s_%s.png", RESOURCES_DIR, card.getSuit().toString(), card.getRank().toString()));
	}
	
	public static void main(String[] args) {
		DeucesUI client = new DeucesUI();
		System.out.println(client.getClass().getResource("/resources"));
		DeucesUI.launch(args);
	}
}
