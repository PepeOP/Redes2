
package calculadorarmi;

/**
 *
 * @author reyjo
 */
import java.rmi.Naming;

public class CalculadoraRMI {
    public static void main(String[] args) {
        try {
            Calculator calculator = (Calculator) Naming.lookup("rmi://localhost/CalculatorService");
            
            // Realiza llamadas a los métodos remotos
            double a = 10;
            double b = 5;
            System.out.println("Suma: " + calculator.add(a, b));
            System.out.println("Resta: " + calculator.subtract(a, b));
            System.out.println("Multiplicación: " + calculator.multiply(a, b));
            System.out.println("División: " + calculator.divide(a, b));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
