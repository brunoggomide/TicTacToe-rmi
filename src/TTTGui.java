import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class TTTGui extends JFrame {
    private TTTInterface game;
    private JButton[] buttons = new JButton[9];
    private int playerID;
    private int symbol;
    private JLabel statusLabel;
    private Timer updateTimer;

    public TTTGui(TTTInterface game, int playerID, int symbol) throws RemoteException {
        this.game = game;
        this.playerID = playerID;
        this.symbol = symbol;
        initializeUI();
        setTitle("Tic Tac Toe - Jogador " + playerID + " (" + (symbol == 1 ? 'X' : 'O') + ")");
        setSize(300, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        startUpdateTimer();
    }

    private void initializeUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            final int position = i + 1;
            JButton button = new JButton();
            button.addActionListener(e -> {
                try {
                    makeMove(position, button);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            });
            buttons[i] = button;
            panel.add(button);
        }
        this.add(panel, BorderLayout.CENTER);

        statusLabel = new JLabel("Espere pelo adversario...", SwingConstants.CENTER);
        this.add(statusLabel, BorderLayout.SOUTH);
    }

    private void makeMove(int position, JButton button) throws RemoteException {
        try {
            int currentTurn = game.whoseTurn(playerID);
            if (currentTurn == playerID) {
                if (game.play(position, playerID, symbol)) {
                    button.setText(symbol == (int) 'X' ? "X" : "O");
                    refreshBoard();
                } else {
                    System.out.println("Movimento inválido.");
                }
            } else {
                System.out.println("Não é a sua vez.");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void refreshBoard() throws RemoteException {
        String boardStr = game.currentBoard(playerID);
        updateBoard(boardStr);
        updateStatus();
    }

    private void updateBoard(String boardStr) {
        char[] boardChars = boardStr.toCharArray();
        int length = Math.min(boardChars.length, buttons.length);
        for (int i = 0; i < length; i++) {
            buttons[i].setText(boardChars[i] == ' ' ? "" : String.valueOf(boardChars[i]));
        }
    }

    private void updateStatus() throws RemoteException {
        int status = game.checkStatus(playerID);
        if (status == 0) {
            statusLabel.setText("EMPATE!");
            stopUpdateTimer();
        } else if (status == playerID) {
            statusLabel.setText("PARABÉNS, VOCÊ VENCEU!");
            stopUpdateTimer();
        } else if (status != -1) {
            statusLabel.setText("VOCÊ PERDEU, BOA SORTE NA PRÓXIMA!");
            stopUpdateTimer();
        } else {
            if (game.whoseTurn(playerID) == playerID) {
                statusLabel.setText("SUA VEZ");
            } else {
                statusLabel.setText("AGUARDE A JOGADA DO OPONENTE");
            }
        }
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(1000, e -> {
            try {
                refreshBoard();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
        updateTimer.start();
    }

    private void stopUpdateTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}
