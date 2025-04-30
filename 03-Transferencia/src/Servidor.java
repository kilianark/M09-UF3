import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexió...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexió acceptada: " + socket.getInetAddress());
        return socket;
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

        while(true) {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) entrada.readObject();
            System.out.println("Nom fitxer rebut: " + nomFitxer);
    
            if (nomFitxer == null || nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                break;
            }
    
            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();
    
            if (contingut == null) {
                System.out.println("Error llegint el fitxer del client: null");
                sortida.writeObject(null);
                break;
            }
    
            System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
            sortida.writeObject(contingut);
            sortida.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            try (socket) {
                System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
            }
        } catch (IOException e) {
            System.err.println("Error tancant connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Servidor servidor = new Servidor();
            Socket socket = servidor.connectar();
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
    }
}
