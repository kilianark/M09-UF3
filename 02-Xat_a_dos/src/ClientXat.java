import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Client connectat a localhost:9999");

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String msg) throws IOException {
        out.writeObject(msg);
        System.out.println("Enviant missatge: " + msg);
    }

    public void tancarClient() throws IOException {
        System.out.println("Tancant client...");
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connecta();

            FilLectorCX fil = new FilLectorCX(client.in);
            fil.start();

            try (Scanner sc = new Scanner(System.in)) {
                System.out.print("Missatge('sortir' per tancar): ");
                String input = sc.nextLine();
                client.enviarMissatge(input);
                
                String missatge;
                do {
                    System.out.print("Missatge ('sortir' per tancar): ");
                    missatge = sc.nextLine();
                    client.enviarMissatge(missatge);
                } while (!missatge.equalsIgnoreCase("sortir"));
                
                fil.join();
            }
            client.tancarClient();

        } catch (IOException | InterruptedException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}
