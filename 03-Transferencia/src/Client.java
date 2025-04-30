import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String DIR_ARRIBADA = System.getProperty("java.io.tmpdir");

    public Socket connectar() throws IOException {
        System.out.println("Connectant a -> " + HOST + ":" + PORT);
        return new Socket(HOST, PORT);
    }

    public void rebreFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        Scanner scanner = new Scanner(System.in);

        while (true) { 
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();
            
            if (nomFitxer == null || nomFitxer.equalsIgnoreCase("sortir") || nomFitxer.isEmpty()) {
                sortida.writeObject("");
                break;
            }

            sortida.writeObject(nomFitxer);
            sortida.flush();

            byte[] contingut = (byte[]) entrada.readObject();
            if (contingut == null) {
                System.out.println("Fitxer rebut nul. Comproveu la ruta.");
                break;
            }

            File output = new File(DIR_ARRIBADA + "/" + new File(nomFitxer).getName());
            Files.write(output.toPath(), contingut);
            System.out.println("Fitxer rebut i guardat com: " + output.getAbsolutePath());
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            socket.close();
            System.out.println("Connexió tancada.");
        } catch (IOException e) {
            System.err.println("Error tancant connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            Socket socket = client.connectar();
            client.rebreFitxers(socket);
            client.tancarConnexio(socket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
