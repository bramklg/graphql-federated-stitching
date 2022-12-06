# GraphQL stitching with federated services using Stitching Directives

The service set-up of this demo is based on the [Stitching directives SDL example](https://github.com/gmac/schema-stitching-handbook/tree/main/stitching-directives-sdl) from the [Schema Stitching Handbook](https://github.com/gmac/schema-stitching-handbook) that is in turn based the [official demonstration repository](https://github.com/apollographql/federation-demo) for [Apollo Federation](https://www.apollographql.com/docs/federation/).

It demonstrates using the GraphQL [Stitching Directives](https://the-guild.dev/graphql/tools/docs/schema-stitching/stitch-directives-sdl) in [Spring Boot 3](https://spring.io/) GraphQL services that are reachable through an API Gateway constructed using [GraphQL Yoga](https://the-guild.dev/graphql/yoga-server).

Aside from demonstrating the Stitching Directives it also demonstrates:
* Subscriptions to Spring Boot 3 backends
* File uploads to Spring Boot 3 backends
* Hot schema reloading (based on [this example from the stitching handbook](https://github.com/gmac/schema-stitching-handbook/tree/main/hot-schema-reloading))

The Spring GraphQL implementation doesn't support file uploads yet, see [this PR](https://github.com/spring-projects/spring-graphql/pull/430)). The [GraphQL Spring Boot kickstarter project](https://github.com/graphql-java-kickstart/graphql-spring-boot) does support file uploads. Thats why the `userservice` doesn't use the Spring GraphQL implementation but the kickstarter project.

## Setup

> I did not put much effort in a clean project structure or build environment. Building the services from the command line is also possible if you have the
correct tooling installed.

To build and launch the services and gateway, execute the following command(s):
```shell
docker-compose build # Build all projects
docker-compose up # Launch all containers
```

After that, the GraphiQL interface of all the services are reachable under the following urls:

* Gateway: http://localhost:9000/graphql
* Inventory: http://localhost:9001/graphql
* Product: http://localhost:9002/graphiql
* Review: http://localhost:9003/graphiql
* User: http://localhost:9004/graphiql

## GraphQL Queries to test

> Please note: all services use an embedded database, the data is gone after a restart. All services provide some initial data on start-up

```graphql
# Get all products from product, inventory and review info (this query fetches data from all of the services)
query products {
  products {
    id
    inStock
    price
    description
    shippingEstimate
    reviews {
      body
      author {
        name
        username
      }
    }
  }
}
```

```graphql
# Subscribe to new reviews. This also fetches data from 2 services (review service and user service)
subscription newReviews {
    newReviews{
        id
        body
        author {
            name
        }
    }
}

# Use this mutation to create a new review after subscribing
mutation createReview {
    createReview(review: {
        body: "This is a test"
        product: 1
        author: 1
    }) {
        id
    }
}
```

### File upload to user service
The following `curl` command can be used to test file uploads to the user service. Since Yoga does not have file upload support in the gateway (yet) it won't work through the gateway but only when directly executing it on the `userservice`.

Note: the user service currently doesn't do anything with the uploaded file
```shell
curl localhost:9004/graphql \
  -F operations='{ "query": "mutation ($user: ID!, $file: Upload!) { addAvatar(user: $user, file: $file) }", "variables": { "file": null, "user": "1" } }' \
  -F map='{ "0": ["variables.file"] }' \
  -F 0=@graphql.png

```
