Real Time Transactions
===============================
## Overview
Built a critical component of core banking enging: real-time balance calculation through [event-sourcing](https://martinfowler.com/eaaDev/EventSourcing.html).

## Schema
The [service.yml](service.yml) is the OpenAPI 3.0 schema to a service created in this project.

## Details
The service accepts two types of transactions:
1) Create User: Used to create user and load money.
2) Loads: Add money to a user (credit)
3) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT returns the updated balance following the transaction. Authorization declines will be saved, even if they do not impact balance calculation.

## Bootstrap instructions
*To run this server locally, do the following:*
1. **Clone the directory to your local machine**.
2. **Run the Spring Boot Application**:
- You can build and run the Spring Boot application using an IDE of your choice.
- You can also Run the application using Maven with the following command in the project directory:

  ```
  mvn spring-boot:run
  ```
3. **Testing with Postman**:
- Install Postman if you haven't already.
- Use Postman to send requests to the endpoints defined in the Spring Boot application in the request format mentioned in the OpenAPI schema.
- Ensure the endpoints are correctly configured and the server responds as expected.

### Additional Notes

- Make sure all the dependencies and configurations are set up correctly before running the server and application.

## Design considerations
### Architecture overview:
The application follows a CQRS (Command Query Responsibility Segregation) and event sourcing architecture.

**Command Side**:
- Handles user commands such as creating a user, crediting user's account and debiting the user's account. Here is where I implemented the REST endpoints for the application
- I implemented the user account creation endpoint even though it is not mentioned in the requirements because as this is an event sourcing base application, for creating an aggregate for a user it makes sense to have a single create user event in the event store followed by credit and debit events.
- Hardcoding the database with random userIds will not work as there won't be any events in the event store for those userIds to understand the current state.

**Query Side** (Not Implemented):
- Ping endpoint is implemented here. Can handle queries to retrieve user information and balance in the account.

### Event Sourcing:
- Unlike traditional RDS, here data is stored in an append-only log. It ensures that all changes to the applications are stored as a sequence of events. This is very important when it comes to real time transactions.
- To achieve event sourcing in this application I have used a popular event store and message delivery system called <b>Axon Framework</b>.
- I chose Axon because it is one of the well documented event sourcing applications for Java, and it also simplifies the operational aspects of event sourcing by providing a centralized platform for managing events, commands, and queries.
- It can also automatically construct your current state by replaying all the events using the unique Aggregate identifier.

### Database:
- I decided to go with the in-memory h2 database because of its simplicity to set up and query using JPA.
- The in-memory nature of H2 database makes it ideal for testing scenarios. Tests can be executed rapidly without the need for external dependencies or setup procedures.
- The database is solely used for the purpose of storing the current state of the application. Hence, an in-memory database makes sense during development stage.

### Testing
- I have used JUnit, Mockito and assertion to build my test cases and test my components.
- I have also performed integration testing using TestRestTemplate for various test cases.

## Deployment considerations
### Snapshot:
- Replaying events to rebuild the current state will become increasingly costly in terms of processing time and resource utilization as the dataset grows.
- Implementing snapshot events reduces the number of events needed to rebuild an aggregate's state by summarizing multiple events into one.
- Instead of loading hundreds of events each time, you only need to load 10-20 events, significantly improving the application's performance.

### Cloud Deployment:

If this application has to be deployed on cloud, I would suggest the below architecture based on my experience with AWS:

- Amazon MSK, a full managed and highly available Apache Kafka Service will be the primary component of our centralized event store,capturing application changes as events.
- We will be having several microservices handling our commands, queries and events. We can use lambda functions that are serverless and highly scalable to run these microservices.
- When the client issues the commands, the command handler microservice which has our command handling code will generate events and stores them in Kafka.
- We then have our event handler microservice which has our event handling code, read the events from Apache kafka and will store the current state in an RDS like Amazon Aurora.
- We also have query handler microservices which reads our data and returns it to the client.
- In case of failures or data inconsistencies, we can replay events to rebuild the application's state. By reprocessing historical events, we can ensure that the application's state remains consistent and up-to-date.

#### Other Options:

- Amazon EventBridge can be used for event sourcing; however, it lacks native support for filtering events based on entity-specific criteria, such as User IDs.
- Additionally, Amazon Kinesis Data Stream or DynamoDB Streams, could be considered in this scenario.

## ASCII art

```
 /$$$$$$$                      /$$  
| $$__  $$                    | $$   
| $$  \ $$  /$$$$$$   /$$$$$$ | $$                                                                            
| $$$$$$$/ /$$__  $$ |____  $$| $$                                                                            
| $$__  $$| $$$$$$$$  /$$$$$$$| $$                                                                            
| $$  \ $$| $$_____/ /$$__  $$| $$               
| $$  | $$|  $$$$$$$|  $$$$$$$| $$                                                                            
|__/  |__/ \_______/ \_______/|__/                                                                            
 /$$$$$$$$ /$$                                                                                                
|__  $$__/|__/                                                                                                
   | $$    /$$ /$$$$$$/$$$$   /$$$$$$                                                                         
   | $$   | $$| $$_  $$_  $$ /$$__  $$                                                                        
   | $$   | $$| $$ \ $$ \ $$| $$$$$$$$                                                                        
   | $$   | $$| $$ | $$ | $$| $$_____/                                                                        
   | $$   | $$| $$ | $$ | $$|  $$$$$$$                                                                        
   |__/   |__/|__/ |__/ |__/ \_______/                                                                        
 /$$$$$$$$                                                           /$$     /$$                        /$$   
|__  $$__/                                                          | $$    |__/                      /$$$$$$ 
   | $$  /$$$$$$  /$$$$$$  /$$$$$$$   /$$$$$$$  /$$$$$$   /$$$$$$$ /$$$$$$   /$$  /$$$$$$  /$$$$$$$  /$$__  $$
   | $$ /$$__  $$|____  $$| $$__  $$ /$$_____/ |____  $$ /$$_____/|_  $$_/  | $$ /$$__  $$| $$__  $$| $$  \__/
   | $$| $$  \__/ /$$$$$$$| $$  \ $$|  $$$$$$   /$$$$$$$| $$        | $$    | $$| $$  \ $$| $$  \ $$|  $$$$$$ 
   | $$| $$      /$$__  $$| $$  | $$ \____  $$ /$$__  $$| $$        | $$ /$$| $$| $$  | $$| $$  | $$ \____  $$
   | $$| $$     |  $$$$$$$| $$  | $$ /$$$$$$$/|  $$$$$$$|  $$$$$$$  |  $$$$/| $$|  $$$$$$/| $$  | $$ /$$  \ $$
   |__/|__/      \_______/|__/  |__/|_______/  \_______/ \_______/   \___/  |__/ \______/ |__/  |__/|  $$$$$$/
                                                                                                     \_  $$_/ 
                                                                                                       \__/  
```