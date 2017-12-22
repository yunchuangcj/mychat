#include<stdio.h>
#include<string.h>
#include"parson.h"
void serialization_example();
int main()
{	
	char jsonstring[500];
	printf("Hello World\n");
	serialization_example(jsonstring);
	printf("The result is:\n%s\n\n",jsonstring);
	printf("Parse Json\n");
	JSON_Value *root_value;
	JSON_Array *arrays;
	JSON_Object *object;
	root_value=json_parse_string(jsonstring);
	object=json_value_get_object(root_value);
	printf("The name is:%s\n",json_object_get_string(object,"name"));	
	printf("The age is:%2.0f\n",json_object_get_number(object,"age"));
	printf("The address is:%s\n",json_object_dotget_string(object,"address.city"));
	arrays=json_object_dotget_array(object,"contact.emails");
	int i;
	for(i=0;i<json_array_get_count(arrays);i++)
	{
		JSON_Object *email;
		email=json_array_get_string(arrays,i);
		printf("The Email is:%s\n",(char*)email);
	}
	return 0;	

}
void serialization_example(char jsonstring[])
{
	JSON_Value *root_value=json_value_init_object();
	JSON_Object *root_object=json_value_get_object(root_value);
	char*serialized_string=NULL;
	json_object_set_string(root_object,"name","Jhon Smith");
	json_object_set_number(root_object,"age",25);
	json_object_dotset_string(root_object,"address.city","Tianjin");
	json_object_dotset_value(root_object,"contact.emails",json_parse_string("[\"email@example.com\",\"email2@example.com\"]"));

	serialized_string=json_serialize_to_string(root_value);
	strcpy(jsonstring,serialized_string);
//	printf("%s",jsonstring);
	json_free_serialized_string(serialized_string);
	json_value_free(root_value);
}
