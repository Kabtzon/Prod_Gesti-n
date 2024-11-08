package ExamenFinal;

import javax.swing.*;
import java.awt.*;

public class CRUDProductos extends JFrame {
    private JButton registerButton, consultButton, updateButton, deleteButton, viewAllButton, billingButton;
    private JRadioButton searchByCode, searchByName;

    public CRUDProductos() {
        setTitle("Gestión de Productos");
        setSize(500, 500);  // Tamaño ajustado de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel de selección de búsqueda OPCION CODIGO O NOMBRE    
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Opciones de Búsqueda", 
                0, 0, null, Color.DARK_GRAY));
        
        searchByCode = new JRadioButton("Buscar por Código");
        searchByName = new JRadioButton("Buscar por Nombre");
        ButtonGroup searchGroup = new ButtonGroup();
        searchGroup.add(searchByCode);
        searchGroup.add(searchByName);
        searchPanel.add(searchByCode);
        searchPanel.add(searchByName);

        // Panel de botones CRUD tamaños 
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        registerButton = new JButton("Registrar Producto");
        consultButton = new JButton("Consultar Producto");
        updateButton = new JButton("Actualizar Producto");
        deleteButton = new JButton("Eliminar Producto");
        viewAllButton = new JButton("Ver Todos los Productos");  // ver todos los productos
        billingButton = new JButton("Facturación"); // facturación

        buttonPanel.add(registerButton);    //registo
        buttonPanel.add(consultButton); //consulta
        buttonPanel.add(updateButton);  //actualizar
        buttonPanel.add(deleteButton);  //eliminar
        buttonPanel.add(viewAllButton);  //inventario
        buttonPanel.add(billingButton);  // botón de facturación 

        // Configuración del diseño principal
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Eventos de botones
        registerButton.addActionListener(e -> openRegisterWindow());
        consultButton.addActionListener(e -> openConsultWindow());
        updateButton.addActionListener(e -> openUpdateWindow());
        deleteButton.addActionListener(e -> openDeleteWindow());
        viewAllButton.addActionListener(e -> openViewAllWindow());  
        billingButton.addActionListener(e -> openBillingWindow()); //abrir la ventana de facturación

        setVisible(true);
    }

    // Métodos para abrir ventanas de operación
    private void openRegisterWindow() {
        new Registro(searchByCode.isSelected());
    }

    private void openConsultWindow() {
        new Consulta(searchByCode.isSelected());
    }

    private void openUpdateWindow() {
        new Actualizar(searchByCode.isSelected());
    }

    private void openDeleteWindow() {
        new Eliminar(searchByCode.isSelected());
    }

    private void openViewAllWindow() {
        new Inventario();  // Abrir la clase Inventario
    }

    private void openBillingWindow() {
        new Facturacion(searchByCode.isSelected());  // Abrir la clase de Facturación
    }

    public static void main(String[] args) {
        new CRUDProductos();
    }
}
