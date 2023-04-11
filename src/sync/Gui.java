package sync;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Gui {
	private boolean isActive = true;
	private JFrame frmSynchronize;

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
		frmSynchronize.setBounds(100, 100, 377, 203);
		frmSynchronize.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSynchronize.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Source folder");
		lblNewLabel.setBounds(10, 11, 84, 19);
		frmSynchronize.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Destination folder");
		lblNewLabel_1.setBounds(10, 58, 141, 14);
		frmSynchronize.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setBounds(146, 94, 141, 14);
		frmSynchronize.getContentPane().add(lblNewLabel_3);
		
		JButton btnNewButton = new JButton("Synchronize");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isActive) {
					Sync.main(null);
					lblNewLabel_3.setText("Synchronized");
				}
				else {
					lblNewLabel_3.setText("Not Synchronized");
				}

			}
		});
		btnNewButton.setBounds(10, 132, 160, 23);
		frmSynchronize.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Stop");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sync.stopSync();
				lblNewLabel_3.setText("Not Synchronized");
			}
		});
		btnNewButton_1.setBounds(193, 132, 160, 23);
		frmSynchronize.getContentPane().add(btnNewButton_1);
		
		JLabel lblNewLabel_2 = new JLabel("State :");
		lblNewLabel_2.setBounds(106, 94, 75, 14);
		frmSynchronize.getContentPane().add(lblNewLabel_2);
		

	}
}
