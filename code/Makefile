server:server.o parson.o
	gcc server.o parson.o -o server
server.o:server.c parson.h
	gcc -c server.c -o server.o
parson.o:parson.c parson.h
	gcc -c parson.c -o parson.o
clean:
	rm -rf*.o json
