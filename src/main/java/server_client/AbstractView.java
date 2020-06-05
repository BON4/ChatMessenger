package server_client;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractView extends JPanel {
    protected static ServerMessengerApplication parent;

    public static void setParent(ServerMessengerApplication parent) {
        AbstractView.parent = parent;
    }

    public AbstractView(){
        super();
    }

    public abstract void initialize();
    public abstract void clearFields();
    //public abstract void initModel();

    protected void addLabeledField(JPanel panel, String lableText, Component field){
        JLabel label = new JLabel(lableText);
        label.setLabelFor(field);
        panel.add(label);
        panel.add(field);
    }
}
