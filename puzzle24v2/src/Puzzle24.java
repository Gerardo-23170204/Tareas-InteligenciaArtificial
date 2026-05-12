import java.util.*;

public class Puzzle24 {
    private static final int dimension = 5;

    private long tiempoTranscurrido;
    private int[] tablero;
    private int[] tableroOriginal;

    private int posVacia;
    private int umbral;
    private int nodosExpandidos;
    private boolean resuelto;
    private List<String> ruta = new ArrayList<>();
    private String tipoHeuristica;

    public Puzzle24(int[] tableroInicial, String tipoHeuristica) {
        this.tipoHeuristica = tipoHeuristica;
        this.tablero = tableroInicial.clone();
        this.tableroOriginal = tableroInicial.clone();

        // Encontrar donde esta el espacio vacío
        for (int i = 0; i < this.tablero.length; i++) {
            if (this.tablero[i] == 0) {
                this.posVacia = i;
                break;
            }
        }
    }

    // Impresion
    public void imprimirTablero(int[] estado) {
        for (int i = 0; i < estado.length; i++) {
            if (estado[i] == 0) {
                System.out.print("  0 ");
            } else {
                System.out.printf("%3d ", estado[i]);
            }
            if ((i + 1) % dimension == 0) System.out.println();
        }
        System.out.println("---------------------");
    }

    private void imprimirPasosSolucion() {
        System.out.println("\nMovimientos (" + tipoHeuristica.toUpperCase() + ")");

        int[] tableroPasoActual = tableroOriginal.clone();
        int vaciaActual = -1;
        for (int i = 0; i < tableroPasoActual.length; i++) {
            if (tableroPasoActual[i] == 0) vaciaActual = i;
        }

        System.out.println("Movimiento 0 (Estado Inicial):");
        imprimirTablero(tableroPasoActual);

        for (int i = 0; i < ruta.size(); i++) {
            String movimiento = ruta.get(i);
            int nuevaVacia = vaciaActual;

            if (movimiento.equals("ARRIBA")) nuevaVacia -= dimension;
            else if (movimiento.equals("ABAJO")) nuevaVacia += dimension;
            else if (movimiento.equals("IZQUIERDA")) nuevaVacia -= 1;
            else if (movimiento.equals("DERECHA")) nuevaVacia += 1;

            // Intercambiar las piezas físicamente en el arreglo para la impresión
            int temporal = tableroPasoActual[vaciaActual];
            tableroPasoActual[vaciaActual] = tableroPasoActual[nuevaVacia];
            tableroPasoActual[nuevaVacia] = temporal;
            vaciaActual = nuevaVacia;

            System.out.println("Movimiento " + (i + 1) + ":");
            imprimirTablero(tableroPasoActual);
        }
    }

    // DISTANCIA DE MANHATTAN
    public int obtenerDistanciaManhattan(int[] estado) {
        int distancia = 0;
        for (int i = 0; i < estado.length; i++) {
            int valor = estado[i];
            if (valor != 0) {
                int indiceObjetivo = valor - 1;
                distancia += Math.abs(i / dimension - indiceObjetivo / dimension) +
                        Math.abs(i % dimension - indiceObjetivo % dimension);
            }
        }
        return distancia;
    }

    // CONFLICTO LINEAL
    public int obtenerConflictoLineal(int[] estado) {
        int distancia = obtenerDistanciaManhattan(estado);
        int conflictos = 0;

        // Revisar filas
        for (int fila = 0; fila < dimension; fila++) {
            for (int i = 0; i < dimension; i++) {
                for (int j = i + 1; j < dimension; j++) {
                    int valor1 = estado[fila * dimension + i];
                    int valor2 = estado[fila * dimension + j];
                    if (valor1 != 0 && valor2 != 0 && (valor1 - 1) / dimension == fila && (valor2 - 1) / dimension == fila) {
                        if (valor1 > valor2) conflictos++;
                    }
                }
            }
        }

        // Revisar columnas
        for (int col = 0; col < dimension; col++) {
            for (int i = 0; i < dimension; i++) {
                for (int j = i + 1; j < dimension; j++) {
                    int valor1 = estado[i * dimension + col];
                    int valor2 = estado[j * dimension + col];
                    if (valor1 != 0 && valor2 != 0 && (valor1 - 1) % dimension == col && (valor2 - 1) % dimension == col) {
                        if (valor1 > valor2) conflictos++;
                    }
                }
            }
        }
        return distancia + (2 * conflictos);
    }

    public int obtenerHeuristica(int[] estado) {
        if (tipoHeuristica.equalsIgnoreCase("lineal")) {
            return obtenerConflictoLineal(estado);
        }
        return obtenerDistanciaManhattan(estado);
    }

    // IDA*
    public void resolver() {
        nodosExpandidos = 0;
        umbral = obtenerHeuristica(tablero);
        long tiempoInicio = System.currentTimeMillis();

        while (true) {
            int temporal = buscar(0, umbral, "");
            if (resuelto) {
                tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                imprimirPasosSolucion();
                return;
            }
            if (temporal == Integer.MAX_VALUE) break;
            umbral = temporal; // Incrementar el umbral
        }
    }

    // Getters para la tabla
    public int obtenerNodosExpandidos() { return nodosExpandidos; }
    public int obtenerLongitudSolucion() { return ruta.size(); }
    public long obtenerTiempoTranscurrido() { return tiempoTranscurrido; }

    //Formula utilizada: f(n)= g(n) + h(n)
    private int buscar(int costoG, int umbralActual, String ultimoMovimiento) {
        int h = obtenerHeuristica(tablero);
        int f = costoG + h;

        if (f > umbralActual) return f;
        if (h == 0) {
            resuelto = true;
            return -1;
        }

        int minimo = Integer.MAX_VALUE;
        nodosExpandidos++;

        int fila = posVacia / dimension;
        int columna = posVacia % dimension;

        int[] movimientos = {-dimension, dimension, -1, 1};
        String[] etiquetas = {"ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"};

        for (int i = 0; i < 4; i++) {
            int movimiento = movimientos[i];
            int nuevaPos = posVacia + movimiento;

            // Validar límites del tablero
            if (nuevaPos < 0 || nuevaPos >= dimension * dimension) continue;
            if (movimiento == -1 && columna == 0) continue;
            if (movimiento == 1 && columna == dimension - 1) continue;

            // Evitar ciclos triviales (ir y volver inmediatamente)
            if (esMovimientoInverso(ultimoMovimiento, etiquetas[i])) continue;

            // Aplicar el movimiento
            intercambiar(posVacia, nuevaPos);
            int vaciaAnterior = posVacia;
            posVacia = nuevaPos;
            ruta.add(etiquetas[i]);

            // Llamada recursiva
            int t = buscar(costoG + 1, umbralActual, etiquetas[i]);

            if (resuelto) return -1;

            // Deshacer el movimiento para explorar otra rama
            ruta.remove(ruta.size() - 1);
            posVacia = vaciaAnterior;
            intercambiar(posVacia, nuevaPos);

            if (t < minimo) minimo = t;
        }
        return minimo;
    }

    private void intercambiar(int i, int j) {
        int temporal = tablero[i];
        tablero[i] = tablero[j];
        tablero[j] = temporal;
    }

    private boolean esMovimientoInverso(String anterior, String actual) {
        if (anterior.equals("ARRIBA") && actual.equals("ABAJO")) return true;
        if (anterior.equals("ABAJO") && actual.equals("ARRIBA")) return true;
        if (anterior.equals("IZQUIERDA") && actual.equals("DERECHA")) return true;
        if (anterior.equals("DERECHA") && actual.equals("IZQUIERDA")) return true;
        return false;
    }
}