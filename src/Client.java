import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Client extends JFrame{
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private String name, address;
    private  int port;

    public Client(String name, String address, int port){
        this.name = name;
        this.address = address;
        this. port = port;
        createWindow();


    }


    private void createWindow(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Client Chat");
        setSize(900,500);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout(0,0));
        setContentPane(contentPane);
        setVisible(true);



    }


}
