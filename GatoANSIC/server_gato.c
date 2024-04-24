#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#define PUERTO 7000
#define TAMBUFER 1024
#define NUM_CASILLAS 9

char tablero[NUM_CASILLAS + 1];  // Tablero de juego (de 1 a 9)
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// Función para mostrar el tablero en la consola
void mostrar_tablero() {
    printf(" %c | %c | %c\n", tablero[1], tablero[2], tablero[3]);
    printf("-----------\n");
    printf(" %c | %c | %c\n", tablero[4], tablero[5], tablero[6]);
    printf("-----------\n");
    printf(" %c | %c | %c\n", tablero[7], tablero[8], tablero[9]);
}


int verificar_ganador() {
    // Implementa la lógica para verificar si hay un ganador
    // Retorna 'X' si gana X, 'O' si gana O, ' ' si empate, o 0 si no ha terminado
}

// Función que maneja el juego para una partida individual
void* manejar_partida(void* args) {
    int* clientes = (int*)args;
    int jugador_actual = 0;  // Índice del jugador cuyo turno es actualmente
    char simbolos[2] = {'X', 'O'};  // Simbolos para los jugadores X y O

    // Inicializar el tablero
    memset(tablero, ' ', sizeof(tablero));
    tablero[NUM_CASILLAS] = '\0';

    // Bucle principal del juego
    while (1) {
        // Mostrar el tablero
        mostrar_tablero();

        // Esperar la jugada del jugador actual
        printf("Turno de %c. ¿Qué casilla juegas? ", simbolos[jugador_actual]);
        fflush(stdout);

        // Implementar la lógica para recibir la jugada del jugador
        // y actualizar el tablero

        // Verificar si hay un ganador o empate
        int resultado = verificar_ganador();
        if (resultado != 0) {
            // Mostrar resultado y finalizar partida
            if (resultado == ' ') {
                printf("¡Empate!\n");
            } else {
                printf("¡%c ha ganado!\n", resultado);
            }
            break;
        }

        // Cambiar al siguiente jugador
        jugador_actual = 1 - jugador_actual;
    }

    // Cerrar conexiones y liberar recursos
    close(clientes[0]);
    close(clientes[1]);
    free(clientes);
    pthread_exit(NULL);
}

int main() {
    int servidor, cliente;
    struct sockaddr_in direccion;
    socklen_t longitud_direccion;
    pthread_t hilo;

    // Crear socket
    servidor = socket(AF_INET, SOCK_STREAM, 0);
    if (servidor == -1) {
        perror("Error al crear el socket");
        exit(EXIT_FAILURE);
    }

    // Configurar dirección del servidor
    memset(&direccion, 0, sizeof(direccion));
    direccion.sin_family = AF_INET;
    direccion.sin_addr.s_addr = INADDR_ANY;
    direccion.sin_port = htons(PUERTO);

    // Vincular el socket a la dirección
    if (bind(servidor, (struct sockaddr*)&direccion, sizeof(direccion)) == -1) {
        perror("Error al vincular el socket");
        exit(EXIT_FAILURE);
    }

    // Escuchar por conexiones entrantes
    if (listen(servidor, 2) == -1) {
        perror("Error al escuchar por conexiones entrantes");
        exit(EXIT_FAILURE);
    }

    printf("Esperando conexiones de jugadores...\n");

    // Bucle principal del servidor
    while (1) {
        int* clientes = (int*)malloc(2 * sizeof(int));
        clientes[0] = accept(servidor, NULL, NULL);
        clientes[1] = accept(servidor, NULL, NULL);

        printf("¡Dos jugadores conectados!\n");

        // Crear un hilo para manejar la partida
        if (pthread_create(&hilo, NULL, manejar_partida, (void*)clientes) != 0) {
            perror("Error al crear el hilo para la partida");
            exit(EXIT_FAILURE);
        }

        // Separar el hilo para que pueda continuar aceptando conexiones
        pthread_detach(hilo);
    }

    // Cerrar socket del servidor (nunca debería llegar aquí)
    close(servidor);

    return 0;
}
