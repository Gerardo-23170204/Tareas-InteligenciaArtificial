package arbol;

public class App {
    public static void main(String[] args) {

        Arbol proyecto = new Arbol();
        proyecto.insertar("Zulema");
        proyecto.insertar("Ana");
        proyecto.insertar("Gerardo");

        proyecto.imprimirArbol();

    }
}
