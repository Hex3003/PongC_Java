#include <stdio.h> 
#include <stdlib.h> 
#include <stdbool.h>
#include <unistd.h> 
#include <string.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <netinet/in.h> 
#include <unistd.h>
#include <pthread.h>

#define PORT     2000
#define SCREEN_WIDTH 1200
#define SCREEN_HEIGHT 750

typedef struct ball_s {
    int x, y;
    int w, h;
    int dx, dy;
}ball_t;

typedef struct paddle_s {
    int x, y;
    int w, h;
}paddle_t;

ball_t ball;
paddle_t paddle[2];
bool connectedClient1, connectedClient2;
char message1[1024], message2[1024];
int sockfd; 
struct sockaddr_in servaddr, clientaddr1, clientaddr2; 
int len1;
int len2;

void init_game();
int check_collision(ball_t a, paddle_t b);
void move_ball();

void *handling_first_connection(void *var);
void *handling_second_connection(void *var);

void *input_client_1(void *var);
void *input_client_2(void *var);

void *game_physics(void *var);

int main(int argc, char **argv) {
	pthread_t thread_id[3];
	connectedClient1 = false;
	connectedClient2 = false;

    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket creation failed"); 
        exit(EXIT_FAILURE); 
    } 
        
    memset(&servaddr, 0, sizeof(servaddr)); 
    memset(&clientaddr1, 0, sizeof(clientaddr1));
	memset(&clientaddr2, 0, sizeof(clientaddr2));
        
    servaddr.sin_family    = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY; 
    servaddr.sin_port = htons(PORT); 
        
    if ( bind(sockfd, (const struct sockaddr *)&servaddr,  
            sizeof(servaddr)) < 0 ) 
    { 
        perror("bind failed"); 
        exit(EXIT_FAILURE); 
    } 
    
	char init[1024];
	memset(&init, 0, 1024); 
	len1 = sizeof(clientaddr1);
	len2 = sizeof(clientaddr2);

	pthread_create(&thread_id[0], NULL, &handling_first_connection, NULL);
	pthread_create(&thread_id[1], NULL, &handling_second_connection, NULL);

	pthread_join(thread_id[0], NULL);
	pthread_join(thread_id[1], NULL);

	if(connectedClient1 && connectedClient2) {
		init_game();
		sprintf(init, "START:%d,%d:%d,%d:1", paddle[0].w, paddle[0].h, ball.w, ball.h);
		printf("invio messaggio: %s", init);
		sendto(sockfd, (const char *)init, strlen(init),MSG_CONFIRM, (const struct sockaddr *) &clientaddr1, len1);
		memset(&init, 0, 1024); 
		sprintf(init, "START:%d,%d:%d,%d:2", paddle[0].w, paddle[0].h, ball.w, ball.h);
		sendto(sockfd, (const char *)init, strlen(init),MSG_CONFIRM, (const struct sockaddr *) &clientaddr2, len2);

		char buffer[1024];


		pthread_create(&thread_id[0], NULL, &game_physics, NULL);
		pthread_create(&thread_id[1], NULL, &input_client_1, NULL);
		pthread_create(&thread_id[2], NULL, &input_client_2, NULL);

		pthread_join(thread_id[0], NULL);
		pthread_join(thread_id[1], NULL);
		pthread_join(thread_id[2], NULL);
	}
    return 0;
}
void *handling_first_connection(void *var){
	char buffer[1024];
	int n = recvfrom(sockfd, (char *)buffer, 1024, MSG_WAITALL, (struct sockaddr *) &clientaddr1, &len1);
	if(strcmp(buffer, "READY") == 0) {
		connectedClient1 = true;
	}
}
void *handling_second_connection(void *var) {
	char buffer[1024];
	int n = recvfrom(sockfd, (char *)buffer, 1024, MSG_WAITALL, (struct sockaddr *) &clientaddr2, &len2);
	if(strcmp(buffer, "READY") == 0) {
		connectedClient2 = true;
	}
}

void *input_client_1(void *var) {
	printf("inizio input client 1");
	while (true) {
		printf("input client 1 eseguito");
		memset(&message1, 0, 1024); 
		int n = recvfrom(sockfd, (char *)message1, 1024, MSG_WAITALL, (struct sockaddr *) &clientaddr1, &len1);
		printf("%s ricevuto dal client1", message1);
		paddle[0].y = atoi(message1);
	}
}
void *input_client_2(void *var) {
	printf("inizio input client 1");
	while (true) {
		printf("input client 2 eseguito");
		memset(&message2, 0, 1024); 
		int n = recvfrom(sockfd, (char *)message2, 1024, MSG_WAITALL, (struct sockaddr *) &clientaddr2, &len2);
		printf("%s ricevuto dal client2", message2);
		paddle[1].y = atoi(message2);
		
	}
}
void *game_physics(void *var) {
	char buffer1[1024];
	char buffer2[1024];
	while(true) {
		memset(&buffer1, 0, 1024); 
    	memset(&buffer2, 0, 1024);
		move_ball();
		sprintf(buffer1, "GAME:%d,%d:%d,%d\n", paddle[1].x, paddle[1].y, ball.x, ball.y);
		sendto(sockfd, (const char *)buffer1, strlen(buffer1),0, (const struct sockaddr *) &clientaddr1,len1);

		sprintf(buffer2, "GAME:%d,%d:%d,%d\n", paddle[0].x, paddle[0].y, ball.x, ball.y);
		sendto(sockfd, (const char *)buffer2, strlen(buffer2),0, (const struct sockaddr *) &clientaddr2,len2);
	}
}

void init_game(){
    ball.x = SCREEN_WIDTH / 2;
	ball.y = SCREEN_HEIGHT / 2;
	ball.w = 10;
	ball.h = 10;
	ball.dy = 1;
	ball.dx = 1;
	
	paddle[0].x = 20;
	paddle[0].y = SCREEN_HEIGHT / 2 - 50 ;
	paddle[0].w = 10;
	paddle[0].h = 50;

	paddle[1].x = SCREEN_WIDTH - 20 - 10;
	paddle[1].y = SCREEN_HEIGHT / 2 - 50;
	paddle[1].w = 10;
	paddle[1].h = 50;
}

int check_collision(ball_t a, paddle_t b) {
    int left_a, left_b;
	int right_a, right_b;
	int top_a, top_b;
	int bottom_a, bottom_b;

	left_a = a.x;
	right_a = a.x + a.w;
	top_a = a.y;
	bottom_a = a.y + a.h;

	left_b = b.x;
	right_b = b.x + b.w;
	top_b = b.y;
	bottom_b = b.y + b.h;
	

	if (left_a > right_b) {
		return 0;
	}

	if (right_a < left_b) {
		return 0;
	}

	if (top_a > bottom_b) {
		return 0;
	}

	if (bottom_a < top_b) {
		return 0;
	}

	return 1;
}

void move_ball() {
	ball.x += ball.dx;
	ball.y += ball.dy;
	
	if (ball.x < 0 || ball.x > SCREEN_WIDTH - 10) {
		
		init_game();
	}

	if (ball.y < 0 || ball.y > SCREEN_HEIGHT - 10) {
		
		ball.dy = -ball.dy;
	}

	for (int i = 0; i < 2; i++) {

		if (check_collision(ball, paddle[i]) == 1) {
			
			if (ball.dx < 0) {
					
				ball.dx -= 1;

			} else {
					
				ball.dx += 1;
			}
			
			ball.dx = -ball.dx;
			
			int hit_pos = (paddle[i].y + paddle[i].h) - ball.y;

			if (hit_pos >= 0 && hit_pos < 7) {
				ball.dy = 4;
			}

			if (hit_pos >= 7 && hit_pos < 14) {
				ball.dy = 3;
			}
			
			if (hit_pos >= 14 && hit_pos < 21) {
				ball.dy = 2;
			}

			if (hit_pos >= 21 && hit_pos < 28) {
				ball.dy = 1;
			}

			if (hit_pos >= 28 && hit_pos < 32) {
				ball.dy = 0;
			}

			if (hit_pos >= 32 && hit_pos < 39) {
				ball.dy = -1;
			}

			if (hit_pos >= 39 && hit_pos < 46) {
				ball.dy = -2;
			}

			if (hit_pos >= 46 && hit_pos < 53) {
				ball.dy = -3;
			}

			if (hit_pos >= 53 && hit_pos <= 60) {
				ball.dy = -4;
			}

			if (ball.dx > 0) {

				if (ball.x < 30) {
				
					ball.x = 30;
				}
				
			} else {
				
				if (ball.x > 710) {
				
					ball.x = 710;
				}
			}
		}
	}
}