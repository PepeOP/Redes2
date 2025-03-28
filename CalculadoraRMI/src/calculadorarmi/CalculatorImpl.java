
package calculadorarmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator {
    protected CalculatorImpl() throws RemoteException {
        super();
    }

    @Override
    public double add(double a, double b) throws RemoteException {
        System.out.println("Servidor: Realizando suma " + a + " + " + b);
        return a + b;
    }

    @Override
    public double subtract(double a, double b) throws RemoteException {
        System.out.println("Servidor: Realizando resta " + a + " - " + b);
        return a - b;
    }

    @Override
    public double multiply(double a, double b) throws RemoteException {
        System.out.println("Servidor: Realizando multiplicación " + a + " * " + b);
        return a * b;
    }

    @Override
    public double divide(double a, double b) throws RemoteException {
        if (b == 0) {
            throw new RemoteException("División por cero");
        }
        System.out.println("Servidor: Realizando división " + a + " / " + b);
        return a / b;
    }
}


