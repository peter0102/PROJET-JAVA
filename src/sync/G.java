package sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;

import java.net.ServerSocket;
import java.net.Socket;


/* Définir la classe G */
public class G extends JFrame {   
	private boolean isActive = true;
    private JFrame frmeSynchronize;
    private JFrame frame;
    
    private JTextField sourceTextField;
    private JTextField destinationTextField;
    private JTextField clientPortTextField;
    private JTextField clientIPTextField;
	private JTextField serverTextField;
	private JTextField textFieldServer;
    
    private JPanel panel;
    private JPanel mainPanel;
    private JPanel clientPanel;
    private JPanel serverPanel;
    private JPanel cardPanel;
    
    private JLabel clientIPLabel;
    private JLabel clientStateLabel;
    private JLabel clientActualStateLabel;
    private JLabel serverLabel;
    private JLabel lblServer;
    
    private CardLayout myLayout;
    
    private JButton clientButton;
    private JButton serverButton;
    private JButton serverBackButton;
    private JButton clientBackButton;
    private JButton clientConnectButton;
    
    /* Lancer l'application */

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    G window = new G();
                    window.frmeSynchronize.setVisible(true); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* Créer l'application */

    public G(){
        createG();
        panel = new JPanel();
        myLayout = new CardLayout();
        mainPanel = new JPanel();
        clientPanel = new JPanel();
        serverPanel = new JPanel();
        clientIPLabel = new JLabel("IP Address: ");
        clientIPTextField = new JTextField();
        clientStateLabel = new JLabel("State: ");
        clientActualStateLabel = new JLabel("Not connected");
        clientBackButton = new JButton("Back");
        clientConnectButton = new JButton("Connect");
        serverLabel = new JLabel("Server Address: ");
        serverBackButton = new JButton("Back");
        lblServer = new JLabel("Server Address: ");
        serverTextField = new JTextField();
        cardPanel = new JPanel();
        initialize();
    }
    
    /* Créer et ajouter les composants à la fenêtre */
    private void createG(){
        /* Créer la fenêtre principale */
        frmeSynchronize= new JFrame("Ma fenêtre");

        /* Créer un panneau */
        JPanel panel = new JPanel();

        /* Ajouter un champ de texte */
        JTextField textField = new JTextField("Entrez votre texte ici");
        panel.add(textField);

        /* Ajouter un bouton */
        JButton button = new JButton("Cliquez ici");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Action à effectuer lors du clic sur le bouton */ 
                System.out.println("Le bouton a été cliqué !");
            }
        });
        panel.add(button);

        /* Ajouter le panneau à la fenêtre */
        frmeSynchronize.getContentPane().add(panel);

        /* Afficher la fenêtre */
        frmeSynchronize.setVisible(true);
    }

    /* Initialiser les composants de la fenêtre */
    private void initialize(){
        /* Définir la tailler de la fenêtre  */
		frmeSynchronize = new JFrame();
		frmeSynchronize.setResizable(false);
		frmeSynchronize.setTitle("Synchronize");
        frmeSynchronize.setBounds(100, 100, 489, 257);        
		frmeSynchronize.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmeSynchronize.getContentPane().setLayout(new CardLayout(0, 0));

        /* créer un panneau */
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        frmeSynchronize.getContentPane().add(cardPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        cardPanel.add(mainPanel, "Main");

        /* Ajouter un label */
        JLabel lblSource = new JLabel("Source:");
        lblSource.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblSource.setBounds(42, 49, 46, 14);
        mainPanel.add(lblSource);

        /* Ajouter un champ de texte */
        sourceTextField = new JTextField();
        sourceTextField.setBounds(98, 47, 307, 20);
        mainPanel.add(sourceTextField);
        sourceTextField.setColumns(10);

        /* Ajouter un label */
        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDestination.setBounds(21, 93, 81, 14);
        mainPanel.add(lblDestination);

        /* Ajouter un champ de texte */
        destinationTextField = new JTextField();
        destinationTextField.setBounds(98, 91, 307, 20);
        mainPanel.add(destinationTextField);
        destinationTextField.setColumns(10);

        /* Ajouter un bouton */
        JButton btnClient = new JButton("Client");
        btnClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                cardLayout.show(cardPanel, "Client");
            }
        });
        btnClient.setBounds(138, 233, 140, 30);
        mainPanel.add(btnClient);

        JButton btnServeur = new JButton("Serveur");
        btnServeur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                cardLayout.show(cardPanel, "Serveur");
            }
        });
        btnServeur.setBounds(288, 233, 140, 30);

        /* Ajouter un panneau pour le client */
        clientPanel.setLayout(null);
        JLabel clientIPLabel = new JLabel("Enter server IP address:");
        clientIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
        clientIPLabel.setBounds(28, 54, 258, 32);
        clientPanel.add(clientIPLabel);

        clientIPTextField = new JTextField();
        clientIPTextField.setColumns(10);
        clientIPTextField.setBounds(296, 54, 153, 32);
        clientPanel.add(clientIPTextField);

        JLabel clientStateLabel = new JLabel("State :");
        clientStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        clientStateLabel.setBounds(152, 161, 98, 32);
        clientPanel.add(clientStateLabel);

        JLabel clientActualStateLabel = new JLabel("");
        clientActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        clientActualStateLabel.setBounds(220, 161, 276, 35);
        clientPanel.add(clientActualStateLabel);

        JButton clientBackButton = new JButton("Back");
        clientBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myLayout.show(cardPanel, "Main");
            }
        });
        clientBackButton.setBounds(10, 13, 85, 21);
        clientPanel.add(clientBackButton);

        JButton clientConnectButton = new JButton("Connect");

        clientConnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String serverIP = clientIPTextField.getText();
                int serverPort = Integer.parseInt(clientPortTextField.getText());
                boolean connected = connectToServer(serverIP, serverPort);
            if (connected) {
                clientActualStateLabel.setText("Connected to server");
            } else {
                clientActualStateLabel.setText("Connection failed");
            }
        }});
        clientConnectButton.setBounds(296, 174, 153, 32);
        clientPanel.add((Component) clientConnectButton);
        

        /* Ajouter un panneau pour le serveur */
        serverPanel.setLayout(new BorderLayout());
        JLabel serverLabel = new JLabel("Server Panel");
        serverLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        new JLabel("Server Panel");
        serverLabel.setBounds(166, 10, 143, 42);
        serverPanel.add((Component) serverLabel);
 
        JButton serverBackButton = new JButton("Back");
        serverBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myLayout.show(cardPanel, "Main");
            }
         });
        serverBackButton.setBounds(10, 13, 85, 21);
        serverPanel.add(serverBackButton);
 
        /* Ajouter un panneau pour l'interface principale */
        mainPanel.setLayout(null);
        panel.add(mainPanel, "Main");
 
        /* Ajouter un label */
        JLabel lblSynchronizeFiles = new JLabel("Synchronize Files");
        lblSynchronizeFiles.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSynchronizeFiles.setBounds(147, 11, 153, 30);
        mainPanel.add(lblSynchronizeFiles);
 
        /* Ajouter un bouton */
        JButton btnServer = new JButton("Server");
        btnServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myLayout.show(cardPanel, "Server");
            }
        });
         btnServer.setBounds(138, 196, 140, 30);
        mainPanel.add(btnServer);
 
        /* Ajouter un bouton */
        JButton btnSynchronize = new JButton("Synchronize");
        btnSynchronize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 /* Action à effectuer lors du clic sur le bouton */ 
                 System.out.println("Synchronizing files...");
            }
        });
        btnSynchronize.setBounds(138, 147, 140, 30);
        mainPanel.add(btnSynchronize);
 
        /* Ajouter un label */
        JLabel lblServer = new JLabel("Server:");
        lblServer.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblServer.setBounds(42, 137, 46, 14);
        panel.add(lblServer);
    
        /* Ajouter un champ de texte */
        serverTextField = new JTextField();
        serverTextField.setBounds(98, 135, 307, 20);
        panel.add(serverTextField);
        serverTextField.setColumns(10);

		CardLayout myLayout = new CardLayout();
		
		frmeSynchronize.getContentPane().add(cardPanel, "Base");
		
		cardPanel.add(mainPanel, "Main");
		
		cardPanel.add(clientPanel,"Client");
		
		JPanel serverPanel = new JPanel();
		cardPanel.add(serverPanel,"Server");
		serverPanel.setLayout(null);
		
		serverLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		serverLabel.setBounds(29, 44, 215, 32);
		serverPanel.add(serverLabel);
		
		serverTextField = new JTextField();
		serverTextField.setBounds(254, 48, 169, 33);
		serverPanel.add(serverTextField);
		serverTextField.setColumns(10);

		JLabel serverStateLabel = new JLabel("State : ");
		serverStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverStateLabel.setBounds(118, 103, 98, 32);
		serverPanel.add(serverStateLabel);
		
		JLabel serverActualStateLabel = new JLabel("");
		serverActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverActualStateLabel.setBounds(189, 105, 276, 35);
		serverPanel.add(serverActualStateLabel);
		
		serverBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
			}
		});
		serverBackButton.setBounds(10, 13, 85, 21);
		serverPanel.add(serverBackButton);
		
		myLayout.show(cardPanel, "Main");
		
		JButton clientButton = new JButton("Client");
		clientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Client");
			}
		});
		mainPanel.setLayout(null);
		clientButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		clientButton.setBounds(65, 110, 130, 56);
		mainPanel.add(clientButton);
		
		JButton serverButton = new JButton("Server");
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Server");
			}
		});
		serverButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		serverButton.setBounds(270, 110, 130, 56);
		mainPanel.add(serverButton);
		
		JLabel mainLabel = new JLabel("Please choose between client and server.");
		mainLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mainLabel.setForeground(new Color(0, 0, 0));
		mainLabel.setBounds(54, 45, 366, 25);
		mainPanel.add(mainLabel);
		clientPanel.setLayout(null);
			
		JLabel stateLabel = new JLabel("");
		stateLabel.setBounds(217, 138, 141, 14);
		clientPanel.add(stateLabel);

		JLabel errorLabel = new JLabel("");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setBounds(47, 162, 415, 14);
		clientPanel.add(errorLabel);

		clientIPLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		clientIPLabel.setBounds(197, 107, 105, 29);
		clientPanel.add(clientIPLabel);
		
		clientIPTextField = new JTextField();
		clientIPTextField.setColumns(10);
		clientIPTextField.setBounds(281, 114, 127, 20);
		clientPanel.add(clientIPTextField);
	
		JButton serverLaunchButton = new JButton("Launch");
		serverLaunchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        try {
					if (serverTextField.getText().equals("")) {
						serverActualStateLabel.setText("Please enter a port");
					}
					if (serverTextField.getText() instanceof String) {
						serverActualStateLabel.setText("Please enter a valid port");
					}
		        	int port = Integer.parseInt(serverTextField.getText());
		            ServerSocket serverSocket = new ServerSocket(port); // create server socket
		            Socket clientSocket = serverSocket.accept(); // wait for client to connect
		            System.out.println("Connection established!"); // print message to console
					serverActualStateLabel.setText("Connection established!");
					DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
					String sourceFolderPath = dataInputStream.readUTF();
					String destinationFolderPath = dataInputStream.readUTF();
					File destDir = new File(destinationFolderPath);
					if (!destDir.exists()) {
						destDir.mkdirs();
					}
					File sourceDir = new File(sourceFolderPath);
					copy(sourceDir, destDir,0,0);

					DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
					dataOutputStream.writeUTF("Folder copied successfully!");

		        }
		        catch (IOException f) {
		            System.out.println("Error: " + f);
		        }
			}
		});
		serverLaunchButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		serverLaunchButton.setBounds(166, 161, 150, 49);
		serverPanel.add(serverLaunchButton);

		JButton startButton = new JButton("Synchronize");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sourceFolderPath = sourceTextField.getText();
				String destinationFolderPath = destinationTextField.getText();
				File sourceDir = new File(sourceFolderPath);
				File destDir = new File(destinationFolderPath);
		        try {
					FileInputStream fileInputStream = new FileInputStream(sourceFolderPath);
		        	int port = Integer.parseInt(clientPortTextField.getText());
					String IP = clientIPTextField.getText();
		            Socket socket = new Socket(IP, port); // create client socket and connect to server
		            System.out.println("Connection established!"); // print message to console
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataOutputStream.write(sourceFolderPath.getBytes());
					dataOutputStream.write(destinationFolderPath.getBytes());
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					String response = dataInputStream.readUTF();

					System.out.println(response);

		        }
		        catch (IOException f) {
		            System.out.println("Error: " + f);
		        }

				if (sourceFolderPath.isEmpty() || destinationFolderPath.isEmpty()) {
					errorLabel.setText("Error: Please fill both source and destination folders.");
					return;
				}
				if (!sourceDir.exists() || !sourceDir.isDirectory()) {
					errorLabel.setText("");
					errorLabel.setText("Error: Invalid source folder path");
					return;
				}

				if (!destDir.exists() || !destDir.isDirectory()) {
					errorLabel.setText("");
					errorLabel.setText("Error: Invalid destination folder path");
					return;
				} else {
					if (isActive) {
						Sync.main(new String[] { sourceFolderPath, destinationFolderPath });
						stateLabel.setText("Synchronized");
						errorLabel.setText("");
						startButton.setEnabled(false);
					} else {
						stateLabel.setText("Not Synchronized");
					}

				}
			}
		});
		startButton.setBounds(47, 186, 160, 23);
		clientPanel.add(startButton);

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sync.stopSync();
				stateLabel.setText("Not Synchronized");
				startButton.setEnabled(true);
			}
		});
		stopButton.setBounds(289, 186, 160, 23);
		clientPanel.add(stopButton);

		JLabel stateLabelInitial = new JLabel("State :");
		stateLabelInitial.setBounds(156, 138, 75, 14);
		clientPanel.add(stateLabelInitial);

		JButton sourceButton = new JButton("Select source folder :");
		sourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Source Folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(frmeSynchronize);
				if (result == JFileChooser.APPROVE_OPTION) {
					sourceTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		sourceButton.setBounds(5, 41, 192, 23);
		clientPanel.add(sourceButton);

		JButton destinationButton = new JButton("Select destination folder :");
		destinationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Destination Folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(frmeSynchronize);
				if (result == JFileChooser.APPROVE_OPTION) {
					destinationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		destinationButton.setBounds(5, 74, 192, 23);
		clientPanel.add(destinationButton);

		sourceTextField = new JTextField();
		sourceTextField.setBounds(207, 43, 242, 20);
		clientPanel.add(sourceTextField);
		sourceTextField.setColumns(10);

		destinationTextField = new JTextField();
		destinationTextField.setBounds(208, 76, 241, 20);
		clientPanel.add(destinationTextField);
		destinationTextField.setColumns(10);
		
		JLabel clientPortLabel = new JLabel("Port : ");
		clientPortLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		clientPortLabel.setBounds(15, 107, 88, 29);
		clientPanel.add(clientPortLabel);
		
		clientPortTextField = new JTextField();
		clientPortTextField.setColumns(10);
		clientPortTextField.setBounds(60, 114, 127, 20);
		clientPanel.add(clientPortTextField);
		
		clientBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
			}
		});

        }
 
     private boolean connectToServer(String serverIP, int serverPort) {
             return true;
        }
        
    public void copy(File source, File destination, int i, int lvl) throws IOException {
	    File[] sourceDir = source.listFiles();
        if (i == sourceDir.length) {
		    return;
	    }
	    if (sourceDir[i].isFile()) {
		    FileInputStream inputStream = new FileInputStream(sourceDir[i]);
		    FileOutputStream outputStream = new FileOutputStream(new File(destination.getPath() + "\\" + sourceDir[i].getName()));
		    byte[] buffer = new byte[1024];
		    int length;
            while ((length = inputStream.read(buffer)) > 0) {
			    outputStream.write(buffer, 0, length);
		    }
	    	inputStream.close();
		    outputStream.close();
		}
		if (sourceDir[i].isDirectory()) {
			File newDir = new File(destination.getPath() + "\\" + sourceDir[i].getName());
			if (!newDir.exists()) {
				newDir.mkdir();
			}
			copy(sourceDir[i], newDir, 0, lvl+1);
		}
		copy(source,destination, i + 1, lvl);
	}
}
