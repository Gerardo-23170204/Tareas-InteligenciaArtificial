
public class App {
    public static void main(String[] args) {
        // Tablero inicial directo y fácil de explicar
        int[] tableroInicial = {
                2,  1,  3,  4,  5,
                6,  7,  8, 10, 15,
                11, 12,  9, 14, 20,
                16, 17, 13, 19, 24,
                21, 22, 18, 23,  0
        };

        // Manhattan
        Puzzle24 resolverManhattan = new Puzzle24(tableroInicial, "manhattan");
        resolverManhattan.resolver();

        // Conflicto Lineal
        Puzzle24 resolverLineal = new Puzzle24(tableroInicial, "lineal");
        resolverLineal.resolver();

        System.out.println("\n---------------- ANALISIS DE RENDIMIENTO ----------------");
        System.out.printf("%-15s | %-16s | %-12s | %-10s%n", "Heurística", "Nodos Expandidos", "Movimientos", "Tiempo");
        System.out.println("------------------------------------------------------------------");

        System.out.printf("%-15s | %-16d | %-12d | %-10d ms%n",
                    "manhattan", resolverManhattan.obtenerNodosExpandidos(), resolverManhattan.obtenerLongitudSolucion(), resolverManhattan.obtenerTiempoTranscurrido());

        System.out.printf("%-15s | %-16d | %-12d | %-10d ms%n",
                    "lineal", resolverLineal.obtenerNodosExpandidos(), resolverLineal.obtenerLongitudSolucion(), resolverLineal.obtenerTiempoTranscurrido());

        System.out.println("------------------------------------------------------------------");

    }

}