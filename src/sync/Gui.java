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

public class Gui extends JFrame {
	private boolean isActive = true;
	private JFrame frmeSynchronize;
	private JTextField sourceTextField;
	private JTextField destinationTextField;
	private JTextField serverPortTextField;
	private JTextField serverDestinationTextField;
	private JTextField clientSourceTextField;
	private JTextField clientPortTextField;

	/**
	 * Launch the application.
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
		cardPanel.add(firstPanel,"First");
		firstPanel.setLayout(null);
		
		JButton firstPanelSync = new JButton("Sync on one computer");
		firstPanelSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Basic");
			}
		});
		firstPanelSync.setFont(new Font("Tahoma", Font.PLAIN, 14));
		firstPanelSync.setBounds(40, 73, 194, 65);
		firstPanel.add(firstPanelSync);
		
		JButton firstPanelSyncNetwork = new JButton("Sync on network");
		firstPanelSyncNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
			}
		});
		firstPanelSyncNetwork.setFont(new Font("Tahoma", Font.PLAIN, 14));
		firstPanelSyncNetwork.setBounds(258, 73, 194, 65);
		firstPanel.add(firstPanelSyncNetwork);
		
		JPanel mainPanel = new JPanel();
		cardPanel.add(mainPanel, "Main");
		
		JPanel basicSyncPanel = new JPanel();
		cardPanel.add(basicSyncPanel,"Basic");
		
		JPanel serverPanel = new JPanel();
		cardPanel.add(serverPanel,"Server");
		
		JPanel clientPanel = new JPanel();
		cardPanel.add(clientPanel,"Client");
		clientPanel.setLayout(null);
		
		JButton serverBackButton_1 = new JButton("Back");
		serverBackButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
			}
		});
		serverBackButton_1.setBounds(10, 10, 85, 21);
		clientPanel.add(serverBackButton_1);
		
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
		clientStateLabel.setBounds(108, 128, 98, 32);
		clientPanel.add(clientStateLabel);
		
		JLabel clientActualStateLabel = new JLabel("");
		clientActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		clientActualStateLabel.setBounds(171, 125, 276, 35);
		clientPanel.add(clientActualStateLabel);
		
		JButton clientConnectButton = new JButton("Connect");
		clientConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (clientPortTextField.getText().equals("")) {
					clientActualStateLabel.setText("Please enter a port");
				}
				boolean isNumeric = clientPortTextField.getText().chars().allMatch( Character::isDigit );
				if (!isNumeric) {
					clientActualStateLabel.setText("Please enter a valid port");
				}
	        	int port = Integer.parseInt(clientPortTextField.getText());
	        	Client client = new Client();
	        	client.sourceFolder = clientSourceTextField.getText();
	        	try {
					client.startConnection("localhost",port);
					clientActualStateLabel.setText("Connection established!");
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		clientConnectButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		clientConnectButton.setBounds(170, 161, 150, 49);
		clientPanel.add(clientConnectButton);
		
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
		serverStateLabel.setBounds(112, 118, 98, 32);
		serverPanel.add(serverStateLabel);
		
		JLabel serverActualStateLabel = new JLabel("");
		serverActualStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverActualStateLabel.setBounds(177, 115, 276, 35);
		serverPanel.add(serverActualStateLabel);
		
		JButton serverLaunchButton = new JButton("Launch");
		serverLaunchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					if (serverPortTextField.getText().equals("")) {
						serverActualStateLabel.setText("Please enter a port");
					}
					boolean isNumeric = serverPortTextField.getText().chars().allMatch( Character::isDigit );
					if (!isNumeric) {
						clientActualStateLabel.setText("Please enter a valid port");
					}
		        	int port = Integer.parseInt(serverPortTextField.getText());
		        	Server server = new Server();
		        	server.destinationFolder = serverDestinationTextField.getText();
		        	try {
						server.startServer(port);
						serverActualStateLabel.setText("Connection established!");
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
			}
		});
		serverLaunchButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		serverLaunchButton.setBounds(166, 161, 150, 49);
		serverPanel.add(serverLaunchButton);
		
		JButton serverBackButton = new JButton("Back");
		serverBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
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
		
		JButton clientBackButton_1 = new JButton("Back");
		clientBackButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"First");
			}
		});
		clientBackButton_1.setBounds(10, 10, 85, 21);
		mainPanel.add(clientBackButton_1);
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
				myLayout.show(cardPanel,"First");
			}
		});
		clientBackButton.setBounds(10, 10, 85, 21);
		basicSyncPanel.add(clientBackButton);
		
	}

	
	public static Gui createAndShowGui() {
		Gui window = new Gui();
		window.frmeSynchronize.setVisible(true);
		return window;
	}
}
