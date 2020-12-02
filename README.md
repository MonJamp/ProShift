This is where we will commit code for the backend/server

The server is where we will store all relevant information for our application which includes but is not limited to: user information, all shift related information, manager information, company information, etc. We are actually using Django to manage our server and it provides us with a handy admin panel so we can check to see if all the data is actually saved on there correctly. From the Django Admin panel we can create new users, check recent shift changes and, much more that we may have to work with for the server.  

![Django Admin panel](https://user-images.githubusercontent.com/63976854/100819213-4a442380-3411-11eb-99a6-b69b04aa67a6.png)

This server branch has many different Rest API implementations which function in returning relevent information to a user from the server as well as adding and modifying information that will be saved to the server. This could look like a user using the `get_shift` API call in order to get all the shifts they have been assigned. There are plenty of these APIs that function to ensure an employee and manager will be able to create and edit any information regarding themselves and the shifts for their profession. 
The server will also create login tokens for users and use this as authentication in order to ensure security along with ensuring that with the right tokens only certain users can access specific parts of the application only deemed for managers. 

Right now in order to test out our APIs we are using **Swagger** with provides us with a great User Interface where we can see all the APIs that are implemented. Swagger UI allows us to quickly test out any particular API and check to make sure it functions as intended. Swagger allows us to quickly identify where an API went wrong in order to quickly fix any error. 

Below is an example of using Swagger to check to make sure the shifts of a particular user will be returned by the `get_shifts_debug` API:

![Swagger](https://user-images.githubusercontent.com/63976854/100818708-4cf24900-3410-11eb-8b06-dc22fb8d2997.png)
