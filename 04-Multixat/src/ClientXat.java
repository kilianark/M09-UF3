import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private boolean sortir = false;
    private String nomUsuari;
    
    public void connectar() {
        try {
            socket = new Socket("localhost", 9999);
            sortida = new ObjectOutputStream(socket.getOutputStream());
            System.out.println();
            System.out.println("Client connectat a localhost:9999");
            System.out.println("Flux d'entrada i sortida creat.");

            new Thread(() -> rebreMissatges()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enviarMissatge(String missatge){
        try {
            System.out.println("Enviant missatge: " + missatge);
            sortida.writeObject(missatge);
            sortida.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rebreMissatges() {
        try {
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String missatge = (String) entrada.readObject();
                String codi = Missatge.getCodiMissatge(missatge);
                String[] parts = Missatge.getPartsMissatge(missatge);

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        System.out.println("Error rebent missatge. Sortint...");
                        System.out.println("Enviant missatge: 1003#Adéu");
                        System.out.println("Cos null. Sortint...");
                        break;
                   case Missatge.CODI_MSG_PERSONAL:
                        if (!parts[2].equals("(Servidor) : 1002") && !parts[2].equals("(Servidor) : Adéu")) {
                            System.out.println("Missatge de " + parts[2]);
                        }

                        break;
                    case Missatge.CODI_MSG_GRUP:
                        break;
                    default:
                        System.out.println("Missatge desconegut");
                }
            }

        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        }
    }

    public void tancarClient(){
        try {
            if (entrada != null) entrada.close();
            if (sortida != null) sortida.close();
            if (socket != null) socket.close();
            System.out.println("Tancant client...");
            System.out.println("Flux d'entrada tancat.");
            System.out.println("Flux de sortida tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mostraAjuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinia(){
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connectar();
        

        while (!client.sortir) {
            mostraAjuda();
            String opcio = client.getLinia();
            switch (opcio) {
                case "1":
                    System.out.print("Introdueix el nom: ");
                    String nom = client.getLinia();  
                    client.nomUsuari = nom;
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    System.out.print("Destinatari: ");
                    String desti = client.getLinia(); 
                    System.out.print("Missatge a enviar: ");
                    String msg = client.getLinia();  
                    client.enviarMissatge(Missatge.getMissatgePersonal(desti, msg));
                    break;
                case "3":
                    System.out.print("Missatge grup: ");
                    String msgGrup = client.getLinia();  
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                    break;
                case "4":
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    client.sortir = true;
                    break;
                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    client.sortir = true;
                    break;
                default:
                    mostraAjuda();
            }
        }

        client.tancarClient();
    }

}   