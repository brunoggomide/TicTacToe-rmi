import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TTTImplementation extends UnicastRemoteObject implements TTTInterface {
    private static class Game {
        Map<Integer, Character> symbols = new HashMap<>();
        boolean gameReady = false;
        int currentPlayer = 1;
        char[] board = new char[9];
        boolean gameEnded = false;

        public Game() {
            for (int i = 0; i < board.length; i++) {
                board[i] = '\u0000';
            }
        }
    }

    private AtomicInteger connectedPlayers = new AtomicInteger(0);
    private Map<Integer, Game> playerGames = new HashMap<>();

    public TTTImplementation() throws RemoteException {
        super();
    }

    @Override
    public Integer registerPlayer() throws RemoteException {
        int playerID = connectedPlayers.incrementAndGet();
        Game game;
        if (playerID % 2 == 1) {
            game = new Game();
            game.currentPlayer = playerID;
            playerGames.put(playerID, game);
        } else {
            game = playerGames.get(playerID - 1);
            playerGames.put(playerID, game);
            game.gameReady = true;
        }
        game.symbols.put(playerID, playerID % 2 != 0 ? 'X' : 'O');
        return playerID;
    }

    @Override
    public boolean isGameReady(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        return game != null && game.gameReady;
    }

    @Override
    public int getSymbol(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        return (int) game.symbols.get(player);
    }

    @Override
    public int whoseTurn(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        return game.currentPlayer;
    }

    @Override
    public boolean play(int pos, int player, int whoPlaying) throws RemoteException {
        Game game = playerGames.get(player);
        if (!game.gameEnded && game.gameReady && game.currentPlayer == player
                && game.symbols.containsKey(player) && pos >= 1 && pos <= 9
                && game.board[pos - 1] == '\u0000') {
            game.board[pos - 1] = game.symbols.get(player);

            alternateCurrentPlayer(game, player);

            checkWinner(player);
            return true;
        }
        return false;
    }

    private void alternateCurrentPlayer(Game game, int currentPlayer) {
        for (Map.Entry<Integer, Character> entry : game.symbols.entrySet()) {
            if (entry.getKey() != currentPlayer) {
                game.currentPlayer = entry.getKey();
                break;
            }
        }
    }

    @Override
    public void checkWinner(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        char symbol = game.symbols.get(player);
        char[] b = game.board;

        if ((b[0] == symbol && b[1] == symbol && b[2] == symbol) ||
                (b[3] == symbol && b[4] == symbol && b[5] == symbol) ||
                (b[6] == symbol && b[7] == symbol && b[8] == symbol) ||
                (b[0] == symbol && b[3] == symbol && b[6] == symbol) ||
                (b[1] == symbol && b[4] == symbol && b[7] == symbol) ||
                (b[2] == symbol && b[5] == symbol && b[8] == symbol) ||
                (b[0] == symbol && b[4] == symbol && b[8] == symbol) ||
                (b[2] == symbol && b[4] == symbol && b[6] == symbol)) {
            game.gameEnded = true;
        } else if (isBoardFull(game)) {
            game.gameEnded = true;
        }
    }

    private boolean isBoardFull(Game game) {
        for (char c : game.board) {
            if (c == '\u0000') {
                return false;
            }
        }
        return true;
    }

    @Override
    public int checkStatus(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        if (!game.gameEnded) {
            return -1;
        }

        char symbol = game.symbols.get(player);
        char[] b = game.board;
        boolean win = (b[0] == symbol && b[1] == symbol && b[2] == symbol) ||
                (b[3] == symbol && b[4] == symbol && b[5] == symbol) ||
                (b[6] == symbol && b[7] == symbol && b[8] == symbol) ||
                (b[0] == symbol && b[3] == symbol && b[6] == symbol) ||
                (b[1] == symbol && b[4] == symbol && b[7] == symbol) ||
                (b[2] == symbol && b[5] == symbol && b[8] == symbol) ||
                (b[0] == symbol && b[4] == symbol && b[8] == symbol) ||
                (b[2] == symbol && b[4] == symbol && b[6] == symbol);

        if (win) {
            return player;
        } else if (isBoardFull(game)) {
            return 0;
        }
        return -2;
    }

    @Override
    public String currentBoard(Integer player) throws RemoteException {
        Game game = playerGames.get(player);
        StringBuilder boardStr = new StringBuilder();
        for (char c : game.board) {
            boardStr.append(c == '\u0000' ? ' ' : c);
        }
        return boardStr.toString();
    }

}
