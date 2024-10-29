package co.com.designer.kiosko.generales;

import javax.mail.Address;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

/**
 *
 * @author Edwin Hastamorir
 */
public class EscuchaTransporte implements ConnectionListener, TransportListener {
    @Override
    public void opened(ConnectionEvent ce) {
        // Exigido por ConnectionListener
        System.out.println(">>> ConnectionListener.opened()");
    }

    @Override
    public void disconnected(ConnectionEvent ce) {
        // Exigido por ConnectionListener
        System.out.println(">>> ConnectionListener.disconnected()");
    }

    @Override
    public void closed(ConnectionEvent ce) {
        // Exigido por ConnectionListener
        System.out.println(">>> ConnectionListener.closed()");
    }

    @Override
    public void messageDelivered(TransportEvent e) {
        // Exigido por TransportListener
        System.out.println(">>> TransportListener.messageDelivered().");
        System.out.println(" Valid Addresses:");
        Address[] valid = e.getValidSentAddresses();
        if (valid != null) {
            for (int i = 0; i < valid.length; i++) {
                System.out.println("    " + valid[i]);
            }
        }
    }

    @Override
    public void messageNotDelivered(TransportEvent e) {
        // Exigido por TransportListener
        System.out.println(">>> TransportListener.messageNotDelivered()");
        System.out.println(" Invalid Addresses:");
        Address[] invalid = e.getInvalidAddresses();
        if (invalid != null) {
            for (int i = 0; i < invalid.length; i++) {
                System.out.println("    " + invalid[i]);
            }
        }
    }

    @Override
    public void messagePartiallyDelivered(TransportEvent e) {
        // Exigido por TransportListener
        System.out.println(">>> TransportListener.messagePartiallyDelivered().");
        System.out.println(" Valid Addresses:");
        Address[] valid = e.getValidSentAddresses();
        if (valid != null) {
            for (int i = 0; i < valid.length; i++) {
                System.out.println("    " + valid[i]);
            }
        }
        System.out.println(" Valid Unsent Addresses:");
        Address[] unsent = e.getValidUnsentAddresses();
        if (unsent != null) {
            for (int i = 0; i < unsent.length; i++) {
                System.out.println("    " + unsent[i]);
            }
        }
        System.out.println(" Invalid Addresses:");
        Address[] invalid = e.getInvalidAddresses();
        if (invalid != null) {
            for (int i = 0; i < invalid.length; i++) {
                System.out.println("    " + invalid[i]);
            }
        }
    }

}

