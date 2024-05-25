import networkx as nx

def leer_grafo(archivo):
    grafo = nx.DiGraph()
    with open(archivo, 'r') as f:
        for linea in f:
            ciudad1, ciudad2, distancia = linea.split()
            grafo.add_edge(ciudad1, ciudad2, weight=int(distancia))
    return grafo

def floyd_warshall(grafo):
    predecesores, distancias = nx.floyd_warshall_predecessor_and_distance(grafo)
    return predecesores, distancias

def mostrar_ruta_mas_corta(predecesores, distancias, origen, destino):
    if origen not in distancias or destino not in distancias[origen]:
        print("No hay ruta disponible de {} a {}".format(origen, destino))
        return
    
    print("La ruta más corta de {} a {} tiene una distancia de {}".format(origen, destino, distancias[origen][destino]))
    camino = []
    while destino != origen:
        camino.insert(0, destino)
        destino = predecesores[origen][destino]
    camino.insert(0, origen)
    print("Ruta: " + " -> ".join(camino))

def centro_del_grafo(grafo, distancias):
    exc = {nodo: max(distancias[nodo].values()) for nodo in grafo.nodes()}
    centro = min(exc, key=exc.get)
    return centro

def main():
    archivo = 'guategrafo.txt'
    try:
        grafo = leer_grafo(archivo)
    except FileNotFoundError:
        print(f"El archivo {archivo} no se encontró. Por favor, asegúrese de que el archivo exista en el directorio actual.")
        return

    predecesores, distancias = floyd_warshall(grafo)
    
    while True:
        print("Opciones:")
        print("1. Mostrar ruta más corta entre dos ciudades")
        print("2. Mostrar la ciudad que es el centro del grafo")
        print("3. Modificar el grafo")
        print("4. Finalizar")
        opcion = int(input())

        if opcion == 1:
            origen = input("Ingrese la ciudad origen: ")
            destino = input("Ingrese la ciudad destino: ")
            mostrar_ruta_mas_corta(predecesores, distancias, origen, destino)
        
        elif opcion == 2:
            centro = centro_del_grafo(grafo, distancias)
            print("La ciudad que es el centro del grafo es:", centro)
        
        elif opcion == 3:
            sub_opcion = int(input("Ingrese 1 para eliminar una conexión, 2 para agregar una conexión: "))
            if sub_opcion == 1:
                origen = input("Ingrese la ciudad origen: ")
                destino = input("Ingrese la ciudad destino: ")
                if grafo.has_edge(origen, destino):
                    grafo.remove_edge(origen, destino)
                    predecesores, distancias = floyd_warshall(grafo)
                else:
                    print("No existe una conexión de {} a {}".format(origen, destino))
            elif sub_opcion == 2:
                origen = input("Ingrese la ciudad origen: ")
                destino = input("Ingrese la ciudad destino: ")
                distancia = int(input("Ingrese la distancia: "))
                grafo.add_edge(origen, destino, weight=distancia)
                predecesores, distancias = floyd_warshall(grafo)
        
        elif opcion == 4:
            break
        
        else:
            print("Opción inválida")

if __name__ == "__main__":
    main()

