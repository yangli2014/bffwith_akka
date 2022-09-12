This is a simple REST API server with spring boot. 
It supports basic CRUD operation on sample data model. 

For example, User and Roles for now.

It suppose to have two implementation - simple "in memory" one
and the second one hooked up to the "real" API server, which 
we plan to be our dev backend portal one.

## Project (rest server api) start

```console
./mvnw clean spring-boot:run
```

Then open in your local browser: http://localhost:8080/webjars/swagger-ui/index.html