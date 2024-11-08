package ExamenFinal;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
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

public class Inventario extends JFrame {
    private DefaultTableModel model;

    public Inventario() {
        setTitle("Inventario");
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Modelo de la tabla
        model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Cantidad");
        model.addColumn("Fecha de Vencimiento");

        // Obtener datos de la base de datos y agregarlos al modelo de la tabla
        try (Connection connection = DatabaseConnection.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM producto")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getString("CodigoProducto"),
                        resultSet.getString("NombreProducto"),
                        resultSet.getDouble("PrecioUnitario"),
                        resultSet.getInt("CantidadProducto"),
                        resultSet.getDate("FechaVencimiento")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener los productos: " + ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }

        // Configuración de la tabla
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Botón para generar PDF
        JButton savePdfButton = new JButton("Guardar en PDF");
        savePdfButton.addActionListener(e -> saveInventoryToPdf());
        add(savePdfButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void saveInventoryToPdf() {
        Document document = new Document();
        try {
            // Seleccionar la ubicación de guardado del PDF
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Inventario como PDF");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Crear el PDF
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Título del documento
                document.add(new Paragraph("Inventario de Productos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK)));
                document.add(new Paragraph(" "));  // Espacio en blanco

                // Tabla en el PDF
                PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
                pdfTable.setWidthPercentage(100);
                
                // Agregar encabezados al PDF
                for (int i = 0; i < model.getColumnCount(); i++) {
                    pdfTable.addCell(new PdfPCell(new Phrase(model.getColumnName(i))));
                }

                // Agregar datos al PDF
                for (int rows = 0; rows < model.getRowCount(); rows++) {
                    for (int cols = 0; cols < model.getColumnCount(); cols++) {
                        pdfTable.addCell(new PdfPCell(new Phrase(model.getValueAt(rows, cols).toString())));
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "Inventario guardado exitosamente en " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
