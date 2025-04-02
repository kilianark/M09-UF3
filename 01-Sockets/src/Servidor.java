import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    static final int PORT = 7777;
    static final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void conecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.printf("Servidor en marxa a %s:%d%n", HOST, PORT);
            System.out.printf("Esperant connexions a %s:%d%n", HOST, PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: /127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void repDades() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Rebut: " + msg);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        try {
            if (clientSocket != null)
                clientSocket.close();
            if (srvSocket != null)
                srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.conecta();
        servidor.repDades();
        servidor.tanca();
    }

}