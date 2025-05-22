import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    static final int PORT = 9999;
    static final String HOST = "localhost";
    static final String MSG_SORTIR = "Adéu";
    Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;

    public void servidorAEscoltar(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println();
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            while(true){
                Socket cliSocket = serverSocket.accept();
                System.out.println("Client connectat: " + cliSocket.getInetAddress());

                GestorClients gestorClients = new GestorClients(cliSocket, this);
                gestorClients.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void finalitzarXat(){
        String msgSortirTots = Missatge.getMissatgeSortirTots(MSG_SORTIR);
        enviarMissatgeGrup(msgSortirTots); 
        clients.clear();
        System.out.println("Tancant tots els clients.");
        System.exit(0);
    }

    public void afegirClient(GestorClients gestorClients){
        clients.put(gestorClients.getNom(), gestorClients);
        System.out.println(gestorClients.getNom() + " connectat.");
        String msg = Missatge.getMissatgeGrup("Entra: " + gestorClients.getNom()); 
        enviarMissatgeGrup(msg);
    }

    public void eliminarClient(String nom){
        if(clients.containsKey(nom)){
            clients.remove(nom);
        }
    }

    public void enviarMissatgeGrup(String msg){
        for(GestorClients gestor: clients.values()){
           gestor.enviarMissatgeBrut(msg);
        }
    }

    public void enviarMissatgePersonal(String nomDestinatari, String nomRemitent, String msg){        
        GestorClients client = clients.get(nomDestinatari); 
        if(client != null){
            System.out.println("Missatge personal per (" + nomDestinatari + ") de (" + nomRemitent + "): " + msg);
            client.enviarMissatge(nomRemitent, msg);
        } else {
            System.out.println("Destinatari no trobat: " + nomDestinatari);
        }
        
    }

    public static void main(String[] args) {
        ServidorXat servidorXat = new ServidorXat();

        servidorXat.servidorAEscoltar();
    }
}