package sync;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Font;
/**
 * This class is the GUI for the program, it is used to select the folders to be synchronized
 */
public class Gui extends JFrame {
	private boolean isActive = true;
	private JFrame frmeSynchronize;
	private JTextField sourceTextField;
	private JTextField destinationTextField;
	private JTextField serverPortTextField;
	private JTextField serverDestinationTextField;
	private JTextField clientSourceTextField;
	private JTextField clientPortTextField;
	private Server server;
	private Client client;

	/**
	 * Launch the application.
	 * This is where the main is, it creates a new Gui object and makes it visible
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmeSynchronize.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	void initialize() {
		frmeSynchronize = new JFrame();
		frmeSynchronize.setResizable(false);
		frmeSynchronize.setTitle("Synchronize");
		frmeSynchronize.setBounds(100, 100, 489, 257);
		frmeSynchronize.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmeSynchronize.getContentPane().setLayout(new CardLayout(0, 0));

		CardLayout myLayout = new CardLayout();

		JPanel cardPanel = new JPanel(myLayout);
		frmeSynchronize.getContentPane().add(cardPanel, "Base");

		JPanel firstPanel = new JPanel();
		cardPanel.add(firstPanel, "First");
		firstPanel.setLayout(null);

		JButton firstPanelSync = new JButton("Sync on one computer");
		firstPanelSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Single Computer");
				myLayout.show(cardPanel, "Basic");
			}
		});
		firstPanelSync.setFont(new Font("Tahoma", Font.PLAIN, 14));
		firstPanelSync.setBounds(40, 73, 194, 65);
		firstPanel.add(firstPanelSync);

		JButton firstPanelSyncNetwork = new JButton("Sync on network");
		firstPanelSyncNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Network");
				myLayout.show(cardPanel, "Main");
			}
		});
		firstPanelSyncNetwork.setFont(new Font("Tahoma", Font.PLAIN, 14));
		firstPanelSyncNetwork.setBounds(258, 73, 194, 65);
		firstPanel.add(firstPanelSyncNetwork);

		JPanel mainPanel = new JPanel();
		cardPanel.add(mainPanel, "Main");

		JPanel basicSyncPanel = new JPanel();
		cardPanel.add(basicSyncPanel, "Basic");

		JPanel serverPanel = new JPanel();
		cardPanel.add(serverPanel, "Server");

		JPanel clientPanel = new JPanel();
		cardPanel.add(clientPanel, "Client");
		clientPanel.setLayout(null);

		JButton ClientBackButton = new JButton("Back");
		ClientBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Network");
				myLayout.show(cardPanel, "Main");
			}
		});
		ClientBackButton.setBounds(10, 10, 85, 21);
		clientPanel.add(ClientBackButton);

		JButton clientSourceFolder = new JButton("Select source folder :");
		clientSourceFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Source Folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(frmeSynchronize);
				if (result == JFileChooser.APPROVE_OPTION) {
					clientSourceTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		clientSourceFolder.setBounds(10, 41, 196, 32);
		clientPanel.add(clientSourceFolder);

		clientSourceTextField = new JTextField();
		clientSourceTextField.setColumns(10);
		clientSourceTextField.setBounds(216, 41, 242, 32);
		clientPanel.add(clientSourceTextField);

		JLabel serverLabel_1 = new JLabel("Please enter a port :");
		serverLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		serverLabel_1.setBounds(10, 83, 215, 32);
		clientPanel.add(serverLabel_1);

		clientPortTextField = new JTextField();
		clientPortTextField.setColumns(10);
		clientPortTextField.setBounds(216, 83, 169, 33);
		clientPanel.add(clientPortTextField);

		JLabel clientStateLabel = new JLabel("State : ");
		clientStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		clientStateLabel.setBounds(35, 128, 98, 32);
		clientPanel.add(clientStateLabel);

		JLabel clientActualStateLabel = new JLabel("");
		clientActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		clientActualStateLabel.setBounds(97, 125, 361, 35);
		clientPanel.add(clientActualStateLabel);

		JLabel serverActualStateLabel = new JLabel("");
		serverActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverActualStateLabel.setBounds(81, 128, 377, 35);
		serverPanel.add(serverActualStateLabel);

		JButton serverLaunchButton = new JButton("Launch");
		serverLaunchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (serverDestinationTextField.getText().isEmpty()) {
					serverActualStateLabel.setText("Please enter destination folder");
					return;
				}
				File destinationDirectory = new File(serverDestinationTextField.getText());
				if (!destinationDirectory.exists()) {
					serverActualStateLabel.setText("Destination folder does not exist");
					return;
				}
				if (serverPortTextField.getText().isEmpty()) {
					serverActualStateLabel.setText("Please enter a port");
					return;
				}
				boolean isNumeric = serverPortTextField.getText().chars().allMatch(Character::isDigit);
				if (!isNumeric) {
					serverActualStateLabel.setText("Please enter a valid port");
					return;
				}
				int port = Integer.parseInt(serverPortTextField.getText());
				if (port < 0 || port > 65535) {
					serverActualStateLabel.setText("Port must be between 0 and 65535");
					return;
				}
				server = new Server();
				server.destinationFolder = serverDestinationTextField.getText();
				Thread serverThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							server.startServer(port);
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
							clientActualStateLabel.setText("Server could not start");

						}
					}
				});
				serverThread.start();
				serverActualStateLabel.setText("Server launched");
				serverLaunchButton.setEnabled(false);
			}
		});
		serverLaunchButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		serverLaunchButton.setBounds(81, 161, 150, 49);
		serverPanel.add(serverLaunchButton);

		JButton clientConnectButton = new JButton("Connect");
		clientConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (clientSourceTextField.getText().isEmpty()) {
					clientActualStateLabel.setText("Please enter source folder");
					return;
				}
				File sourceDirectory = new File(clientSourceTextField.getText());
				if (!sourceDirectory.exists()) {
					clientActualStateLabel.setText("Source folder does not exist");
					return;
				}
				if (clientPortTextField.getText().isEmpty()) {
					clientActualStateLabel.setText("Please enter a port");
					return;
				}
				boolean isNumeric = clientPortTextField.getText().chars().allMatch(Character::isDigit);
				if (!isNumeric) {
					clientActualStateLabel.setText("Please enter a valid port");
					return;
				}
				int port = Integer.parseInt(clientPortTextField.getText());
				if (port < 0 || port > 65535) {
					clientActualStateLabel.setText("Port must be between 0 and 65535");
					return;
				}
				client = new Client();
				client.sourceFolder = clientSourceTextField.getText();
				Thread connectThread = new Thread(new Runnable() { // on crée un thread qui gère la connection pour ne
																	// pas bloquer l'interface
					@Override
					public void run() {
						try {
							client.startConnection("localhost", port);
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
							clientActualStateLabel.setText("Connection failed");

						}
					}
				});
				connectThread.start();
				clientActualStateLabel.setText("Connection established");
				if (clientActualStateLabel.getText() == "Connection failed") {
					clientConnectButton.setEnabled(false);
				}
			}
		});
		clientConnectButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		clientConnectButton.setBounds(79, 161, 150, 49);
		clientPanel.add(clientConnectButton);

		JButton serverStopButton = new JButton("Stop");
		serverStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (server != null) {
						server.stopServer();
						serverLaunchButton.setEnabled(true);
						if (!serverActualStateLabel.getText().isEmpty()) {
							serverActualStateLabel.setText("Server stopped");
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		serverStopButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		serverStopButton.setBounds(268, 161, 150, 49);
		serverPanel.add(serverStopButton);

		JButton clientStopButton = new JButton("Stop");
		clientStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (client != null) { // évite les erreurs quand on clique sur stop sans être connecté
						client.stopConnection();
						clientConnectButton.setEnabled(true);
						if (!clientActualStateLabel.getText().isEmpty()) {
							clientActualStateLabel.setText("Connection stopped");
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		clientStopButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		clientStopButton.setBounds(261, 161, 150, 49);
		clientPanel.add(clientStopButton);

		serverPanel.setLayout(null);

		JLabel serverLabel = new JLabel("Please enter a port :");
		serverLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		serverLabel.setBounds(10, 85, 215, 32);
		serverPanel.add(serverLabel);

		serverPortTextField = new JTextField();
		serverPortTextField.setBounds(216, 89, 169, 33);
		serverPanel.add(serverPortTextField);
		serverPortTextField.setColumns(10);

		JLabel serverStateLabel = new JLabel("State : ");
		serverStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverStateLabel.setBounds(20, 131, 98, 32);
		serverPanel.add(serverStateLabel);

		JButton serverBackButton = new JButton("Back");
		serverBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Network");
				myLayout.show(cardPanel, "Main");
			}
		});
		serverBackButton.setBounds(10, 13, 85, 21);
		serverPanel.add(serverBackButton);

		JButton serverDestinationFolder = new JButton("Select destination folder :");
		serverDestinationFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Destination Folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(frmeSynchronize);
				if (result == JFileChooser.APPROVE_OPTION) {
					serverDestinationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		serverDestinationFolder.setBounds(10, 43, 196, 32);
		serverPanel.add(serverDestinationFolder);

		serverDestinationTextField = new JTextField();
		serverDestinationTextField.setColumns(10);
		serverDestinationTextField.setBounds(216, 44, 242, 32);
		serverPanel.add(serverDestinationTextField);

		myLayout.show(cardPanel, "First");

		JButton clientButton = new JButton("Client");
		clientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Network Client");
				myLayout.show(cardPanel, "Client");
			}
		});
		mainPanel.setLayout(null);
		clientButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		clientButton.setBounds(65, 110, 130, 56);
		mainPanel.add(clientButton);

		JButton serverButton = new JButton("Server");
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize - Network Server");
				myLayout.show(cardPanel, "Server");
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

		JButton mainBackButton = new JButton("Back");
		mainBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize");
				myLayout.show(cardPanel, "First");
			}
		});
		mainBackButton.setBounds(10, 10, 85, 21);
		mainPanel.add(mainBackButton);
		basicSyncPanel.setLayout(null);

		JLabel stateLabel = new JLabel("");
		stateLabel.setBounds(217, 138, 141, 14);
		basicSyncPanel.add(stateLabel);

		JLabel errorLabel = new JLabel("");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setBounds(47, 162, 415, 14);
		basicSyncPanel.add(errorLabel);

		JButton startButton = new JButton("Synchronize");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sourceFolderPath = sourceTextField.getText();
				String destinationFolderPath = destinationTextField.getText();
				File sourceDir = new File(sourceFolderPath);
				File destDir = new File(destinationFolderPath);
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
				}
				if (sourceFolderPath.equals(destinationFolderPath)) {
					errorLabel.setText("");
					errorLabel.setText("Error: Source and destination folders are the same");
					return;
				} else {
					if (isActive) {
						Sync sync = new Sync(sourceFolderPath, destinationFolderPath);
						Thread thread = new Thread(sync);
						thread.start();
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
		basicSyncPanel.add(startButton);

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sync.stopSync();
				stateLabel.setText("Not Synchronized");
				startButton.setEnabled(true);
			}
		});
		stopButton.setBounds(289, 186, 160, 23);
		basicSyncPanel.add(stopButton);

		JLabel stateLabelInitial = new JLabel("State :");
		stateLabelInitial.setBounds(156, 138, 75, 14);
		basicSyncPanel.add(stateLabelInitial);

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
		basicSyncPanel.add(sourceButton);

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
		basicSyncPanel.add(destinationButton);

		sourceTextField = new JTextField();
		sourceTextField.setBounds(207, 43, 242, 20);
		basicSyncPanel.add(sourceTextField);
		sourceTextField.setColumns(10);

		destinationTextField = new JTextField();
		destinationTextField.setBounds(208, 76, 241, 20);
		basicSyncPanel.add(destinationTextField);
		destinationTextField.setColumns(10);

		JButton clientBackButton = new JButton("Back");
		clientBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmeSynchronize.setTitle("Synchronize");
				myLayout.show(cardPanel, "First");
			}
		});
		clientBackButton.setBounds(10, 10, 85, 21);
		basicSyncPanel.add(clientBackButton);

	}
}
