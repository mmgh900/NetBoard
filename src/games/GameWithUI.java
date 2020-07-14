package games;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.Square;
import controllers.GameController;
import gui.elements.SquareSkin;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import users.Client;

public class GameWithUI {
    protected Square[][] squares = new Square[3][3];
    protected GameController gameController;

    protected Client client;
    private Player currentPlayer;
    private GameMode gameMode;
    private Player thisPlayer;
    private ClientProfile otherPlayer;

    private int[] winPathNumbers;

    public GameWithUI(Client client) {
        gameController = client.getWindow().getGameController();
        currentPlayer = Player.NONE;
        gameMode = GameMode.NONE;
        this.client = client;
    }

    public int[] getWinPathNumbers() {
        return winPathNumbers;
    }

    public void load() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameController.initializeTabs();
            }
        });

    }

    public void startGame(GameMode gameMode1) {
        otherPlayer = null;


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                winPathNumbers = null;
                gameMode = gameMode1;
                gameController.clearGameScene();
                gameController.makeNewBoardSkin();

                for (int i = 0; i < squares.length; i++) {
                    for (int j = 0; j < squares[i].length; j++) {
                        squares[i][j] = new Square(i, j);
                    }
                }

                if (gameMode == GameMode.SINGLE_PLAYER) {
                    gameController.setPlayersInfo(client.getClientProfile().getFirstName(), "Computer AI", "@" + client.getClientProfile().getUsername().toLowerCase(), "");
                } else if (gameMode == GameMode.MULTIPLAYER) {
                    gameController.setPlayersInfo("Player X", "Player O", "", "");

                } else if (gameMode == GameMode.ONLINE) {

                } else if (gameMode == GameMode.NONE) {

                }
                setCurrentPlayer(Player.PLAYER_X);
            }
        });

    }

    public ClientProfile getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(ClientProfile otherPlayer) {
        if (otherPlayer == null) {
            throw new NullPointerException("Client profile is null");
        }
        this.otherPlayer = otherPlayer;
    }

    public void startGame(ClientProfile playerX, ClientProfile playerO) {
        startGame(GameMode.ONLINE);
        client.getWindow().loadGameScene();

        playerO.setPlayingOnline(true);
        playerX.setPlayingOnline(true);

        if (playerX.equals(client.getClientProfile())) {
            thisPlayer = Player.PLAYER_X;
            setOtherPlayer(playerO);
            gameController.setPlayersInfo(client.getClientProfile(), otherPlayer);
            System.out.println("Player X = " + client.getClientProfile() + "(this player) Player Y = " + otherPlayer + "(other player)");
        } else if (playerO.equals(client.getClientProfile())) {
            thisPlayer = Player.PLAYER_O;
            setOtherPlayer(playerX);
            gameController.setPlayersInfo(otherPlayer, client.getClientProfile());
            System.out.println("Player X = " + otherPlayer + "(other player) Player Y = " + client.getClientProfile() + "(this player)");
        } else {
            try {
                throw new Exception(client.getClientProfile().toString() + " this player was neither " + playerX.toString() + " nor " + playerO.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updateGame(Square[][] squares) {
        this.squares = squares;
        Player result = checkResult(squares);
        gameController.repaintBoard(squares);
        if (result == null) {

            setCurrentPlayer(thisPlayer);
        } else {
            doForResult(checkResult(squares));
        }
    }

    public boolean isMyTurn() {
        return getCurrentPlayer() == thisPlayer;
    }

    protected Player checkResult(Square[][] squares) {
        if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[0][1].getState() && squares[0][0].getState() == squares[0][2].getState()) {
            winPathNumbers = new int[]{0, 0, 0, 2};
            return squares[0][0].getState();
        } else if (squares[1][0].getState() != Player.NONE && squares[1][0].getState() == squares[1][1].getState() && squares[1][0].getState() == squares[1][2].getState()) {
            winPathNumbers = new int[]{1, 0, 1, 2};
            return squares[1][0].getState();
        } else if (squares[2][0].getState() != Player.NONE && squares[2][0].getState() == squares[2][1].getState() && squares[2][0].getState() == squares[2][2].getState()) {
            winPathNumbers = new int[]{2, 0, 2, 2};
            return squares[2][0].getState();
        } else if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[1][0].getState() && squares[0][0].getState() == squares[2][0].getState()) {
            winPathNumbers = new int[]{0, 0, 2, 0};
            return squares[0][0].getState();
        } else if (squares[0][1].getState() != Player.NONE && squares[0][1].getState() == squares[1][1].getState() && squares[0][1].getState() == squares[2][1].getState()) {
            winPathNumbers = new int[]{0, 1, 2, 1};
            return squares[0][1].getState();
        } else if (squares[0][2].getState() != Player.NONE && squares[0][2].getState() == squares[1][2].getState() && squares[0][2].getState() == squares[2][2].getState()) {
            winPathNumbers = new int[]{0, 2, 2, 2};
            return squares[0][2].getState();
        } else if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[1][1].getState() && squares[0][0].getState() == squares[2][2].getState()) {
            winPathNumbers = new int[]{0, 0, 2, 2};
            return squares[0][0].getState();
        } else if (squares[0][2].getState() != Player.NONE && squares[0][2].getState() == squares[1][1].getState() && squares[0][2].getState() == squares[2][0].getState()) {
            winPathNumbers = new int[]{0, 2, 2, 0};
            return squares[0][2].getState();
        }

        boolean draw = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (squares[i][j].getState() == Player.NONE) {
                    draw = false;
                }
            }
        }

        if (draw == true) {
            currentPlayer = Player.NONE;
            winPathNumbers = null;
            return Player.NONE;
        }
        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        gameController.setCurrentPlayer(currentPlayer);
    }

    public void doForResult(Player result) {
        if (result != null) {
            gameController.doAboutResult(result);
            if (gameMode == GameMode.SINGLE_PLAYER) {
                if (result == Player.PLAYER_X) {
                    client.getClientProfile().getTicTacToeStatistics().addSinglePlayerWins();
                    client.sendProfileToServer();
                } else if (result == Player.PLAYER_O) {
                    client.getClientProfile().getTicTacToeStatistics().addSinglePlayerLosses();
                    client.sendProfileToServer();
                }

            } else if (gameMode == GameMode.MULTIPLAYER) {

            } else if (gameMode == GameMode.ONLINE) {
                client.getClientProfile().setPlayingOnline(false);
                otherPlayer.setPlayingOnline(false);

                if (result == thisPlayer) {
                    client.getClientProfile().getTicTacToeStatistics().addTotalOnlineWins();
                    client.sendProfileToServer();
                } else if (result != Player.NONE && result != null) {
                    client.getClientProfile().getTicTacToeStatistics().addTotalOnlineLosses();
                    client.sendProfileToServer();
                }
            }
        }
        }


    public void handleClick(MouseEvent mouseEvent) {
        SquareSkin squareSkin = (SquareSkin) mouseEvent.getSource();
        Square square = squares[squareSkin.getX()][squareSkin.getY()];

        if (gameMode == GameMode.SINGLE_PLAYER) {
            if (square.getState() == Player.NONE) {
                gameController.massage.setText("");
                if (getCurrentPlayer() == Player.PLAYER_X) {

                    square.setState(Player.PLAYER_X);
                    gameController.repaintBoard(squares);
                    Player result = checkResult(squares);

                    if (result == null) {
                        new TicTacToeAiService(this).start();
                    } else {
                        doForResult(result);
                    }
                } else {
                    gameController.massage.setText("It's not your turn.");
                }

            }

        } else if (gameMode == GameMode.MULTIPLAYER) {
            if (square.getState() == Player.NONE) {
                if (getCurrentPlayer().equals(Player.PLAYER_X)) {
                    square.setState(Player.PLAYER_X);
                    Player result = checkResult(squares);
                    if (result == null) {
                        setCurrentPlayer(Player.PLAYER_O);
                    } else {
                        doForResult(result);
                    }
                } else if (getCurrentPlayer() == Player.PLAYER_O) {
                    square.setState(Player.PLAYER_O);
                    Player result = checkResult(squares);
                    if (result == null) {
                        setCurrentPlayer(Player.PLAYER_X);
                    } else {
                        doForResult(result);
                    }
                }
                gameController.repaintBoard(squares);


            }
        } else if (gameMode == GameMode.ONLINE) {
            if (otherPlayer == null) {
                throw new NullPointerException("other player is null");
            }
            if (isMyTurn() && square.getState().equals(Player.NONE)) {
                gameController.massage.setText("");
                square.setState(getCurrentPlayer());
                client.connection.sendPacket(new Packet(squares, client.getClientProfile(), otherPlayer, Packet.PacketPropose.UPDATE_GAME));
                if (thisPlayer == Player.PLAYER_X) {
                    setCurrentPlayer(Player.PLAYER_O);
                } else if (thisPlayer == Player.PLAYER_O) {
                    setCurrentPlayer(Player.PLAYER_X);
                }
                gameController.repaintBoard(squares);
                doForResult(checkResult(squares));
            } else if (!isMyTurn()) {
                gameController.massage.setText("It's not your turn.");
            }
        }

    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameController getGameController() {
        return gameController;
    }


    public enum GameMode {
        SINGLE_PLAYER, MULTIPLAYER, ONLINE, NONE
    }

    public enum Player {
        NONE, PLAYER_X, PLAYER_O
    }


}
