package ExamenFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public Login() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con margen
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));  // Márgenes de 15 px alrededor
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));  // Espacio entre filas y columnas

        mainPanel.add(new JLabel("Usuario:"));
        userField = new JTextField();
        mainPanel.add(userField);

        mainPanel.add(new JLabel("Contraseña:"));
        passField = new JPasswordField();
        mainPanel.add(passField);

        loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(this);

        mainPanel.add(new JLabel()); // Espacio vacío
        mainPanel.add(loginButton);

        add(mainPanel);  // Agrega el panel principal a la ventana
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingresa ambos campos.");
                return;
            }

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return authenticate(username, password);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(null, "Login exitoso");
                            dispose();
                            new CRUDProductos();
                        } else {
                            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al autenticar el usuario");
                    }
                }
            };
            worker.execute();
        }
    }

    private boolean authenticate(String username, String password) {
        String query = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos");
        }
        return false;
    }

    public static void main(String[] args) {
        new Login();
    }
}
