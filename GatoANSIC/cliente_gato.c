#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PUERTO 7000
#define TAMBUFER 1024

int main() {
    int cliente;
    struct sockaddr_in direccion;
    char buffer[TAMBUFER];

    // Crear socket
    cliente = socket(AF_INET, SOCK_STREAM, 0);
    if (cliente == -1) {
        perror("Error al crear el socket");
        exit(EXIT_FAILURE);
    }

    // Configurar dirección del servidor
    memset(&direccion, 0, sizeof(direccion));
    direccion.sin_family = AF_INET;
    direccion.sin_port = htons(PUERTO);
    inet_pton(AF_INET, "127.0.0.1", &direccion.sin_addr);

    // Conectar al servidor
    if (connect(cliente, (struct sockaddr*)&direccion, sizeof(direccion)) == -1) {
        perror("Error al conectar al servidor");
        exit(EXIT_FAILURE);
    }

    printf("Conectado al servidor. Esperando inicio de juego...\n");

    // Bucle principal del cliente (lógica del juego)
    while (1) {
        // Recibir mensaje del servidor
        ssize_t bytes_recibidos = recv(cliente, buffer, TAMBUFER, 0);
        if (bytes_recibidos <= 0) {
            printf("El servidor ha cerrado la conexión.\n");
            break;
        }
        buffer[bytes_recibidos] = '\0';

        // Imprimir mensaje del servidor
        printf("%s", buffer);

        // Si el mensaje indica que es el turno del cliente
        if (strstr(buffer, "¿Qué casilla juegas?") != NULL) {
            // Leer entrada del usuario
            int casilla;
            printf("Ingrese el número de casilla (1-9): ");
            scanf("%d", &casilla);

            // Enviar la jugada al servidor
            snprintf(buffer, TAMBUFER, "%d\n", casilla);
            send(cliente, buffer, strlen(buffer), 0);
        }
    }

    // Cerrar socket del cliente
    close(cliente);

    return 0;
}
