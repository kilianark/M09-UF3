
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null) serverSocket.close();
        System.out.println("Servidor aturat.");
    }

    public String getNom(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeObject("Escriu el teu nom:");
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.iniciarServidor();

            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(servidor.clientSocket.getInputStream());

            String nomClient = servidor.getNom(in, out);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(in, nomClient);
            fil.start();
            System.out.println("Fil de xat creat.");
            System.out.println("Fil de " + nomClient + " iniciat");

            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                out.writeObject(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            fil.join();
            servidor.clientSocket.close();
            servidor.pararServidor();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
