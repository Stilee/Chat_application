
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

//TODO: MAKE SEPERATE CLASS FOR GUI AND CLIENT

public class Client extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JTextField txtMessage;
    private JTextArea history;
    private DefaultCaret caret;

    //TCP will be better
    private DatagramSocket socket;
    private String name, address;
    private InetAddress ip;
    private int port;
    private Thread send;

    public Client(String name, String address, int port) {
        setTitle("Chat Client");
        this.name = name;
        this.address = address;
        this.port = port;
        boolean connect = openConnection(address);
        if(!connect){
            System.err.println("Connection failed!");
            console("Connection failed");
        }
        createWindow();
        console("Attempting a connection to " + address + ":" + port + ", user:" + name);
        String connection = "/c/" + name;
        send(connection.getBytes());
    }


    private boolean openConnection(String address){
        try{
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        }catch (UnknownHostException e){
            e.printStackTrace();
            return false;
        }catch (SocketException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //TODO: do ogarnięcia
    private String receive(){
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data,data.length);
        try{
            socket.receive(packet);
        }catch (IOException e){
            e.printStackTrace();
        }
        String message = new String(packet.getData());
        return message;
    }


    private void send(final byte[] data){
        send = new  Thread("Send"){
            public void run(){
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();

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

        setVisible(true);
        txtMessage.requestFocusInWindow();


        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(txtMessage.getText());
            }
        });

        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    send(txtMessage.getText());
                }
            }
        });



    }

    public void console(String message){
        history.append(message + "\n\r");
        history.setCaretPosition(history.getDocument().getLength());
    }

    public void send (String message){
        if(message.equals("")) return;
        message = name +": "+ message;
        console(message);
        message= "/m/" + message;
        send(message.getBytes());
        txtMessage.setText("");

        txtMessage.requestFocusInWindow();
    }



}