package client;


import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.border.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.Arrays;

import server.IChatServer;

public class ChatClient extends JFrame implements IChatClientCallback {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final int callbackPort = 0;
    private IChatServer serverObject;
    private String userName;

    // GUI Variablen
    private JMenuBar jmenu;
    private JMenu fileMenu;
    private JMenuItem exitMenuItem;
    private JMenu commandsMenu;
    private JMenuItem loginMenuItem;
    private JMenuItem logoutMenuItem;
    private JMenuItem clearMenuItem;
    private JMenu viewMenu;
    private JPanel eastPanel;
    private JTextArea userList;
    private JPanel southPanel;
    private JTextField inputField;
    private JPanel centerPanel;
    private JScrollPane jScrollPane;
    private JTextArea textArea;


    /**
     * Constructor for the ChatClient object
     *
     * @param serverAddress Die Adresse des Chat-Servers
     */
    public ChatClient(String serverAddress) {

        // Setup GUI
        this.setBounds(25, 18, 500, 400);
        jmenu = new JMenuBar();
        this.setJMenuBar(jmenu);
        fileMenu = new JMenu("File");
        jmenu.add(fileMenu);
        exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ExitMenuItemListener());
        commandsMenu = new JMenu("Commands");
        jmenu.add(commandsMenu);
        loginMenuItem = new JMenuItem("Login");
        commandsMenu.add(loginMenuItem);
        loginMenuItem.addActionListener(new LoginMenuItemListener());
        logoutMenuItem = new JMenuItem("Logout");
        commandsMenu.add(logoutMenuItem);
        logoutMenuItem.addActionListener(new LogoutMenuItemListener());
        logoutMenuItem.setEnabled(false);
        clearMenuItem = new JMenuItem("Clear");
        commandsMenu.add(clearMenuItem);
        clearMenuItem.addActionListener(new ClearMenuItemListener());
        viewMenu = new JMenu("Look & Feel");

        ButtonGroup buttonGroup = new ButtonGroup();
        final UIManager.LookAndFeelInfo[] info =
                UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < info.length; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(info[i].getName(), i == 0);
            final String className = info[i].getClassName();
            item.addActionListener(new ViewMenuItemListener(className));
            buttonGroup.add(item);
            viewMenu.add(item);
        }

        jmenu.add(viewMenu);

        Container thisContent = this.getContentPane();
        this.setFont(new Font("dialog", 0, 12));
        this.setTitle("RMI-Chat");

        eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(100, 10));
        Border eastPanelBorder0 = new EtchedBorder();
        eastPanel.setBorder(eastPanelBorder0);
        eastPanel.setMinimumSize(new Dimension(8, 25));

        userList = new JTextArea();
        Border userListBorder0 = new EmptyBorder(2, 2, 2, 2);
        userList.setBorder(userListBorder0);
        userList.setEditable(false);
        userList.setFont(new Font("dialog", Font.BOLD, 12));

        BorderLayout eastPanelLayout = new BorderLayout();
        eastPanel.setLayout(eastPanelLayout);
        eastPanel.add(userList, BorderLayout.CENTER);

        southPanel = new JPanel();
        southPanel.setPreferredSize(new Dimension(10, 30));
        Border southPanelBorder0 = new EtchedBorder();
        southPanel.setBorder(southPanelBorder0);

        inputField = new JTextField();
        Border inputFieldBorder0 = new EmptyBorder(2, 2, 2, 2);
        inputField.setBorder(inputFieldBorder0);
        inputField.setLayout(null);
        inputField.addActionListener(new InputFieldListener());

        BorderLayout southPanelLayout = new BorderLayout();
        southPanel.setLayout(southPanelLayout);
        southPanel.add(inputField, BorderLayout.CENTER);

        centerPanel = new JPanel();
        Border centerPanelBorder0 = new EtchedBorder();
        centerPanel.setBorder(centerPanelBorder0);

        jScrollPane = new JScrollPane();
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        textArea = new JTextArea();
        Border textAreaBorder0 = new EmptyBorder(2, 2, 2, 2);
        textArea.setBorder(textAreaBorder0);
        textArea.setEditable(false);
        textArea.setFont(new Font("dialog", Font.BOLD, 12));

        jScrollPane.getViewport().setView(textArea);

        BorderLayout centerPanelLayout = new BorderLayout();
        centerPanel.setLayout(centerPanelLayout);
        centerPanel.add(jScrollPane, BorderLayout.CENTER);

        BorderLayout thisContentLayout = new BorderLayout();
        thisContent.setLayout(thisContentLayout);
        thisContent.add(eastPanel, BorderLayout.EAST);
        thisContent.add(southPanel, BorderLayout.SOUTH);
        thisContent.add(centerPanel, BorderLayout.CENTER);

        this.addWindowListener(new MyWindowListener());

        // Hier m�ssen Sie Server finden und Ihren Callback bekannt machen
        try {
//            System.setProperty("java.rmi.server.hostname", serverAddress);
            UnicastRemoteObject.exportObject(this, callbackPort);

//            serverObject = (IChatServer) Naming.lookup("rmi://localhost:1099/ChatServer");

            String path = "resources/";

            System.setProperty("javax.net.ssl.trustStore", path + "truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "the!truststore");
            System.setProperty("javax.net.ssl.keyStore", path + "client-ks.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "the!client");
            // Get reference to the RMI registry running on port 1099 in the local
            // host
            Registry registry = LocateRegistry.getRegistry(null, 1099, new SslRMIClientSocketFactory());
            // Lookup the remote reference bound to the name "HelloServer"
            serverObject = (IChatServer) registry.lookup("ChatServer");
//            serverObject.login("EpicUser", this);
        } catch (Exception e) {
            System.out.println("Exception" + e);
            e.printStackTrace();
            return;
        }
    }


    /**
     * Callback-Methode
     * Wird vom Server aufgerufen, wenn jemand eine Nachricht verschickt
     *
     * @param userID  Description of Parameter
     * @param message Description of Parameter
     * @throws RemoteException Description of Exception
     */
    public void receiveChat(String userID, String message) throws RemoteException {
        textArea.append("[" + userID + "]: " + message + "\n");
    }


    /**
     * Callback-Methode
     * Diese wird vom Server aufgerufen, wenn es einen neuen Benutzer gibt
     *
     * @param userID Description of Parameter
     * @param users  Description of Parameter
     * @throws RemoteException Description of Exception
     */
    public void receiveUserLogin(String userID, Object[] users) throws RemoteException {
        textArea.append("[" + userID + "]: ... ist dem Chat beigetreten.\n");
        StringBuilder sb = new StringBuilder();
        for (Object o: users){
            sb.append(o.toString()).append("\n");
        }
        userList.setText(sb.toString());
    }


    /**
     * Callback-Methode
     * Diese wird vom Server aufgerufen, wenn ein Benutzer das System verl�sst
     *
     * @param userID Description of Parameter
     * @param users  Description of Parameter
     * @throws RemoteException Description of Exception
     */
    public void receiveUserLogout(String userID, Object[] users) throws RemoteException {
        textArea.append("[" + userID + "]: ... hat den Chat verlassen.\n");
        StringBuilder sb = new StringBuilder();
        for (Object o: users){
            sb.append(o.toString()).append("\n");
        }
        userList.setText(sb.toString());
    }


    /**
     * Und hier die main Klasse unseres Chat-Servers
     *
     * @param args k�nnte man noch die Adresse des Servers einf�gen
     */
    public static void main(String[] args) {

        ChatClient chat = new ChatClient("localhost");
        chat.setVisible(true);
    }


    /**
     * Wenn wir das Fenster schliessen, dann ein logout
     */
    class MyWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            try {
                // ruft eine entfernte Methode auf
                serverObject.logout(userName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }


    class ViewMenuItemListener implements ActionListener {
        String className;

        public ViewMenuItemListener(String className) {
            this.className = className;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(className);
            } catch (Exception ex) {
                System.out.println(ex);
            }
            SwingUtilities.updateComponentTreeUI(ChatClient.this);
        }
    }

    class ExitMenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                // f�hre logout durch
                serverObject.logout(userName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }

    class LoginMenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(ChatClient.this, "Please enter a nickname.",
                    "LOGIN", JOptionPane.PLAIN_MESSAGE);
            try {
                // F�hre Login durch
                textArea.setText("");
                if (serverObject.login(name, ChatClient.this)) {
                    // falls erfolgreich
                    userName = name;
                    loginMenuItem.setEnabled(false);
                    logoutMenuItem.setEnabled(true);
                } else {
                    textArea.setText("This name is already in use." + "\n"
                            + "Please choose another name.");
                    loginMenuItem.setEnabled(true);
                }
            } catch (Exception ex) {
                System.err.println("Exception " + ex);
            }
            inputField.setText("");
        }
    }

    class LogoutMenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                // Logout
                if (serverObject.logout(userName)) {
                    textArea.append(userName + " logged out" + "\n");
                    userName = "";
                    userList.setText("");
                    loginMenuItem.setEnabled(true);
                    logoutMenuItem.setEnabled(false);
                } else {
                    textArea.append("Logout-Error" + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class ClearMenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            textArea.setText("");
        }
    }

    class InputFieldListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String message = inputField.getText();
            if (message.length() > 0) {
                try {
                    // Hier chatten wir.
                    serverObject.chat(userName, message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                inputField.setText("");
            }
        }
    }
}
