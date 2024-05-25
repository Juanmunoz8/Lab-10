import java.io.*;
import java.util.*;

public class Grafo {
    private int[][] matrizAdyacencia;
    private String[] ciudades;
    private int numCiudades;
    private final int INFINITO = 999999;
    private Map<String, Integer> ciudadIndiceMap;

    public Grafo(int numCiudades) {
        this.numCiudades = numCiudades;
        matrizAdyacencia = new int[numCiudades][numCiudades];
        ciudades = new String[numCiudades];
        ciudadIndiceMap = new HashMap<>();
        for (int i = 0; i < numCiudades; i++) {
            Arrays.fill(matrizAdyacencia[i], INFINITO);
            matrizAdyacencia[i][i] = 0;
        }
    }

    public void agregarCiudad(int indice, String nombre) {
        ciudades[indice] = nombre;
        ciudadIndiceMap.put(nombre, indice);
    }

    public void agregarArco(String origen, String destino, int distancia) {
        int indiceOrigen = ciudadIndiceMap.get(origen);
        int indiceDestino = ciudadIndiceMap.get(destino);
        matrizAdyacencia[indiceOrigen][indiceDestino] = distancia;
    }

    public void eliminarArco(String origen, String destino) {
        int indiceOrigen = ciudadIndiceMap.get(origen);
        int indiceDestino = ciudadIndiceMap.get(destino);
        matrizAdyacencia[indiceOrigen][indiceDestino] = INFINITO;
    }

    public void floydWarshall() {
        int[][] dist = new int[numCiudades][numCiudades];
        int[][] siguiente = new int[numCiudades][numCiudades];

        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                dist[i][j] = matrizAdyacencia[i][j];
                if (matrizAdyacencia[i][j] != INFINITO && i != j) {
                    siguiente[i][j] = j;
                } else {
                    siguiente[i][j] = -1;
                }
            }
        }

        for (int k = 0; k < numCiudades; k++) {
            for (int i = 0; i < numCiudades; i++) {
                for (int j = 0; j < numCiudades; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }

        // Mostrar resultados
        System.out.println("Matriz de distancias más cortas:");
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (dist[i][j] == INFINITO) {
                    System.out.print("INF ");
                } else {
                    System.out.print(dist[i][j] + " ");
                }
            }
            System.out.println();
        }

        System.out.println("Rutas más cortas entre cada par de ciudades:");
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j && dist[i][j] != INFINITO) {
                    System.out.print("Ruta de " + ciudades[i] + " a " + ciudades[j] + ": " + ciudades[i]);
                    int u = i;
                    while (u != j) {
                        u = siguiente[u][j];
                        System.out.print(" -> " + ciudades[u]);
                    }
                    System.out.println(" (Distancia: " + dist[i][j] + ")");
                }
            }
        }
    }

    public String centroDelGrafo() {
        int[] eccentricity = new int[numCiudades];
        int[][] dist = new int[numCiudades][numCiudades];

        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                dist[i][j] = matrizAdyacencia[i][j];
            }
        }

        for (int k = 0; k < numCiudades; k++) {
            for (int i = 0; i < numCiudades; i++) {
                for (int j = 0; j < numCiudades; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        for (int i = 0; i < numCiudades; i++) {
            eccentricity[i] = Arrays.stream(dist[i]).max().getAsInt();
        }

        int minEccentricity = Arrays.stream(eccentricity).min().getAsInt();
        int centro = -1;
        for (int i = 0; i < numCiudades; i++) {
            if (eccentricity[i] == minEccentricity) {
                centro = i;
                break;
            }
        }

        return ciudades[centro];
    }

    public static void main(String[] args) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("guategrafo.txt"));
            List<String[]> lineas = new ArrayList<>();
            String linea;

            while ((linea = br.readLine()) != null) {
                lineas.add(linea.split(" "));
            }

            Set<String> ciudadesUnicas = new HashSet<>();
            for (String[] datos : lineas) {
                ciudadesUnicas.add(datos[0]);
                ciudadesUnicas.add(datos[1]);
            }

            int numCiudades = ciudadesUnicas.size();
            Grafo grafo = new Grafo(numCiudades);

            int indice = 0;
            for (String ciudad : ciudadesUnicas) {
                grafo.agregarCiudad(indice, ciudad);
                indice++;
            }

            for (String[] datos : lineas) {
                String ciudad1 = datos[0];
                String ciudad2 = datos[1];
                int distancia = Integer.parseInt(datos[2]);
                grafo.agregarArco(ciudad1, ciudad2, distancia);
            }

            Scanner scanner = new Scanner(System.in);
            boolean continuar = true;

            while (continuar) {
                System.out.println("Opciones:");
                System.out.println("1. Mostrar ruta más corta entre dos ciudades");
                System.out.println("2. Mostrar la ciudad que es el centro del grafo");
                System.out.println("3. Modificar el grafo");
                System.out.println("4. Finalizar");
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.println("Ingrese la ciudad origen:");
                        String origen = scanner.nextLine();
                        System.out.println("Ingrese la ciudad destino:");
                        String destino = scanner.nextLine();
                        grafo.floydWarshall();
                        break;

                    case 2:
                        String centro = grafo.centroDelGrafo();
                        System.out.println("La ciudad que es el centro del grafo es: " + centro);
                        break;

                    case 3:
                        System.out.println("Ingrese 1 para eliminar una conexión, 2 para agregar una conexión:");
                        int subOpcion = scanner.nextInt();
                        scanner.nextLine();
                        if (subOpcion == 1) {
                            System.out.println("Ingrese la ciudad origen:");
                            origen = scanner.nextLine();
                            System.out.println("Ingrese la ciudad destino:");
                            destino = scanner.nextLine();
                            grafo.eliminarArco(origen, destino);
                        } else if (subOpcion == 2) {
                            System.out.println("Ingrese la ciudad origen:");
                            origen = scanner.nextLine();
                            System.out.println("Ingrese la ciudad destino:");
                            destino = scanner.nextLine();
                            System.out.println("Ingrese la distancia:");
                            int distancia = scanner.nextInt();
                            scanner.nextLine();
                            grafo.agregarArco(origen, destino, distancia);
                        }
                        grafo.floydWarshall();
                        break;

                    case 4:
                        continuar = false;
                        break;

                    default:
                        System.out.println("Opción inválida");
                        break;
                }
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("El archivo guategrafo.txt no se encontró. Por favor, asegúrese de que el archivo exista en el directorio actual.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
