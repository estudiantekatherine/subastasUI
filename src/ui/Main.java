package ui;

import cr.ac.ucenfotec.logica.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clase principal de la interfaz de consola de la plataforma de subastas
 * Solo gestiona el menú y delega toda la lógica al ControladorPlataforma
 *
 * @version 1.0
 */
public class Main {

    private static Service service = new Service();
    private static Scanner in = new Scanner(System.in);

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    /**
     * Punto de entrada principal de la aplicación
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        imprimirEncabezado();

        // Regla: verificar si existe un moderador al iniciar
        if (!service.existeModerador()) {
            System.out.println("\nNo se encontró ningún moderador registrado.");
            System.out.println("Es obligatorio registrar un moderador antes de continuar.\n");
            solicitarRegistroModerador();
        } else {
            System.out.println("\nModerador detectado. Bienvenido a la plataforma.\n");
        }

        mostrarMenuPrincipal();
    }

    /**
     * Imprime el encabezado decorativo de la aplicación.
     */
    private static void imprimirEncabezado() {
        System.out.println("PLATAFORMA DIGITAL DE SUBASTAS");
    }

    //Registro incial del moderador

    /**
     * Solicita los datos para registrar el moderador del sistema
     * Si el registro falla, vuelve a solicitarlo hasta que sea exitoso
     */
    private static void solicitarRegistroModerador() {
        System.out.println("REGISTRO DEL MODERADOR");
        String nombreCompleto = out("Nombre completo: ");
        String identificacion = out("Número de identificación: ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");
        String contrasena = out("Contraseña: ");
        String correo = out("Correo electrónico: ");

        String resultado = service.registrarModerador(
                nombreCompleto, identificacion, fechaNacimiento, contrasena, correo);

        System.out.println("\n→ " + resultado + "\n");

        if (resultado.startsWith("Error")) {
            System.out.println("Por favor, intente el registro nuevamente.\n");
            solicitarRegistroModerador();
        }
    }

    //Menú principal
    /**
     * Muestra el menú principal y procesa la opción seleccionada
     */
    private static void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println(" MENÚ PRINCIPAL");
            System.out.println("1. Registro de usuarios");
            System.out.println("2. Listado de usuarios");
            System.out.println("3. Creación de subastas");
            System.out.println("4. Listado de subastas");
            System.out.println("5. Creación de ofertas");
            System.out.println("6. Listado de ofertas");
            System.out.println("7. Inicio de sesión");
            System.out.println("8. Verificar moderador");
            System.out.println("0. Salir de la plataforma");

            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuRegistroUsuarios();
                case 2 -> mostrarListadoUsuarios();
                case 3 -> menuCreacionSubasta();
                case 4 -> mostrarListadoSubastas();
                case 5 -> menuCreacionOferta();
                case 6 -> mostrarListadoOfertas();
                case 7 -> menuInicioSesion();
                case 8 -> verificarModerador();
                case 0 -> System.out.println("\n¡Hasta luego! Gracias por usar la plataforma\n");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    //Opción: registro de usuarios
    /**
     * Submenú para elegir el tipo de usuario a registrar
     */
    private static void menuRegistroUsuarios() {
        System.out.println("REGISTRO DE USUARIOS");
        System.out.println("  1. Registrar Vendedor");
        System.out.println("  2. Registrar Coleccionista");
        System.out.println("  0. Volver al menú principal");

        int opcion = leerEntero("Seleccione el tipo de usuario: ");
        switch (opcion) {
            case 1 -> registrarVendedor();
            case 2 -> registrarColeccionista();
            case 0 -> { }
            default -> System.out.println("Opción no válida.");
        }
    }

    /**
     * Formulario de registro de un nuevo vendedor
     */
    private static void registrarVendedor() {
        System.out.println("\n--- Registro de Vendedor ---");
        String nombreCompleto = out("Nombre completo: ");
        String identificacion = out("Número de identificación: ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");
        String contrasena = out("Contraseña: ");
        String correo = out("Correo electrónico: ");
        String direccion = out("Dirección de residencia: ");

        String resultado = service.registrarVendedor(
                nombreCompleto, identificacion, fechaNacimiento,
                contrasena, correo, direccion);
        System.out.println("\n→ " + resultado);
    }

    /**
     * Formulario de registro de un nuevo coleccionista
     * Tras el registro exitoso, ofrece agregar objetos a su colección
     */
    private static void registrarColeccionista() {
        System.out.println("\n--- Registro de Coleccionista ---");
        String nombreCompleto = out("Nombre completo         : ");
        String identificacion = out("Número de identificación: ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");
        String contrasena = out("Contraseña              : ");
        String correo = out("Correo electrónico      : ");
        String direccion = out("Dirección de residencia : ");

        String resultado = service.registrarColeccionista(
                nombreCompleto, identificacion, fechaNacimiento,
                contrasena, correo, direccion);
        System.out.println("\n→ " + resultado);

        if (!resultado.startsWith("Error")) {
            menuAgregarObjetosAColeccion(identificacion);
        }
    }

    /**
     * Permite al coleccionista agregar objetos a su colección tras registrarse
     *
     * @param identificacionColeccionista Identificación del coleccionista registrado
     */
    private static void menuAgregarObjetosAColeccion(String identificacionColeccionista) {
        System.out.print("\n¿Desea agregar objetos a su colección ahora? (s/n): ");
        String respuesta = in.nextLine().trim().toLowerCase();

        while (respuesta.equals("s")) {
            System.out.println("\n-- Agregar Objeto a la Colección --");
            String nombreObjeto = out("Nombre del objeto    : ");
            String descripcion  = out("Descripción          : ");
            int opcionEstado    = leerOpcionEstadoObjeto();
            LocalDate fechaCompra = leerFecha("Fecha de compra (dd/MM/yyyy): ");

            String resultadoObjeto = service.agregarObjetoAColeccionista(
                    identificacionColeccionista, nombreObjeto, descripcion,
                    opcionEstado, fechaCompra);
            System.out.println("→ " + resultadoObjeto);

            System.out.print("¿Agregar otro objeto? (s/n): ");
            respuesta = in.nextLine().trim().toLowerCase();
        }
    }

    //Opción 2: Listado de usuarios
    /**
     * Muestra el listado completo de todos los usuarios registrados
     */
    private static void mostrarListadoUsuarios() {
        System.out.println("LISTADO DE USUARIOS");

        ArrayList<String> usuarios = service.getListaUsuariosFormateada();

        if (usuarios.isEmpty()) {
            System.out.println("  No hay usuarios registrados en la plataforma.");
            return;
        }
        for (String linea : usuarios) {
            System.out.println("  " + linea);
        }
        System.out.println("\nTotal de usuarios: " + service.getCantidadDeUsuarios());
    }

    //Opción 3: Creación de subastas

    /**
     * Formulario para crear una nueva subasta
     */
    private static void menuCreacionSubasta() {
        System.out.println("          CREACIÓN DE SUBASTA         ");

        String identificacionCreador = out("Identificación del creador: ");

        ArrayList<String> objetosDisponibles =
                service.getObjetosDeColeccionistaFormateados(identificacionCreador);
        if (!objetosDisponibles.isEmpty() && !objetosDisponibles.get(0).startsWith("Error")) {
            System.out.println("\nObjetos en su colección:");
            for (String linea : objetosDisponibles) {
                System.out.println("  " + linea);
            }
        }

        double precioMinimo = leerDecimal("Precio mínimo de aceptación (¢): ");
        LocalDateTime fechaVencimiento = leerFechaHora("Fecha de vencimiento (dd/MM/yyyy HH:mm): ");

        System.out.println("Ingrese los nombres de los objetos a subastar.");
        System.out.println("(Presione Enter con línea vacía para terminar)");
        ArrayList<String> nombresObjetos = new ArrayList<>();
        String nombreObjeto = out("Nombre del objeto: ");
        while (!nombreObjeto.isEmpty()) {
            nombresObjetos.add(nombreObjeto);
            nombreObjeto = out("Nombre del objeto: ");
        }

        String resultado = service.crearSubasta(
                identificacionCreador, precioMinimo, fechaVencimiento, nombresObjetos);
        System.out.println("\n→ " + resultado);
    }

    // Opción 4: Listado de subastas

    /**
     * Muestra el listado completo de todas las subastas registradas
     */
    private static void mostrarListadoSubastas() {
        System.out.println("LISTADO DE SUBASTAS");

        ArrayList<String> subastas = service.getListaSubastasFormateada();

        if (subastas.isEmpty()) {
            System.out.println("  No hay subastas registradas en la plataforma.");
            return;
        }
        for (String linea : subastas) {
            System.out.println("  " + linea);
        }
        System.out.println("\nTotal de subastas: " + service.getCantidadDeSubastas());
    }

    //Opción 5: Creación de ofertas

    /**
     * Formulario para registrar una nueva oferta sobre una subasta
     */
    private static void menuCreacionOferta() {
        System.out.println("CREACIÓN DE OFERTA");

        mostrarListadoSubastas();

        if (service.getCantidadDeSubastas() == 0) {
            System.out.println("No hay subastas disponibles para ofertar.");
            return;
        }

        int numeroSubasta= leerEntero("Número de la subasta a ofertar: ");
        String identificacionOferente = out("Identificación del coleccionista oferente: ");
        double precioOfertado= leerDecimal("Precio ofertado (¢): ");

        String resultado = service.crearOferta(
                identificacionOferente, numeroSubasta, precioOfertado);
        System.out.println("\n→ " + resultado);
    }

    //Opción 6: Listado de ofertas

    /**
     * Muestra el listado de todas las ofertas agrupadas por subasta
     */
    private static void mostrarListadoOfertas() {
        System.out.println("LISTADO DE OFERTAS");

        ArrayList<String> ofertas = service.getTodasLasOfertasFormateadas();

        if (ofertas.isEmpty()) {
            System.out.println("  No hay ofertas registradas en la plataforma.");
            return;
        }
        for (String linea : ofertas) {
            System.out.println("  " + linea);
        }
        System.out.println("\nTotal de ofertas: " + service.getCantidadTotalDeOfertas());
    }

    //Opción 7: Inicio de sesión
    /**
     * Formulario de inicio de sesión
     * Solicita correo y contraseña y delega la validación al Service
     */
    private static void menuInicioSesion() {
        System.out.println("INICIO DE SESIÓN");
        String correo    = out("Correo electrónico: ");
        String contrasena = out("Contraseña        : ");

        String resultado = service.iniciarSesion(correo, contrasena);
        System.out.println("\n " + resultado);
    }

    //Opción 8: Validar si existe un moderador
    /**
     * Verifica si existe un moderador registrado en el sistema
     * y muestra el resultado al usuario
     */
    private static void verificarModerador() {
        if (service.existeModerador()) {
            System.out.println("\n Existe un moderador registrado en la plataforma");
        } else {
            System.out.println("\n No existe ningún moderador registrado en la plataforma");
        }
    }

    //Otros métodos: auxiliares de lectura - validaciones manuales

    /**
     * Lee una línea de texto desde la consola
     *
     * @param mensaje Mensaje que se muestra al usuario
     * @return Texto ingresado sin espacios al inicio o al final
     */
    private static String out(String mensaje) {
        System.out.print(mensaje);
        return in.nextLine().trim();
    }

    /**
     * Lee un número entero desde la consola
     * Si el usuario ingresa algo que no es número, vuelve a pedirlo
     *
     * @param mensaje Mensaje que se muestra al usuario
     * @return Número entero ingresado
     */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Ingrese un número entero.");
            }
        }
    }

    /**
     * Lee un número decimal desde la consola
     * Si el usuario ingresa algo que no es número, vuelve a pedirlo
     *
     * @param mensaje Mensaje que se muestra al usuario.
     * @return Número decimal ingresado.
     */
    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Ingrese un número decimal (ejemplo: 150.50).");
            }
        }
    }

    /**
     * Lee una fecha desde la consola en formato dd/MM/yyyy
     * Si el formato es incorrecto, vuelve a pedirla
     *
     * @param mensaje Mensaje que se muestra al usuario.
     * @return Fecha ingresada.
     */
    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDate.parse(in.nextLine().trim(), FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println(" Formato inválido. Use dd/MM/yyyy (ejemplo: 15/03/1990).");
            }
        }
    }

    /**
     * Lee una fecha y hora desde la consola en formato dd/MM/yyyy HH:mm.
     * Si el formato es incorrecto, vuelve a pedirla.
     *
     * @param mensaje Mensaje que se muestra al usuario.
     * @return Fecha y hora ingresada.
     */
    private static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDateTime.parse(in.nextLine().trim(), FORMATO_FECHA_HORA);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy HH:mm (ejemplo: 25/03/2026 18:00).");
            }
        }
    }

    /**
     * Muestra las opciones de estado físico y retorna la opción elegida.
     *
     * @return Número de opción: 1=Nuevo, 2=Usado, 3=Antiguo sin abrir.
     */
    private static int leerOpcionEstadoObjeto() {
        System.out.println("Estado físico del objeto:");
        System.out.println("  1. Nuevo");
        System.out.println("  2. Usado");
        System.out.println("  3. Antiguo sin abrir");
        int opcion = leerEntero("Seleccione el estado (1-3): ");
        if (opcion < 1 || opcion > 3) {
            System.out.println("⚠ Opción inválida. Se asignará estado 'Nuevo' por defecto.");
            return 1;
        }
        return opcion;
    }
}