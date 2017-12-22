#include<stdio.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<string.h>
#include<stdlib.h>
#include<unistd.h>
#include<pthread.h>

#define BUFFER_SIZE 50

int client_sockfd;
struct sockaddr_in remote_addr;
int islive=0;
void thread_live()
{
	int ret;
	while(1)
	{
		ret=send(client_sockfd,"1",2,0);
		printf("ret:%d\n",ret);
		if(ret==-1)		
		{
			islive=0;
			printf("Server disconnect!\n");			
			while(1)
			{
				if((client_sockfd=socket(PF_INET,SOCK_STREAM,0))<0)
				{
					perror("client socket create failed");					
				}
				if(connect(client_sockfd,(struct sockaddr*)&remote_addr,sizeof(struct sockaddr))<0)
				{
					perror("connect to server failed");
					sleep(5);
				}
				else
				{
					islive=1;
					printf("connect success\n");
					break;
				}
			}
		}
		else
		{
			sleep(5);
		}
	}
}
int main(int argc,char *argv[])
{
//	int client_sockfd;
	int len;
//	struct sockaddr_in remote_addr;
	char buf[BUFFER_SIZE];
	pthread_t id;
	memset(&remote_addr,0,sizeof(remote_addr));
	remote_addr.sin_family=AF_INET;
	remote_addr.sin_addr.s_addr=inet_addr("127.0.0.1");
	remote_addr.sin_port=htons(8008);

	if((client_sockfd=socket(PF_INET,SOCK_STREAM,0))<0)
	{
		perror("client socket creation failed");
		exit(EXIT_FAILURE);
	}
	if(connect(client_sockfd,(struct sockaddr*)&remote_addr,sizeof(struct sockaddr))<0)
	{
		perror("connect to server failed");
		exit(EXIT_FAILURE);
	}
	islive=1;
	//create keep alive thread
	int ret;
	ret=pthread_create(&id,NULL,(void*)thread_live,NULL);
	if(ret!=0)
	{
		printf("Create pthread error!\n");
		exit(1);		
	}	

	while(1)
	{
		printf("Please input the message:\n");
		scanf("%s",buf);
		if(strcmp(buf,"exit")==0)
		{
			send(client_sockfd,"exit",5,0);
			break;
		}
		if(islive==1)
		{
			send(client_sockfd,buf,BUFFER_SIZE,0);
			len=recv(client_sockfd,buf,BUFFER_SIZE,0);
			printf("receive from server:%s\n",buf);
			if(len<0)
			{
				perror("receive from server failed");
				exit(EXIT_FAILURE);
			}
		}
		else
		{
			printf("Server is disConnect!");
		}
	}
	if(close(client_sockfd)<0)
	{
		perror("close failed");
		exit(EXIT_FAILURE);
	}
	printf("Client exit\n");	
	return 0;
}

