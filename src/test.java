import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class test {
    private JTextPane textPane1;
    private JTextArea textArea1;
    private JTextArea textArea2;

    public test() {
        textArea2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
    }
}
