package tema8Ejercicio2;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.sql.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Tema8Ejercicio2 {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tema8Ejercicio2 window = new Tema8Ejercicio2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	public Tema8Ejercicio2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	class ConnectionSingleton {
		private static Connection con;
		
		public static Connection getConnection() throws SQLException {
			//Estas tres lineas sirven para que el programa Java reconozca la base de datos
			String url="jdbc:mysql://127.0.0.1:3307/comunidades";
			String user="alumno";
			String password="Alumno123";
			
			if (con==null ||  con.isClosed()) {
				con=DriverManager.getConnection(url, user, password);
			}
			return con;
		}
	}
	
	private void initialize() {
		try {
			Connection con = ConnectionSingleton.getConnection();
			
			frame = new JFrame();
			frame.setBounds(100, 100, 650, 300);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			
			JLabel lblComunidadAutonoma = new JLabel("Comunidad Autónoma");
			lblComunidadAutonoma.setBounds(12, 12, 168, 15);
			frame.getContentPane().add(lblComunidadAutonoma);
			
			JLabel lblProvincias = new JLabel("Provincias");
			lblProvincias.setBounds(205, 12, 168, 15);
			frame.getContentPane().add(lblProvincias);
			
			JComboBox comboBox_ComunidadesAutonomas = new JComboBox();
			JComboBox comboBoxProvincias = new JComboBox();
			
			comboBox_ComunidadesAutonomas.setBounds(12, 39, 168, 24);
			try { //Este try sirve para que, de la tabla comunidad, ponga todos los nombres en el primer comboBox
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM comunidad");
				while (rs.next()) {
					int idComunidad=rs.getInt("id");
					String nombre=rs.getString("nombre");
					comboBox_ComunidadesAutonomas.addItem(nombre);
				}
				rs.close();
				stmt.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			
			comboBox_ComunidadesAutonomas.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) { //Esta propiedad es para cuando eliges otra comunidad del primer comboBox
					
					//Este primer try sirve para que, si elijes la comunidad de Valencia, te ponga en el segundo comboBox todas sus provincias
					try {
						PreparedStatement sel_pstmt = con.prepareStatement("SELECT * FROM provincia WHERE provincia.id_comunidad=?");
						sel_pstmt.setInt(1, comboBox_ComunidadesAutonomas.getSelectedIndex()+1);
						ResultSet rs_sel = sel_pstmt.executeQuery();
						comboBoxProvincias.removeAllItems();
						while (rs_sel.next()) {
							String nombre=rs_sel.getString("nombre");
							comboBoxProvincias.addItem(nombre);
						}
						rs_sel.close();
						sel_pstmt.close();
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
				}
			});
			frame.getContentPane().add(comboBox_ComunidadesAutonomas);
			
			comboBoxProvincias.setBounds(205, 39, 168, 24);
			try { //Este try sirve para que, de la tabla provincia, ponga todos los nombres en el segundo comboBox
				PreparedStatement sel_pstmt = con.prepareStatement("SELECT * FROM provincia WHERE provincia.id_comunidad=?");
				sel_pstmt.setInt(1, comboBox_ComunidadesAutonomas.getSelectedIndex()+1);
				ResultSet rs_sel = sel_pstmt.executeQuery();
				while (rs_sel.next()) {
					String nombre=rs_sel.getString("nombre");
					comboBoxProvincias.addItem(nombre);
				}
				rs_sel.close();
				sel_pstmt.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			frame.getContentPane().add(comboBoxProvincias);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}