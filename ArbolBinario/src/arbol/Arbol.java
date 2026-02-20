package arbol;

public class Arbol {
    private Nodo raiz;

    public Arbol() {
        this.raiz = null;
    }

    public void insertar(String nombre) {
        raiz = insertarRecursivo(raiz, nombre);
    }

    private Nodo insertarRecursivo(Nodo actual, String nombre) {
        if (actual == null) {
            return new Nodo(nombre);
        }

        if (nombre.compareTo(actual.nombre) < 0) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, nombre);
        } else if (nombre.compareTo(actual.nombre) > 0) {
            actual.derecho = insertarRecursivo(actual.derecho, nombre);
        }

        return actual;
    }

    // IMPRIMIR ARBOL (Inorden)
    public void imprimirArbol() {
        System.out.println("Contenido del arbol (Orden Alfabetico):");
        ayudanteImprimir(raiz);
        System.out.println();
    }

    private void ayudanteImprimir(Nodo actual) {
        if (actual != null) {
            ayudanteImprimir(actual.izquierdo);           // Visita hijo izquierdo
            System.out.print("[" + actual.nombre + "] "); // Imprime el nodo actual
            ayudanteImprimir(actual.derecho);             // Visita hijo derecho
        }
    }
}
