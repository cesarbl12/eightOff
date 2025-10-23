// El nombre de tu módulo puede ser diferente.
// El log de error dice que se llama 'com.example.eightoff'
module com.example.eightoff {

    // 1. Requerir los módulos de JavaFX que estás usando
    requires javafx.controls;
    requires javafx.graphics;

    // 2. ¡ESTA ES LA CORRECCIÓN!
    //    Necesitas "abrir" tu paquete 'principal' al módulo 'javafx.graphics'.
    //    Esto le da permiso a JavaFX para usar "reflection" y construir
    //    la instancia de tu clase 'Main'.
    opens principal to javafx.graphics;

    // 3. (Opcional) Si en el futuro tus controladores o vistas
    //    necesitaran ser accedidos por JavaFX (por ejemplo, para FXML),
    //    también los abrirías. Por ahora, esto no es necesario.
    //    opens controlador to javafx.fxml;
    //    opens vista to javafx.fxml;
}