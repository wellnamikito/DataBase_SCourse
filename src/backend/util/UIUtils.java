package backend.util;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UIUtils {

    public static void enableEnterToNextField(Container container) {
        for (Component c : container.getComponents()) {

            if (c instanceof JTextField || c instanceof JFormattedTextField) {
                ((JComponent) c).getInputMap().put(
                        KeyStroke.getKeyStroke("ENTER"),
                        "focusNext"
                );

                ((JComponent) c).getActionMap().put("focusNext",
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                c.transferFocus();
                            }
                        }
                );
            }

            if (c instanceof Container) {
                enableEnterToNextField((Container) c);
            }
        }
    }
}
