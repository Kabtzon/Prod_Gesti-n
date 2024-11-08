package ExamenFinal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Eliminar extends JFrame {
    private JTextField searchField;
    private boolean searchByCode;

    public Eliminar(boolean searchByCode) {
        this.searchByCode = searchByCode;
        setTitle("Eliminar Producto");
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Panel principal con margen y espaciado
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setLayout(new BorderLayout(10, 10));  // Espacio entre componentes

        JLabel searchLabel = new JLabel(searchByCode ? "Eliminar por Código:" : "Eliminar por Nombre:");
        mainPanel.add(searchLabel, BorderLayout.NORTH);

        // Campo de texto de búsqueda con altura adecuada
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));  // Tamaño ajustado del campo de entrada
        mainPanel.add(searchField, BorderLayout.CENTER);

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.addActionListener(e -> deleteProduct());
        mainPanel.add(deleteButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void deleteProduct() {
        String query = searchByCode ?
                "DELETE FROM producto WHERE CodigoProducto = ?" :
                "DELETE FROM producto WHERE NombreProducto = ?";

        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, searchField.getText());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún producto con el criterio proporcionado.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
