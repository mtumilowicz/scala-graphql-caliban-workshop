# scala-graphql-caliban-workshop

* references
    * [GraphQL - when REST API is not enough - lessons learned - Marcin Stachniuk](https://www.youtube.com/watch?v=vMGet_9y40g)
    * [GraphQL as an alternative approach to REST with Luis Weir](https://www.youtube.com/watch?v=hJOOdCPlXbU)
    * [Moving beyond REST: GraphQL and Java - Pratik Patel](https://www.youtube.com/watch?v=LVhzB2kFEAE)
    * [2019 - Marcin Stachniuk - GraphQL - gdy api RESTowe to za mało](https://www.youtube.com/watch?v=lJiOay1fk_g)
    * [GraphQL in Java World, let's go for a dive - Vladimir Dejanović](https://www.youtube.com/watch?v=5_uSpiXCeMI)
    * [Introducing and Scaling a GraphQL BFF](https://www.youtube.com/watch?v=YnZr-qeiGO0)
    * [Migrate your APIs to GraphQL: how? and why! by Guillaume Scheibel](https://www.youtube.com/watch?v=IkPMpzQ-TRI)
    * [Exploring GraphQL-Braid: Leaving RESTish world and building a distributed GraphQL system](https://www.youtube.com/watch?v=iNtxkbzopDg)
    * [Your GraphQL field guide by Bojan Tomić](https://www.youtube.com/watch?v=ROwICdehlb0)
    * [4Developers Katowice: GraphQL - when REST API is not enough (...) cz. II, Marcin Stachniuk](https://www.youtube.com/watch?v=95I3e2Zy09Y)
    * [Zymposium - Caliban](https://www.youtube.com/watch?v=mzqsXklbmfM)
    * [Functional Scala - Caliban: Designing a Functional GraphQL Library by Pierre Ricadat](https://www.youtube.com/watch?v=OC8PbviYUlQ)
    * [Caliban: Functional GraphQL Library for Scala by Pierre Ricadat](https://www.youtube.com/watch?v=oLvdhVNIC3k)
    * [SF Scala–A Tour of Caliban: FP GraphQL in Scala & Understanding Scala's Type System](https://www.youtube.com/watch?v=lgxUKsOH65k&list=WL)

* graphQl is a new API standard that provides a more efficient, powerful and flexible alternative
to REST
* rest vs grapql
    * multiple endpoints -> a single enpoint (/graphql)
    * same set of fields -> fields you ask for
* graphQl schema tells you what fields you can request from the API
* you write a graphQl query describing the data fields you want
* BFF - backend for frontend API
* different clients need  different sets of data
* shared API becomes a bottleneck when rolling out new features
* suppose we have different client with different needs (different types of data)
    * Web, Iphone, Android, Tv
* Graph Query Language
* main concepts
    * one endpoint for all operations
    * always define in request what you expect in response (what you need)
    * three types of operations: queries, mutations, subscriptions
        * subscription - request and stream of responses
    * defined by schema
* example
    * request
        ```
        {
            customer(id: "2") {
                id
                # this description will be showed in GraphQl 'swagger'
                name @include(if:false) // conditional logic
                aliasForEmail:email // fields could be aliased
            }
        }
        ```
    * response
        ```
        {
            "data": {
                "customer": {
                    "id": "2",
                    "name": "name",
                    "aliasForEmail": "a@b.com"
                }
            }
        }
        ```
    * types
        ```
        type Customer { // should be java class with same fields
            // fields with ! are required
            id: ID!
            name: String!
            email: String!
        }
        ```
        ```
        type Query { // should be java class with mapping methods - customer(id) returns service.findById(id)
            customer(id: String!): Customer!
        }
        ```
        ```
        type Status {
            NEW, CANCELED, DONE
        }
        ```
* even if error you get 200 OK
    * dlaczego? bo graphql nie jest ściśle związany z http
* mutation for updates
    ```
    mutation {
        createCustomer(input: {
            name: "X"
            email: "Y"
            clientMutationId: "1"
        }) {
            // what to expect in response
            customer {
                id
            }
        }
    }
    ```
    ```
    input CreateCustomerInput {
        ...
    }

    type CreateCustomerPayload {
        ...
    }

    type Mutation {
        createCustomer(input: CreateCustomerInput): CreateCustomerPayload!
    }
    ```
* pagination
    * before, after, offset, limit
* filtering
    ```
  filter { or: [{ ... }] }, name: "Bob }
  ```
* sorting
    * orderBy: ASC, DESC
    * sort: NEWEST, IMPORTANCE
* n+1 problem
    ```
    {
        customers { // 1 call
            id
            name
            orders { // n calls
                id
                status
            }
        }
    }
    ```
    * solution: add async BatchLoader (java dataloader, scala ZQuery)
* something like swagger: graphql/graphiql
* GraphQL Pros
    * nice alternative to REST
    * it can be used together with REST
    * get exactly what you want get
    * good for API with different clients
    * easy testing
    * nice tooling
* GraphQL cons
    * hard to return simple Map
    * performance overhead
    * a lot of similar code to write
    * caching
        * network - unsuitable as there is a common URL for all ops
    * authorization / authentication
        * graphql zwraca też częściowo poprawne dane, np pytasz o imię customera i jego zamówienia
        do zamówienień nie masz uprawnień, więc dostaniesz imię i komunikat, że do zamówień nie ma uprawnień
            * sam decydujesz jak robisz autentykację
    * no operation idempotency
* 5 key characteristics
    1. hierarchical
        * queries as hierarchies of data definitions, shaped
        just how data is expected to be returned
    1. view-centric
        * by design built to satisfy frontend application requirements
    1. strongly-typed
        * a grapql servers defines a specific type system
        * queries are executed within this context
    1. introspective
        * the type system itself is queryable
        * tools are built around this capability
        * IntrospectionQuery - dokumentacja coś w stylu swaggera, openApi
    1. version-free
        * grapql takes a strong option on avoiding versioning by providing
        the tools for the continuous evolution
* fragment
    ```
    query MyQuery ($name1: String) {
        country1: getCountries(name: $name1, first: 1) { // country1 alias, first - pagination
            ...fields
        }
    }

    fragment fields on Country {
        id
        name
        ... other fields
    }
    ```
    * be declarative - it is deliberate that there is no wildcard (*) to get everything
* best practices are clear: versioning should be avoided and tools are provided
for continuous evolution
* rest has a problem of overfetching (to many fields) and underfetching
(often 2 or more calls to get data you need)
* graphQl is specification
* GraphQL consists of 2 subjects
    * Schema Definition Language
        * Schema is mandatory
        * client can cache specification of the request and comply with it
        * server validates request according to schema - if invalid - inform client
    * Query Language
* scalar
    * fields don't have any sub-fields
    * predefined: ID, Boolean, Int, String
    * we could define: `scalar Date`
        * backend have to recognize it, so `scalar Datexxx` will not be recognized by backend
* Query implements GraphQlQueryResolver
* TalkResolver implements GraphQLResolver<Tall> { public List<Speaker> speakers(Talk talk) { ... } }
* if http query use POST method
    * query works with GET and POST
* union
    * union SearchResult = Human | Droid | Starship
    * query
        ```
      query {
        all {
            _typename
            ... on Speaker {
                // and fields when Speaker
            }
            ... on Talk {
                // and fields when Talk
            }
        }
      }
      ```
* interface
    * interface is an abstract type that includes a certain set of fields that a type must include to implement
    the interface
    * we could also query for appropriate implementations like with union (... on X)
* Mutation implements GraphQLMutationResolver { ... }
* Subscription implements GraphQLSubscriptionResolver { ... }
* authentication / authorisation
    ```
    MyGraphQLCintext extends GraphQLContext {
        ...
    }

    public <method> (<args>, DataFetchingEnvironment env) {
        MyGraphQLContext context = env.getContext();
    }
    ```
* https://github.com/ExpediaGroup/graphql-kotlin
* https://medium.com/@rahul86s/graphql-microservices-example-using-spring-and-braid-27134e13c51
* cons
    * malicious queries
        * arbitrary complex query
            * solution: analyze AST complexity - @GraphQLComplexity
        * arbitrary size of the result
            * solution: limit depth - instrumentation(new MaxQueryDepthInstrumentation(10))
        * solution: limit the execution time
        * stream the results
    * no streaming
    * n+1 data loader
    * caching is no longer simple
        * granular (per field) - @cacheControl
* authorization
    * who can fetch specific fields - @auth(role: "Manager")
    * who can see specific fields - GraphFieldVisibility

# caliban
* queries vs mutations
  * queries - read only
  * mutations - add, edit, delete (commands)
    * typically, the transport protocol is http
  * queries will be done in parallel, mutations sequentially
* subscriptions - typically the transport is websocket
* val api = graphQL(resolver)
  * println(api.render) // prints schema
* server exposes a typed schema
* vs sangria, sangria is
  * lots of boilerplate (macros to the rescue)
  * future based (effects are better)
  * schema and resolved tied together
* caliban is
  * minimize boilerplate
  * purely functional (strongly typed, explicit errors)
  * user friendly
  * schema / resolver separation
* plan of action
  * query -> parsing -> validation (vs schema) -> execution -> result
* schema deriving
  * case class Pug { name: String, nicknames: List[String], pictureUrl: Option[String] }
    * type: Pug { name: String, nicknames: [String!]!, pictureUrl: String }
    * optionality -> option
  * enums
    * sealed trait Color
      case object FAWN extends Color
      * type: enum Color { FAWN }
  * arguments
    * case class PugName(name: String)
      case class Queries(pug: PugName => Option[Pug])
    * type: Queries { pug(name: String!): Pug }
* resolver
  case class Queries(pug: PugName => Task[Pug])
  val resolver = Queries( args => PugService.findPugs(args.name), ... )
* trait Schema[R, T]
  * R for enviroment
* mangolia - used for create schema for traits and case classes
* execution
  1. schema + resolver => execution plan (tree of steps)
     * at the root bunch of fields, and for each fields the next steps
  2. filter plan to include only requested fields
     * cut the branches that we don't need
  3. reduce plan to pure values and effects
     * an object step that has only pure values leaves -> it could be also pure value
  4. run effects
    * n+1 problems
      * { orders { id customer { name } }
        * fetch order (1 query)
        * fetch customer (n query)
      * ZQuery to the rescue
        * parallelize queries
        * cache identical queries
        * batch queries if batching function provided
* provide a schema for custom types
  * implicit val nesSchema: Schema[NonEmptyString] = Schema.stringSchema.contramap(_.value)
* http4s module
  * val route: HttpRoutes[RIO[R, *]] = Http4sAdapter.makeRestService(interpreter)
* builtin wrappers
    ```
  val api = graphQL(...) @@
    maxDdepth(30) @@
    maxFields(200) @@
    timeout(10 seconds) @@
    printSlowQueries(1 second)
  ```
* caliban client