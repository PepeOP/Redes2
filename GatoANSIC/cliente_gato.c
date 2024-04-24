// Código del cliente (game_client.c)

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

    // Bucle principal del cliente (puedes implementar la lógica del juego aquí)
    while (1) {
        // Implementa la lógica del juego aquí (enviar y recibir mensajes del servidor)
    }

    // Cerrar socket del cliente
    close(cliente);

    return 0;
}
