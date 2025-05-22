import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread{
    private Socket client;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socket, ServidorXat servidorXat){
        this.client = socket;
        this.servidorXat = servidorXat;
    }

    public String getNom(){ return nom; }

    @Override
    public void run(){
        try {
            sortida = new ObjectOutputStream(client.getOutputStream());
            entrada = new ObjectInputStream(client.getInputStream());

            while (!sortir) {
                String missatge = (String) entrada.readObject();
                processaMissatge(missatge);
            }

            client.close();
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            sortida.writeObject(Missatge.getMissatgePersonal(nom, "(" +remitent + ") : " + missatge));
            sortida.flush();
        } catch (IOException e) {
            System.out.println("oos null. Sortint...");
        }
    }

    public void enviarMissatgeBrut(String missatge) {
        try {
            sortida.writeObject(missatge);
            sortida.flush();
        } catch (IOException e) {
            System.out.println("oos null. Sortint...");
        }
    }

    public void processaMissatge(String missatge){
        String codi = Missatge.getCodiMissatge(missatge);
        String[] parts = Missatge.getPartsMissatge(missatge);

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidorXat.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidorXat.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                String destinatari = parts[1];
                String msg = parts[2];
                servidorXat.enviarMissatgePersonal(destinatari, nom, msg);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidorXat.enviarMissatgeGrup(parts[1]);
                break;
            default:
                System.out.println("Codi incorrecte: " + codi);
        }
    }
}