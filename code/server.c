#include<stdio.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<stdlib.h>
#include<string.h>
#include<sys/epoll.h>
#include"parson.h"
#define BUFFER_SIZE 4096
#define MAX_EVENTS 50
struct Client
{
	int sockfd;
	struct sockaddr_in remote_addr;
	int islive;
	char username[20];
		
};
struct Clientnode
{
	struct Client client;
	struct Clientnode *next;
};
struct Message
{
	int type;
	char content[2048];
	char fromid[20];
	char toid[20];
	char time[30];
};
int clientcount=0;
struct Clientnode * clientsHeader;
struct Clientnode * clientsEnd;

void DealMessage(int sockfd,char buf[],int len);
int main()
{
	printf("Hello World!\n");
	struct Clientnode*p,*q;	
	int server_sockfd;
	int client_sockfd;
	int len;
	struct sockaddr_in my_addr;
	struct sockaddr_in remote_addr;
	int sin_size;
	char buf[BUFFER_SIZE];
	memset(&my_addr,0,sizeof(my_addr));
	my_addr.sin_family=AF_INET;
	my_addr.sin_addr.s_addr=INADDR_ANY;
	my_addr.sin_port=htons(8008);
	if((server_sockfd=socket(PF_INET,SOCK_STREAM,0))<0)
	{
		perror("socket");
		return -1;
	}
	p=(struct Clientnode*)malloc(sizeof(struct Clientnode));
	p->client.sockfd=server_sockfd;
	p->client.remote_addr=my_addr;
	p->next=NULL;
	clientsHeader=p;
	clientsEnd=p;

	int opt=1;
	if(setsockopt(server_sockfd,SOL_SOCKET,SO_REUSEADDR,(char*)&opt,sizeof(opt))<0)
	{
		perror("set opt error");
		return -1;
	}
	if(bind(server_sockfd,(struct sockaddr*)&my_addr,sizeof(struct sockaddr))<0)
	{
		perror("bind");
		return -1;
	}
	listen(server_sockfd,MAX_EVENTS);
	sin_size=sizeof(struct sockaddr_in);
	int epoll_fd;
	epoll_fd=epoll_create(MAX_EVENTS);
	if(epoll_fd==-1)
	{
		perror("epoll_create failed");
		exit(EXIT_FAILURE);
	}
	struct epoll_event ev;
	struct epoll_event events[MAX_EVENTS];
	ev.events=EPOLLIN;
	ev.data.fd=server_sockfd;

	if(epoll_ctl(epoll_fd,EPOLL_CTL_ADD,server_sockfd,&ev)==-1)
	{
		perror("epoll_ctl:server_sockfd register failed");
		exit(EXIT_FAILURE);
	}
	int nfds;
	while(1)
	{
//		printf("epoll_wait events\n");
		nfds=epoll_wait(epoll_fd,events,MAX_EVENTS,-1);
		if(nfds==-1)
		{
			perror("start epoll_wait failed");
			exit(EXIT_FAILURE);
		}
		int i;
		for(i=0;i<nfds;i++)
		{
			if(events[i].data.fd==server_sockfd)
			{
				if((client_sockfd=accept(server_sockfd,(struct sockaddr*)&remote_addr,&sin_size))<0)
				{
					perror("accept client_sockfd failed");
//					exit(EXIT_FAILURE);
				}
				ev.events=EPOLLIN;
				ev.data.fd=client_sockfd;
				if(epoll_ctl(epoll_fd,EPOLL_CTL_ADD,client_sockfd,&ev)==-1)
				{
					perror("epoll_ctl:client_sockfd register failed");
//					exit(EXIT_FAILURE);
				}
				p=(struct Clientnode*)malloc(sizeof(struct Clientnode));
				p->client.sockfd=client_sockfd;
				p->client.remote_addr=remote_addr;
				p->client.islive=1;
				p->next=NULL;
				clientsEnd->next=p;
				clientsEnd=p;
				for(p=clientsHeader;p!=NULL;p=p->next)
				{
					printf("Client sockfd:%d (%s)\n",p->client.sockfd,inet_ntoa(p->client.remote_addr.sin_addr));
				}	
				printf("accept client %s\n",inet_ntoa(remote_addr.sin_addr));

			}
			else
			{
				//printf("recv a message\n");
				int sockfd;
				sockfd=events[i].data.fd;	
				memset(buf,'\0',BUFFER_SIZE);
				len=recv(sockfd,buf,BUFFER_SIZE,0);				
				if(len<0)
				{
					perror("receive from client failed");
//					exit(EXIT_FAILURE);
				}
				if(strcmp(buf,"exit")==0)
				{
					printf("A client disconnect!\n");

					if(epoll_ctl(epoll_fd,EPOLL_CTL_DEL,sockfd,&ev)==-1)
					{
						perror("epoll_ctl:client_sockfd del failed");
//						exit(EXIT_FAILURE);
					}
					for(p=clientsHeader;p->next!=NULL&&p->next->client.sockfd!=sockfd;p=p->next)
					{}
					if(p->next!=NULL)
					{	
						q=p->next;		
						if(q->next==NULL)
						{
							clientsEnd=p;
						}
						p->next=p->next->next;
						free(q);
					}
					continue;
				}
				else
				{
					printf("receive from client(%d):%s\n",sockfd,buf);			
					DealMessage(sockfd,buf,len);
					//if(send(sockfd,"I have received your message.",30,0)<0)
					//{
					//	perror("send fail");
					//	exit(EXIT_FAILURE);
					//}
				}
				
			}
		}
	}
	printf("Server stop!\n");
	return 0;
}
void DealMessage(int sockfd,char buf[],int len)
{
	struct	Message msg;
	struct Clientnode*p,*q;
	JSON_Value *root_value;
	JSON_Object *object;
	root_value=json_parse_string(buf);
	object=json_value_get_object(root_value);
	char *stype;
	stype=(char*)json_object_get_string(object,"type");
	printf("%s\n",stype);
	int type=stype[0]-'0';
	if(type==1)
	{
		char*content;
		content=(char*)json_object_get_string(object,"content");
		printf("Content is:%s\n",content);
		for(p=clientsHeader->next;p!=NULL&&p->client.sockfd!=sockfd;p=p->next)
		{}
		if(p!=NULL)
		{
			strcpy(p->client.username,content);
			char clientcontent[1024];
			int i,t=0;
			for(q=clientsHeader->next;q!=NULL;q=q->next)
			{
				char*username=q->client.username;
				if(strcmp(username,"")!=0)
				{
					for(i=0;username[i]!='\0';i++)
					{
						clientcontent[t++]=username[i];
					}
					clientcontent[t++]='$';
				}
			}
			clientcontent[t++]='\0';
			printf("send to clients:%s\n",clientcontent);
			JSON_Value*root_value=json_value_init_object();
			JSON_Object*root_object=json_value_get_object(root_value);
			char*msgstring=NULL;
			json_object_set_string(root_object,"type","2");
			json_object_set_string(root_object,"content",clientcontent);
			msgstring=json_serialize_to_string(root_value);
			printf("msgstring is:%s\n",msgstring);
			for(i=0;msgstring[i]!='\0';i++)
			{}
			for(q=clientsHeader->next;q!=NULL;q=q->next)
			{
				if((send(q->client.sockfd,msgstring,i,0))<0)
				{
					perror("send error");
				}
			}

			
		}

	}
	else if(type==2)	
	{
		char content[1024];
		int t=0,i;
		for(p=clientsHeader->next;p!=NULL;p=p->next)
		{
			if(p->client.username!=NULL&&strcmp(p->client.username,""))
			{
				char*username=p->client.username;
				if(strcmp(username,"")!=0)
				{
					for(i=0;username[i]!='\0';i++)
					{
						content[t++]=username[i];
					}
					content[t++]='$';
				}
			}			
		}	
		content[t++]='\0';
		printf("%s\n",content);		
		JSON_Value* root_value=json_value_init_object();
		JSON_Object*root_object=json_value_get_object(root_value);
		char* msgstring=NULL;
		json_object_set_string(root_object,"type","2");
		json_object_set_string(root_object,"content",content);
		msgstring=json_serialize_to_string(root_value);
		printf("%s\n",msgstring);
		for(i=0;msgstring[i]!='\0';i++)
		{}
				
		if((send(sockfd,msgstring,i,0))<0)
		{
			perror("send error");
		}

	}
	else if(type==3)
	{
		char*toid;
		toid=(char*)json_object_get_string(object,"toid");
		printf("Message toid:%s\n",toid);
		for(p=clientsHeader->next;p!=NULL;p=p->next)
		{
			if(strcmp(p->client.username,toid)==0)
			{
				break;
			}
		}	
		if(p!=NULL)
		{
			if((send(p->client.sockfd,buf,len,0))<0)
			{
				perror("send error3");
			}		
		}

	}
	

}







































