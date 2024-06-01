import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import java.awt.Color;

public class Dashboard {

//	Declaration Part
	private JFrame frame;
	private JTextField idTxt;
	private JTextField titletxt;
	private JTextField authorTxt;
	private JTextField yearTxt;
	private JTable dataTable;
	
//	This is the file path to our access database
	private static final String DATABASE_PATH = "C://Users/ssebb/eclipse-workspace/LMS/src/LibraryDB.accdb";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dashboard window = new Dashboard();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	
	/**
     * Method to Populate the table with data from the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void populateTable() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Connect to the database
            String url = "jdbc:ucanaccess://" + DATABASE_PATH;
            connection = DriverManager.getConnection(url);

            // SQL query
            String query = "SELECT ID, TITLE, AUTHOR, YEAR FROM books";

            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Clear existing table data
            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            model.setRowCount(0);

            // Populate the table
            while (resultSet.next()) {
                Object[] rowData = {
                    resultSet.getInt("ID"),
                    resultSet.getString("TITLE"),
                    resultSet.getString("AUTHOR"),
                    resultSet.getString("YEAR")
                };
                model.addRow(rowData);
            }
        } finally {
            // Clean up our connection to the database
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
//            we close the connection after using the database to save resources
            if (connection != null) {
                connection.close();
            }
        }
    }
    

    /**
     * Function/method that Inserts data into the database table.
     *
     * @param databasePath the path to the MS Access database file
     * @param id           the ID to insert
     * @param title        the title to insert
     * @param author       the author to insert
     * @param year         the year to insert
     * @throws SQLException if a database access error occurs
     */
    public void insertData(String databasePath, int id, String title, String author, String year) throws SQLException {
        // Initialize the connection and prepared statement to null
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // Construct the database URL using the provided path
            String url = "jdbc:ucanaccess://" + databasePath;
            // Establish the connection to the database
            connection = DriverManager.getConnection(url);

            // SQL statement for inserting a new record into the books table
            String insertSQL = "INSERT INTO books (ID, TITLE, AUTHOR, YEAR) VALUES (?, ?, ?, ?)";

            // Prepare the SQL statement with the provided data
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, id);         // Set the ID parameter
            preparedStatement.setString(2, title);   // Set the title parameter
            preparedStatement.setString(3, author);  // Set the author parameter
            preparedStatement.setString(4, year);    // Set the year parameter

            // Execute the insert operation and get the number of rows affected
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                // Print a success message if the insert operation was successful
                System.out.println("Add new book succeeded");
            }
        } finally {
            // Ensure the prepared statement is closed to free resources
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Ensure the database connection is closed to free resources
            if (connection != null) {
                connection.close();
            }
        }
    }

    
    /**
     * Delete the selected row from the table and the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void deleteSelectedRow() throws SQLException {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            // No row is selected, do nothing
            return;
        }
        
        // Confirm the deletion
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete book with ID: " + dataTable.getValueAt(selectedRow, 0) + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Get the ID of the selected row
        int id = (int) dataTable.getValueAt(selectedRow, 0);

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Connect to the database
            String url = "jdbc:ucanaccess://" + DATABASE_PATH;
            connection = DriverManager.getConnection(url);

            // SQL query to delete the record
            String query = "DELETE FROM books WHERE ID = ?";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            // Execute the delete
            preparedStatement.executeUpdate();

            // Remove the row from the table model
            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            model.removeRow(selectedRow);
        } finally {
            // Cleaning up
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

	/**
	 * Create the application.
	 * @throws SQLException 
	 */
	public Dashboard() throws SQLException {
		initialize();
		populateTable();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.activeCaption);
		frame.setResizable(false);
		frame.setBounds(100, 100, 1026, 581);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(frame);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 77, 234, 456);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Book ID");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1.setForeground(new Color(0, 0, 255));
		panel.add(lblNewLabel_1);
		
		idTxt = new JTextField();
		idTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					titletxt.requestFocus();
				}
			}
		});
		panel.add(idTxt);
		idTxt.setColumns(10);
		
		JLabel lblNewLabel_1_1 = new JLabel("Title");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_1.setForeground(new Color(0, 0, 255));
		panel.add(lblNewLabel_1_1);
		
		titletxt = new JTextField();
		titletxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					authorTxt.requestFocus();
				}
			}
		});
		titletxt.setColumns(10);
		panel.add(titletxt);
		
		JLabel lblNewLabel_1_2 = new JLabel("Author");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_2.setForeground(new Color(0, 0, 255));
		panel.add(lblNewLabel_1_2);
		
		authorTxt = new JTextField();
		authorTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					yearTxt.requestFocus();
				}
			}
		});
		authorTxt.setColumns(10);
		panel.add(authorTxt);
		
		JLabel lblNewLabel_1_3 = new JLabel("Year");
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_3.setForeground(new Color(0, 0, 255));
		panel.add(lblNewLabel_1_3);
		
		yearTxt = new JTextField();
		yearTxt.setColumns(10);
		panel.add(yearTxt);
		
		JButton addBtn = new JButton("Add Book");
		addBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
		addBtn.setBackground(new Color(0, 204, 102));
		addBtn.setForeground(new Color(255, 255, 255));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
		            String id = idTxt.getText();
		            int book_id = Integer.parseInt(id); 
		            String title = titletxt.getText();
		            String author = authorTxt.getText();
		            String year = yearTxt.getText();		            

		            insertData(DATABASE_PATH, book_id, title, author, year);
		            
		            idTxt.setText("");
		            titletxt.setText("");
		            authorTxt.setText("");
		            yearTxt.setText("");
		            
//		            populateTable();
		            JOptionPane.showMessageDialog(frame, "New Book " + title  + " has been added successfully!");
		        } catch (SQLException e1) {
		            e1.printStackTrace();
		        }
			}
			
		});
		panel.add(addBtn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(254, 77, 748, 456);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 11, 728, 44);
		panel_1.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton refreshBtn = new JButton("Refresh Table");
		refreshBtn.setBackground(new Color(0, 153, 255));
		refreshBtn.setForeground(new Color(255, 255, 255));
		refreshBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
		refreshBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
                    populateTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
			}
		});
		panel_3.add(refreshBtn);
		
		JButton deleteBtn = new JButton("Delete Selected Book");
		deleteBtn.setForeground(new Color(255, 255, 255));
		deleteBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
		deleteBtn.setBackground(new Color(255, 51, 51));
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
                    deleteSelectedRow();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
			}
		});
		panel_3.add(deleteBtn);
		
		JPanel panel_3_1 = new JPanel();
		panel_3_1.setBounds(10, 66, 728, 379);
		panel_1.add(panel_3_1);
		panel_3_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		dataTable = new JTable();
		dataTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"BooK ID", "Title", "Author", "Year"
			}
		));
		JScrollPane scrollPane = new JScrollPane(dataTable);
		panel_3_1.add(scrollPane);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 11, 992, 61);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("VU LIBRARY MANAGEMENT SYSTEM");
		lblNewLabel.setForeground(new Color(0, 0, 255));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel);
	}
}
