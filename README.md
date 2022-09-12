# BFF with Akka HTTP
This is a demo project for DevJam 2022 to evaluate the Akka HTTP technology. 

## The basic idea of this project
<img width="307" alt="Screen Shot 2022-09-12 at 12 57 08 PM" src="https://user-images.githubusercontent.com/9009178/189712726-3e1d8ff3-533f-4654-ab0a-e07d9266e759.png">

## Tasks:

- Implement a simple REST API server with spring boot
  - Support basic CURD operation on sample data model. For example, User and Address 
- Create a web server with Akka HTTP
  - Accept HTTP requests with AKK HTTP server
  - Delegate the HTTP requests to REST API server and send back the result to client
- Basic performance test:
  - How many request can handle per seconds
- Optional
  - Compare to another BFF technology: Spring boot web with Apache HTTP client or Spring web client.
  - Can support GraphQl?

## References

- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html)
- [Backends For Frontends](https://samnewman.io/patterns/architectural/bff/)