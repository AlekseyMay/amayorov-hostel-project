**Documentation**

First of all be attentive while inserting information into requests' fields. As SWAGGER doesn't support dynamically changing documentation of DTO fields for different request (the only way was to use different DTOs for each request). The only option was to use hints which fields to fill and which have to be left unfilled.

At the start of the application the default User with 'username': admin and 'password': admin with 'roles': ADMIN,MANAGER has been created.

**Client details:**

'client_id': my-trusted-client

'client_secret': secret

my JMS default endpoint (may differ): http://localhost:8161/

login: admin

password: admin

JMS implementation: when a new User with role ADMIN is created it sends queue and listener sends informational email to email list, after which a topic is going to be created and all the subscribers

receive console informational message about the email sent to them. To check whether it is working please log in gmail.com:

login: amayorovhostel@gmail.com

password: Hostel111

**H2 database info:**

endpoint: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./src/main/resources/data/hosteldb;AUTO_SERVER=true

user name: sa

password:

**Caffeine cache implementation:**

All the requests are using cache system, setting/cleaning/updating. Cache cleans automatically after 60 min of inactivity.

**Akka actors classic simple implementation:**

Every 120 seconds actor shows in console information about all registered users and about quarters available for the next 7 days.

**Docker compose commands:**

1. mvn clean || just once when changes to the code are made
2. mvn install || just once when changes to the code are made
3. docker-compose build
4. docker-compose up OR docker-compose up -d (to run in hidden mode, only in Desktop Docker log will be shown)
5. docker-compose stop (to stop the working containers)
6. docker-compose down (to stop and delete all the containers)
7. docker-compose start (to start exixsting containers) OR docker-compose up

It is also possible to use Docker Desktop software to start or stop containers.

**Way of proceeding (to test the app):**

1. Check Registration. It should work for everybody.
2. At start of the application it creates two roles: ADMIN, MANAGER. And these roles are already set up for privileges, just for test purposes you can create additional roles, but in order to achieve functionality, yo have to change the code.
3. Log in with default admin User and create a new Admin user. Log out and log in with newly created account.
4. Create Categories, at least several from the enum List.
5. Create several Quarters.
6. Imagine that any guests need particular quarters for particular time, so check the quarters for availability with /quarters/validation, of course at the beginning, when no guests with presence added, it will response the list of all the quarters, meaning that all are free.
7. Create several Guests.
8. You can try adding additional presence to guests.
9. You can check the quarters again on those dates on which you created presence for guests, it will response that there are no free quarters with demanded categories and amount.
10. You can try now any other remaining requests. As well as log in as MANAGER to check if the privileges works in accordance with technical assignment.
