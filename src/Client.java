import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

public class Client {

    public static void main(String[] args) {
        try {
            final TTTInterface ttt = (TTTInterface) Naming.lookup("rmi://localhost:3000/tictactoe");

            final Integer playerID = ttt.registerPlayer();

            System.err.println("\nUsuário registrado com ID: " + playerID + "\n");
            System.err.println("\nProcurando Partida...\n");

            while (!ttt.isGameReady(playerID)) {
                Thread.sleep(1000);
            }
            System.err.println("\nO JOGO VAI COMEÇAR!\n");

            final int symbol = ttt.getSymbol(playerID);
            System.err.printf("\nVocê joga com: %c\n", symbol == (int) 'X' ? 'X' : 'O');

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new TTTGui(ttt, playerID, symbol);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
