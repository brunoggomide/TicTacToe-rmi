import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static final int registryPort = 3000;

    public static void main(String[] args) {
        try {
            TTTInterface ttt = new TTTImplementation();

            LocateRegistry.createRegistry(registryPort);
            Naming.rebind("rmi://localhost:" + registryPort + "/tictactoe", ttt);

            System.err.println("Servidor funcionando...");

            System.in.read();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
