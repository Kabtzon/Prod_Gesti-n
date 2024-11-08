package ExamenFinal;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Consulta extends JFrame {
    private JTextField searchField;
    private boolean searchByCode;

    public Consulta(boolean searchByCode) {
        this.searchByCode = searchByCode;
        setTitle("Consultar Producto");
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Panel principal con margen y espaciado
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setLayout(new BorderLayout(10, 10));  // Espacio entre componentes

        JLabel searchLabel = new JLabel(searchByCode ? "Buscar por Código:" : "Buscar por Nombre:");
        mainPanel.add(searchLabel, BorderLayout.NORTH);

        // Campo de texto de búsqueda con altura adecuada
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));  // Tamaño ajustado del campo de entrada
        mainPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> searchProduct());
        mainPanel.add(searchButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void searchProduct() {
        String query = searchByCode ?
                "SELECT * FROM producto WHERE CodigoProducto = ?" :
                "SELECT * FROM producto WHERE NombreProducto = ?";

        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, searchField.getText());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String codigo = resultSet.getString("CodigoProducto");
                String nombre = resultSet.getString("NombreProducto");
                double precio = resultSet.getDouble("PrecioUnitario");
                int cantidad = resultSet.getInt("CantidadProducto");
                String fechaVencimiento = resultSet.getDate("FechaVencimiento").toString();

                String result = "Código: " + codigo +
                        "\nNombre: " + nombre +
                        "\nPrecio: " + precio +
                        "\nCantidad: " + cantidad +
                        "\nFecha de Vencimiento: " + fechaVencimiento;

                // Crear opciones del cuadro de diálogo
                Object[] options = {"Guardar en PDF", "Aceptar"};
                int option = JOptionPane.showOptionDialog(this, result, "Resultado de la Consulta",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

                // Verificar si el usuario seleccionó "Guardar en PDF"
                if (option == JOptionPane.YES_OPTION) {
                    saveToPdf(codigo, nombre, precio, cantidad, fechaVencimiento);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar el producto: " + ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveToPdf(String codigo, String nombre, double precio, int cantidad, String fechaVencimiento) {
        Document document = new Document();
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Consulta como PDF");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Añadir contenido al PDF
                document.add(new Paragraph("Resultado de la Consulta"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Código: " + codigo));
                document.add(new Paragraph("Nombre: " + nombre));
                document.add(new Paragraph("Precio: " + precio));
                document.add(new Paragraph("Cantidad: " + cantidad));
                document.add(new Paragraph("Fecha de Vencimiento: " + fechaVencimiento));

                document.close();
                JOptionPane.showMessageDialog(this, "Consulta guardada exitosamente en " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}
