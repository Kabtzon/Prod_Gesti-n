package ExamenFinal;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Facturacion extends JFrame {
    private JTextField nombreCompradorField, telefonoCompradorField, emailCompradorField, nitCompradorField;
    private DefaultTableModel model;
    private JTable table;
    private JLabel totalVentaLabel;
    private double totalVenta;

    public Facturacion(boolean searchByCode) {
        setTitle("Facturación - Heladería Munshi");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Panel de datos del comprador
        JPanel buyerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buyerPanel.setBorder(BorderFactory.createTitledBorder("Datos del Comprador"));
        buyerPanel.add(new JLabel("Nombre del comprador:"));
        nombreCompradorField = new JTextField();
        buyerPanel.add(nombreCompradorField);

        buyerPanel.add(new JLabel("Teléfono del comprador:"));
        telefonoCompradorField = new JTextField();
        buyerPanel.add(telefonoCompradorField);

        buyerPanel.add(new JLabel("E-mail del comprador:"));
        emailCompradorField = new JTextField();
        buyerPanel.add(emailCompradorField);

        buyerPanel.add(new JLabel("NIT:"));
        nitCompradorField = new JTextField();
        buyerPanel.add(nitCompradorField);

        // Panel para la tabla de productos
        model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Nombre");
        model.addColumn("Fecha Vencimiento");
        model.addColumn("Precio Unitario");
        model.addColumn("Cantidad");
        model.addColumn("Precio Total");

        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Botón para agregar productos
        JButton addProductButton = new JButton("Agregar Producto");
        addProductButton.addActionListener(e -> agregarProducto(searchByCode));

        // Etiqueta para el precio total de la venta
        totalVentaLabel = new JLabel("Precio Total de Venta: Q0.00");

        // Botón para realizar la venta
        JButton sellButton = new JButton("Vender");
        sellButton.addActionListener(e -> realizarVenta());

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(buyerPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(addProductButton);
        bottomPanel.add(totalVentaLabel);
        bottomPanel.add(sellButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void agregarProducto(boolean searchByCode) {
        String codigo = JOptionPane.showInputDialog(this, "Ingrese el código o nombre del producto:");
        
        // Buscar producto en la base de datos
        String query = searchByCode ?
                "SELECT * FROM producto WHERE CodigoProducto = ?" :
                "SELECT * FROM producto WHERE NombreProducto = ?";
        
        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, codigo);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String codigoProducto = resultSet.getString("CodigoProducto");
                String nombreProducto = resultSet.getString("NombreProducto");
                double precioUnitario = resultSet.getDouble("PrecioUnitario");
                int cantidadDisponible = resultSet.getInt("CantidadProducto");
                String fechaVencimiento = resultSet.getDate("FechaVencimiento").toString();

                // Selección de cantidad a vender
                String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad a vender (disponible: " + cantidadDisponible + "):");
                int cantidad = Integer.parseInt(cantidadStr);
                
                if (cantidad > cantidadDisponible) {
                    JOptionPane.showMessageDialog(this, "Cantidad excede el límite disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double precioTotal = precioUnitario * cantidad;
                model.addRow(new Object[]{codigoProducto, nombreProducto, fechaVencimiento, precioUnitario, cantidad, precioTotal});

                // Actualizar el precio total de la venta
                totalVenta += precioTotal;
                totalVentaLabel.setText("Precio Total de Venta: Q" + totalVenta);
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarVenta() {
        // Realizar la actualización en la base de datos para cada producto en la tabla
        try (Connection connection = DatabaseConnection.conectar()) {
            for (int row = 0; row < model.getRowCount(); row++) {
                String codigoProducto = model.getValueAt(row, 0).toString();
                int cantidadVendida = Integer.parseInt(model.getValueAt(row, 4).toString());

                // Actualizar la cantidad en la base de datos
                String updateQuery = "UPDATE producto SET CantidadProducto = CantidadProducto - ? WHERE CodigoProducto = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, cantidadVendida);
                    statement.setString(2, codigoProducto);
                    statement.executeUpdate();
                }
            }

            // Mostrar mensaje de venta exitosa y preguntar si quiere guardar el PDF
            int choice = JOptionPane.showConfirmDialog(this, "Venta exitosa. ¿Desea guardar la factura en PDF?", "Venta", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                saveInvoiceToPdf();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al realizar la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveInvoiceToPdf() {
        Document document = new Document();
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Factura como PDF");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Encabezado de la factura
                document.add(new Paragraph("HELADERIA MUNSHI"));
                document.add(new Paragraph(" "));

                // Datos del comprador
                document.add(new Paragraph("Nombre del comprador: " + nombreCompradorField.getText()));
                document.add(new Paragraph("Teléfono del comprador: " + telefonoCompradorField.getText()));
                document.add(new Paragraph("E-mail del comprador: " + emailCompradorField.getText()));
                document.add(new Paragraph("NIT: " + nitCompradorField.getText()));
                document.add(new Paragraph(" "));

                // Tabla de productos
                PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Agregar encabezados al PDF
                for (int i = 0; i < model.getColumnCount(); i++) {
                    pdfTable.addCell(new Paragraph(model.getColumnName(i)));
                }

                // Agregar filas al PDF
                for (int rows = 0; rows < model.getRowCount(); rows++) {
                    for (int cols = 0; cols < model.getColumnCount(); cols++) {
                        pdfTable.addCell(new Paragraph(model.getValueAt(rows, cols).toString()));
                    }
                }

                document.add(pdfTable);

                // Precio total
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Precio Total de Venta: Q" + totalVenta));

                document.close();
                JOptionPane.showMessageDialog(this, "Factura guardada exitosamente en " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
