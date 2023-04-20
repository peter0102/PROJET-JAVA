package sync;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
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
	private JTextField serverTextField;
	private JTextField clientPortTextField;
	private JTextField clientIPTextField;

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
		
		JPanel mainPanel = new JPanel();
		cardPanel.add(mainPanel, "Main");
		
		JPanel clientPanel = new JPanel();
		cardPanel.add(clientPanel,"Client");
		
		JPanel serverPanel = new JPanel();
		cardPanel.add(serverPanel,"Server");
		serverPanel.setLayout(null);
		
		JLabel serverLabel = new JLabel("Please choose a port :");
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
		
		JButton serverBackButton = new JButton("Back");
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

		JLabel clientIPLabel = new JLabel("IP Adress : ");
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
					int fileSize = dataInputStream.readInt();
					if (fileSize > 0) {
						byte[] fileNameBytes = new byte[fileSize];
						dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
						String fileName = new String(fileNameBytes);
						FileOutputStream fileOutputStream = new FileOutputStream(fileName);
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = dataInputStream.read(buffer)) > 0) {
							fileOutputStream.write(buffer, 0, bytesRead);
						}
						fileOutputStream.close();
						System.out.println("File " + fileName + " received from client.");
					}
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
					String sourceFileName = sourceDir.getName();
					byte[] sourceFileNameBytes = sourceFileName.getBytes();
					byte[] sourceFileLengthBytes = new byte[(int)sourceDir.length()];
					fileInputStream.read(sourceFileLengthBytes);
					dataOutputStream.writeInt(sourceFileNameBytes.length);
					dataOutputStream.write(sourceFileNameBytes);
					dataOutputStream.writeInt(sourceFileLengthBytes.length);
					dataOutputStream.write(sourceFileLengthBytes);
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
		
		JButton clientBackButton = new JButton("Back");
		clientBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myLayout.show(cardPanel,"Main");
			}
		});
		clientBackButton.setBounds(10, 10, 85, 21);
		clientPanel.add(clientBackButton);
		
	}

	
	public static Gui createAndShowGui() {
		Gui window = new Gui();
		window.frmeSynchronize.setVisible(true);
		return window;
	}
}
