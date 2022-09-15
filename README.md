# BFF with Akka HTTP
This is a demo project for DevJam 2022 to evaluate the Akka HTTP technology. 

## The basic idea of this project
![Screen Shot 2022-09-15 at 4 26 11 PM](https://user-images.githubusercontent.com/9009178/190502853-3bc928cb-95c6-42b1-b721-eebb495059d8.png)


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
