package sync;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class Gui {
	private boolean isActive = true;
	private JFrame frmSynchronize;
	private JTextField sourceTextField;
	private JTextField destinationTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmSynchronize.setVisible(true);
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
	private void initialize() {
		frmSynchronize = new JFrame();
		frmSynchronize.setResizable(false);
		frmSynchronize.setTitle("Synchronize");
		frmSynchronize.setBounds(100, 100, 489, 257);
		frmSynchronize.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSynchronize.getContentPane().setLayout(null);
		
		JLabel stateLabel = new JLabel("");
		stateLabel.setBounds(207, 132, 141, 14);
		frmSynchronize.getContentPane().add(stateLabel);

        JLabel errorLabel = new JLabel("");
		errorLabel.setForeground(new Color(255, 0, 0));
        errorLabel.setBounds(47, 157, 415, 14);
		frmSynchronize.getContentPane().add(errorLabel);

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
                    else {
                    if (isActive) {
                        Sync.main(new String[] {sourceFolderPath, destinationFolderPath});
                        stateLabel.setText("Synchronized");
                        errorLabel.setText("");
                        startButton.setEnabled(false);
                    }
                    else {
                        stateLabel.setText("Not Synchronized");
                    }

                }
            }
		});
		startButton.setBounds(47, 186, 160, 23);
		frmSynchronize.getContentPane().add(startButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sync.stopSync();
				stateLabel.setText("Not Synchronized");
                startButton.setEnabled(true);
			}
		});
		stopButton.setBounds(289, 186, 160, 23);
		frmSynchronize.getContentPane().add(stopButton);
		
		JLabel stateLabelIniatial = new JLabel("State :");
		stateLabelIniatial.setBounds(159, 132, 75, 14);
		frmSynchronize.getContentPane().add(stateLabelIniatial);
		
		JButton sourceButton = new JButton("Select source folder :");
        sourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Source Folder");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frmSynchronize);
                if (result == JFileChooser.APPROVE_OPTION) {
                    sourceTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

		sourceButton.setBounds(5, 23, 192, 23);
		frmSynchronize.getContentPane().add(sourceButton);
		
		JButton destinationButton = new JButton("Select destination folder :");
        destinationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Destination Folder");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frmSynchronize);
                if (result == JFileChooser.APPROVE_OPTION) {
                    destinationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

		destinationButton.setBounds(5, 73, 192, 23);
		frmSynchronize.getContentPane().add(destinationButton);
		
		sourceTextField = new JTextField();
		sourceTextField.setBounds(207, 24, 242, 20);
		frmSynchronize.getContentPane().add(sourceTextField);
		sourceTextField.setColumns(10);
		
		destinationTextField = new JTextField();
		destinationTextField.setBounds(207, 74, 241, 20);
		frmSynchronize.getContentPane().add(destinationTextField);
		destinationTextField.setColumns(10);
		

	}
    
}
