#ifdef _WIN32
    #include <winsock2.h>
    #include <windows.h>
    #pragma comment(lib, "ws2_32.lib")
#else
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <arpa/inet.h>
    #include <pthread.h>
    #include <unistd.h>
    #include <string.h>
    #include <errno.h>
#endif

#include <stdio.h>
#include <stdlib.h>

#define PORT 8080
#define MAX_CLIENTS 100
#define BUFFER_SIZE 1024

typedef struct {
    int socket1;
    int socket2;
    char board[9];
    int turn;
} Game;

void initialize_board(char *board) {
    for (int i = 0; i < 9; i++) {
        board[i] = '1' + i;
    }
}

void print_board(char *board) {
    printf("\n %c | %c | %c\n", board[0], board[1], board[2]);
    printf("---|---|---\n");
    printf(" %c | %c | %c\n", board[3], board[4], board[5]);
    printf("---|---|---\n");
    printf(" %c | %c | %c\n\n", board[6], board[7], board[8]);
}

int check_winner(char *board) {
    int win_conditions[8][3] = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };
    for (int i = 0; i < 8; i++) {
        if (board[win_conditions[i][0]] == board[win_conditions[i][1]] &&
            board[win_conditions[i][1]] == board[win_conditions[i][2]]) {
            return 1;
        }
    }
    return 0;
}

#ifdef _WIN32
DWORD WINAPI game_thread(LPVOID arg) {
#else
void *game_thread(void *arg) {
#endif
    Game *game = (Game *)arg;
    char buffer[BUFFER_SIZE];
    int current_socket;
    char player;

    while (1) {
        current_socket = (game->turn % 2 == 0) ? game->socket1 : game->socket2;
        player = (game->turn % 2 == 0) ? 'X' : 'O';

        snprintf(buffer, sizeof(buffer), "Tu turno (%c). ¿Qué casilla juegas? ", player);
        send(current_socket, buffer, strlen(buffer), 0);

        recv(current_socket, buffer, sizeof(buffer), 0);
        int move = buffer[0] - '1';

        if (move < 0 || move > 8 || game->board[move] == 'X' || game->board[move] == 'O') {
            snprintf(buffer, sizeof(buffer), "Movimiento inválido. Inténtalo de nuevo.\n");
            send(current_socket, buffer, strlen(buffer), 0);
            continue;
        }

        game->board[move] = player;
        game->turn++;

        for (int i = 0; i < 9; i++) {
            buffer[i] = game->board[i];
        }
        buffer[9] = '\0';

        send(game->socket1, buffer, sizeof(buffer), 0);
        send(game->socket2, buffer, sizeof(buffer), 0);

        if (check_winner(game->board)) {
            snprintf(buffer, sizeof(buffer), "Jugador %c gana!\n", player);
            send(game->socket1, buffer, strlen(buffer), 0);
            send(game->socket2, buffer, strlen(buffer), 0);
            break;
        }

        if (game->turn == 9) {
            snprintf(buffer, sizeof(buffer), "Es un empate!\n");
            send(game->socket1, buffer, strlen(buffer), 0);
            send(game->socket2, buffer, strlen(buffer), 0);
            break;
        }
    }

    closesocket(game->socket1);
    closesocket(game->socket2);
    free(game);

#ifdef _WIN32
    return 0;
#else
    pthread_exit(NULL);
#endif
}

int main() {
    int server_fd, new_socket;
    struct sockaddr_in address;
    int opt = 1;
#ifdef _WIN32
    int addrlen = sizeof(address);
#else
    socklen_t addrlen = sizeof(address);
#endif

#ifdef _WIN32
    WSADATA wsa;
    WSAStartup(MAKEWORD(2, 2), &wsa);
    HANDLE thread_id;
#else
    pthread_t thread_id;
#endif

    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt)) < 0) {
        perror("setsockopt");
        closesocket(server_fd);
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("bind failed");
        closesocket(server_fd);
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, MAX_CLIENTS) < 0) {
        perror("listen");
        closesocket(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("Esperando conexiones...\n");

    while (1) {
        Game *game = malloc(sizeof(Game));
        initialize_board(game->board);
        game->turn = 0;

        if ((game->socket1 = accept(server_fd, (struct sockaddr *)&address, &addrlen)) < 0) {
            perror("accept");
            free(game);
            continue;
        }

        if ((game->socket2 = accept(server_fd, (struct sockaddr *)&address, &addrlen)) < 0) {
            perror("accept");
            closesocket(game->socket1);
            free(game);
            continue;
        }

        printf("Nueva partida iniciada\n");

#ifdef _WIN32
        thread_id = CreateThread(NULL, 0, game_thread, (void *)game, 0, NULL);
        if (thread_id == NULL) {
            perror("CreateThread");
            closesocket(game->socket1);
            closesocket(game->socket2);
            free(game);
            continue;
        }
        CloseHandle(thread_id);
#else
        if (pthread_create(&thread_id, NULL, game_thread, (void *)game) != 0) {
            perror("pthread_create");
            closesocket(game->socket1);
            closesocket(game->socket2);
            free(game);
            continue;
        }
        pthread_detach(thread_id);
#endif
    }

#ifdef _WIN32
    WSACleanup();
#endif

    return 0;
}

