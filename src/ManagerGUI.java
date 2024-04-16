import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ManagerGUI extends JFrame {

    private JPanel contentPane;
    private JLabel lblNewLabel;
    private JTextField textField;
    private JTabbedPane tabbedPane;
    private JButton btnNewButton;
    private JLabel lblNewLabel_2;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ManagerGUI frame = new ManagerGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ManagerGUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Manager");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 835, 674);
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.add(getLblNewLabel());
        contentPane.add(getTextField());
        contentPane.add(getTabbedPane());
        contentPane.add(getBtnNewButton());
    }

    public JLabel getLblNewLabel() {
        if (lblNewLabel == null) {
            lblNewLabel = new JLabel("Manager Port");
            lblNewLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
            lblNewLabel.setBounds(202, 22, 148, 53);
        }
        return lblNewLabel;
    }

    public JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 22));
            textField.setBounds(331, 27, 144, 46);
            textField.setColumns(10);
        }
        return textField;
    }

    public JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
            tabbedPane.setBorder(null);
            tabbedPane.setBounds(12, 73, 805, 521);
            tabbedPane.addTab(null, null, getLblNewLabel_2(), null);
        }
        return tabbedPane;
    }

    public JButton getBtnNewButton() {
        if (btnNewButton == null) {
            btnNewButton = new JButton("START SERVER");
            btnNewButton.setBackground(Color.WHITE);
            btnNewButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            btnNewButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    int port = 8; // Default port
                    try {
                        port = Integer.parseInt(textField.getText());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(contentPane, "Can't start at this port, program will use the default port=8\nDetails: " + e,
                                "Error while read Port", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        ServerSocket serverSocket = new ServerSocket(port);
                        JOptionPane.showMessageDialog(contentPane, "Server is running at port: " + port, "Started server",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Start a new thread to wait for client connections
                        Thread serverThread = new Thread(new Runnable() {
                            public void run() {
                                while (true) {
                                    try {
                                        Socket clientSocket = serverSocket.accept();
                                        if (clientSocket != null) {
                                            // Handle client connection
                                            handleClientConnection(clientSocket);
                                        }
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        serverThread.start();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(contentPane, "Details: " + e, "Start server error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnNewButton.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
            btnNewButton.setBounds(487, 25, 167, 47);
        }
        return btnNewButton;
    }

    public JLabel getLblNewLabel_2() {
        if (lblNewLabel_2 == null) {
            lblNewLabel_2 = new JLabel("Waitting for client");
            lblNewLabel_2.setBackground(Color.WHITE);
            lblNewLabel_2.setForeground(Color.RED);
            lblNewLabel_2.setFont(new Font("Arial Unicode MS", Font.BOLD, 28));
            lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return lblNewLabel_2;
    }

    // Handle client connection
    private void handleClientConnection(Socket clientSocket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Read the name of the client
            String staffName = br.readLine();
            staffName = staffName.substring(0, staffName.indexOf(":"));

            // Create ChatPanel and display it in TabbedPane
            ChatPanel chatPanel = new ChatPanel(clientSocket, "Manager", staffName);
            tabbedPane.add(staffName, chatPanel);
            chatPanel.updateUI();

            // Start a thread to check for messages from the client
            Thread chatThread = new Thread(chatPanel);
            chatThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
