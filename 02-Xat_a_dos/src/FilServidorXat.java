import java.io.IOException;
import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream in;
    private String nom;

    public FilServidorXat(ObjectInputStream in, String nom) {
        this.in = in;
        this.nom = nom;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while (!(missatge = (String) in.readObject()).equalsIgnoreCase(ServidorXat.MSG_SORTIR)) {
                System.out.println("Rebut: " + missatge);
            }
            System.out.println("Fil de xat finalitzat.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
