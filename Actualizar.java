package ExamenFinal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class Actualizar extends JFrame {
    private JTextField codeField, nameField, priceField, quantityField;
    private JComboBox<Integer> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private boolean searchByCode;

    public Actualizar(boolean searchByCode) {
        this.searchByCode = searchByCode;
        setTitle("Actualizar Producto");
        setSize(450, 400);  // Aumenta el tamaño de la ventana para dar más espacio
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(new JLabel("Código:"));
        codeField = new JTextField();
        panel.add(codeField);

        panel.add(new JLabel("Nombre:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Precio:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Cantidad:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        // Selector de fecha con JComboBox
        panel.add(new JLabel("Fecha de Vencimiento:"));
        JPanel datePanel = new JPanel(new GridLayout(1, 3, 5, 5));

        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayComboBox.addItem(i);
        }
        datePanel.add(dayComboBox);

        monthComboBox = new JComboBox<>(new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                                                     "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
        datePanel.add(monthComboBox);

        yearComboBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i <= currentYear + 10; i++) {
            yearComboBox.addItem(i);
        }
        datePanel.add(yearComboBox);

        panel.add(datePanel);

        JButton updateButton = new JButton("Actualizar");
        updateButton.addActionListener(e -> updateProduct());
        panel.add(updateButton);

        add(panel);
        setVisible(true);
    }

    private void updateProduct() {
        String code = codeField.getText();
        String name = nameField.getText();
        double price;
        int quantity;

        // Validar campos de entrada
        try {
            price = Double.parseDouble(priceField.getText());
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa valores válidos para precio y cantidad.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la fecha de los JComboBox
        int day = (int) dayComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex();  // Los meses van de 0 a 11
        int year = (int) yearComboBox.getSelectedItem();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());

        // Consulta de actualización con fecha de vencimiento
        String query = "UPDATE producto SET NombreProducto = ?, PrecioUnitario = ?, CantidadProducto = ?, FechaVencimiento = ? WHERE CodigoProducto = ?";

        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setInt(3, quantity);
            statement.setDate(4, sqlDate);  // Fecha de vencimiento seleccionada
            statement.setString(5, code);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún producto con el código proporcionado.", "Error", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar el producto en la base de datos: " + ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
