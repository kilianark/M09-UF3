
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    static final int PORT = 7777;
    static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connexió establerta amb el servidor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void envia(String msg) {
        out.println(msg);
        System.out.println("Enviat al servidor: " + msg);
    }

    public void tanca() {
        try {
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
            System.out.println("Client tancat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.conecta();
        client.envia("Prova enviament 1");
        client.envia("Prova enviament 2");
        client.envia("Adeu!");

        System.out.println("Prem ENTER per tancar....");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.tanca();
    }
}
