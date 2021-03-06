import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;


public class ClientWindow  extends JFrame implements Runnable{
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextArea history;
    private DefaultCaret caret;
    private Client client;

    private Thread run, listen;
    private boolean running = false;



    public ClientWindow(String name, String address, int port) {
        setTitle("Chat Client");
        client = new Client(name, address, port);
        boolean connect = client.openConnection(address);
        if(!connect){
            System.err.println("Connection failed!");
            console("Connection failed");
        }
        createWindow();
        console("Attempting a connection to " + address + ":" + port + ", user:" + name);
        String connection = "/c/" + name;
        client.send(connection.getBytes());
        run = new Thread(this, "Running");
        running = true;
        run.start();
    }

    private void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 550);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7}; // SUM = 880
        gbl_contentPane.rowHeights = new int[]{35, 475, 40}; // SUM = 550
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
        gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        history = new JTextArea();
        history.setEditable(false);
        caret = (DefaultCaret)history.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroll = new JScrollPane(history);
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.insets = new Insets(0, 0, 5, 5);
        scrollConstraints.fill = GridBagConstraints.BOTH;
        scrollConstraints.gridx = 0;
        scrollConstraints.gridy = 0;
        scrollConstraints.gridwidth = 3;
        scrollConstraints.gridheight =2;
        scrollConstraints.insets = new Insets(0, 5, 0, 0);
        contentPane.add(scroll, scrollConstraints);

        txtMessage = new JTextField();
        GridBagConstraints gbc_txtMessage = new GridBagConstraints();
        gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
        gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMessage.gridx = 0;
        gbc_txtMessage.gridy = 2;
        gbc_txtMessage.gridwidth = 2;
        contentPane.add(txtMessage, gbc_txtMessage);
        txtMessage.setColumns(10);

        JButton btnSend = new JButton("Send");
        GridBagConstraints gbc_btnSend = new GridBagConstraints();
        gbc_btnSend.insets = new Insets(0, 0, 0, 5);
        gbc_btnSend.gridx = 2;
        gbc_btnSend.gridy = 2;
        contentPane.add(btnSend, gbc_btnSend);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                String disconnect = "/d/"+client.getID() + "/e/";
                send(disconnect,false);
                client.close();
                running=false;
            }
        });

        setVisible(true);
        txtMessage.requestFocusInWindow();


        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(txtMessage.getText(),true);
            }
        });

        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    send(txtMessage.getText(),true);
                }
            }
        });

    }

    public void listen(){
        listen = new Thread("Listen"){
            public void run(){
                while(true) {
                    String message = client.receive();
                    if (message.startsWith("/c/")){
                        client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
                        console("Successfully connected to server! ID: " + client.getID());
                    }else if(message.startsWith("/m/")){
                        String text = message.substring(3);
                        text = text.split("/e/")[0];
                        console(text);
                    }else if(message.startsWith("/i/")){
                        String text = "/i/" + client.getID() +"/e/";
                        send(text,false);
                    }
                }
            }
        };
        listen.start();
    }


    public void console(String message){
        history.append(message + "\n\r");
        history.setCaretPosition(history.getDocument().getLength());
    }

    public void run(){
        listen();
    }


    public void send (String message, boolean text){
        if(message.equals("")) return;
        if(text){
            message = client.getName() +": "+ message;
            message= "/m/" + message;
        }

        client.send(message.getBytes());
        txtMessage.setText("");

        txtMessage.requestFocusInWindow();
    }
}
