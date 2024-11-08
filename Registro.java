package ExamenFinal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class Registro extends JFrame {
    private JComboBox<Integer> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;

    public Registro(boolean searchByCode) {
        setTitle("Registrar Nuevo Producto");
        setSize(450, 400);  // Tamaño ajustado de la ventana
        setLocationRelativeTo(null);

        // Panel principal con márgenes
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));  // Márgenes alrededor
        mainPanel.setLayout(new GridLayout(7, 2, 10, 10));  // Espaciado entre componentes

        mainPanel.add(new JLabel("Código:"));
        JTextField codeField = new JTextField();
        mainPanel.add(codeField);

        mainPanel.add(new JLabel("Nombre:"));
        JTextField nameField = new JTextField();
        mainPanel.add(nameField);

        mainPanel.add(new JLabel("Precio:"));
        JTextField priceField = new JTextField();
        mainPanel.add(priceField);

        mainPanel.add(new JLabel("Cantidad:"));
        JTextField quantityField = new JTextField();
        mainPanel.add(quantityField);

        // Selector de fecha JComboBox
        mainPanel.add(new JLabel("Fecha de Vencimiento:"));
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

        mainPanel.add(datePanel);

        JButton saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> saveProduct(codeField.getText(), nameField.getText(), priceField.getText(), quantityField.getText()));
        mainPanel.add(saveButton);

        add(mainPanel);  // Agregar el panel principal a la ventana
        setVisible(true);
    }

    private void saveProduct(String code, String name, String priceText, String quantityText) {
        double price;
        int quantity;

        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa valores válidos para precio y cantidad.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la fecha de los JComboBox
        int day = (int) dayComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex();
        int year = (int) yearComboBox.getSelectedItem();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());

        String query = "INSERT INTO producto (CodigoProducto, NombreProducto, PrecioUnitario, CantidadProducto, FechaVencimiento) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, code);
            statement.setString(2, name);
            statement.setDouble(3, price);
            statement.setInt(4, quantity);
            statement.setDate(5, sqlDate);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto registrado exitosamente.");
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar el producto: " + ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
