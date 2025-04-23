import java.io.IOException;
import java.io.ObjectInputStream;

public class FilLectorCX extends Thread {
    private ObjectInputStream in;

    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fil de lectura iniciat");
            String missatge;
            while (!(missatge = (String) in.readObject()).equalsIgnoreCase("sortir")) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}
