#ifdef _WIN32
    #include <winsock2.h>
    #include <ws2tcpip.h>
    #pragma comment(lib, "ws2_32.lib")
#else
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <unistd.h>
    #include <string.h>
#endif

#include <stdio.h>
#include <stdlib.h>

#define PORT 8080
#define BUFFER_SIZE 1024

int main() {
    int sock = 0, valread;
    struct sockaddr_in serv_addr;
    char buffer[BUFFER_SIZE] = {0};

#ifdef _WIN32
    WSADATA wsa;
    WSAStartup(MAKEWORD(2, 2), &wsa);
#endif

    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        printf("\n Error al crear el socket \n");
        return -1;
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT);

#ifdef _WIN32
    if (InetPton(AF_INET, "127.0.0.1", &serv_addr.sin_addr) <= 0) {
#else
    if (inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr) <= 0) {
#endif
        printf("\n Dirección no soportada \n");
        return -1;
    }

    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) {
        printf("\n Error en la conexión \n");
        return -1;
    }

    while (1) {
        valread = recv(sock, buffer, BUFFER_SIZE, 0);
        if (valread > 0) {
            buffer[valread] = '\0';
            printf("%s", buffer);
        }

        if (strncmp(buffer, "Es un empate!", 13) == 0 || strncmp(buffer, "Jugador", 7) == 0) {
            break;
        }

        if (buffer[0] == 'T' && buffer[1] == 'u') {
            fgets(buffer, BUFFER_SIZE, stdin);
            send(sock, buffer, strlen(buffer), 0);
        }
    }

#ifdef _WIN32
    closesocket(sock);
    WSACleanup();
#else
    close(sock);
#endif

    return 0;
}


