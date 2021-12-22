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
    * [Spring Boot GraphQL Tutorial - Full Course](https://www.youtube.com/playlist?list=PLiwhu8iLxKwL1TU0RMM6z7TtkyW-3-5Wi)
    * https://ghostdogpr.github.io/caliban/faq/
    * https://ghostdogpr.github.io/caliban/docs/
    * https://www.manning.com/books/graphql-in-action
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-1-8ceb6099c3c2
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-2-c7762110c0f9
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-3-8962a02d5d64
    * https://www.peerislands.io/how-to-use-graphql-to-build-bffs/
    * https://graphql.org/

## preface
* goals of this workshops
    * introduction into GraphQL
        * motivation
        * schema
        * query, mutation, subscription
        * data loaders
* workshop plan
    * task1: implement query for returning all customers (with a size limit)
    * task2: implement subscription for deleted customers
    * task3: implement getting orders in batch

## introduction
* BFF - backend for frontend API
    * different clients need  different sets of data
        * Web, Iphone, Android, Tv
    * instead of the frontend application aggregating data by calling and processing information from multiple
    data sources and APIs, we create a BFF layer
        * This layer does the following:
            * Receive request from the client application
            * Call multiple backend services as required to get the required data
            * Format the response with just the information required by the client
            * Respond with the formatted data to the client application
    * pros
        * Simplify the frontend logic
        * Avoid over-fetching or under-fetching
        * Reduce the number of network calls that the client has to make, to render a page
* the best way to represent data in the real world is with a graph-like data structure
    * data model is usually a graph of objects with many relations between them
    * why think of data in terms of resources (in URLs) or tables?
* GraphQL = Graph Query Language
* graphQl is a new API standard that provides a more efficient, powerful and flexible alternative
to REST
* GraphQL is a strong type system that is used to describe data and organize APIs
* A GraphQL service can be written in any programming language, and it can be conceptually split into two major parts, structure and behavior:
    * structure = strongly typed schema
        * schema is basically a graph of fields that have types
            * graph represents all the possible data objects that can be read (or updated) through the service
        * client uses the schema to know what questions they can ask
    * behavior = resolver functions
        * each field in a GraphQL schema is backed by a resolver function
        * resolver function defines what data to fetch for its field
        * resolver function represents the instructions on how and where to access raw data
* GraphQL takes the custom endpoint idea to an extreme
    * the whole server a single smart endpoint
    * solution to the multiple round-trip problem
        * API clients ask about multiple resources, and the API server knows how to answer questions
        about a single resource
        * client has to communicate with the server multiple times to gather all data
* vs REST
    * don't need any documentation like swagger
    * REST has a problem of overfetching (to many fields) and underfetching
    (many calls to get data you need)
    * REST: each endpoint represents a resource
        * when a client needs data about multiple resources, it has to perform multiple network requests
    * In a pure REST API (not a customized one): a client cannot specify which fields to select
    * REST APIs big problem is versioning
        * usually means new endpoints
        * versioning should be avoided and tools are provided for continuous evolution
        * basically, you can add new fields and types without removing the old ones because you have
        a graph and can flexibly grow it by adding more nodes
            * Clients can continue to use older features, and
              they can also incrementally update their code to use new features.
            * especially important for mobile clients because you cannot control the ver-
                          sion of the API they are using
            * GraphQL offers a way to deprecate (and hide)
    * REST APIs eventually turn into a mix of regular REST endpoints plus custom ad hoc
      endpoints crafted for performance reasons.                          older nodes so that consumers of the schema only see the new ones.
* pros
    * decouples clients from servers and allows both of them to evolve and scale independently
    * efficiency
    * instead of a client going directly to multiple data services client communicate with the GraphQL service
        * GraphQL service communicates with the different services
* cons
    * malicious queries
        * complex queries
            * solution: analyze AST complexity
        * large result size
            * solution: limit depth - instrumentation(new MaxQueryDepthInstrumentation(10))
        * long execution time
            * solution: limit the execution time
            * solution: stream the results
    * n+1 problem
        * solution: data loader
    * caching is no longer simple
        * network layer - unsuitable as there is a common URL for all ops
        * solution: granularity (per field)
    * hard to return simple Map
* 5 key characteristics
    1. hierarchical: queries as hierarchies of data definitions
    1. view-centric: by design built to satisfy frontend application requirements
    1. strongly-typed: typed context (schema) + queries are executed within this context
    1. introspective: type system (schema) itself is queryable
    1. version-free: tools for the continuous evolution

## schema
* something like swagger: graphql/graphiql
    * https://api.spacex.land/graphql/
* example
    ```
    type Starship {
      id: ID!
      name: String! // non nullable
      appearsIn: [Episode!]! // list of objects
      length(unit: LengthUnit = METER): Float // argument
    }
    ```
* good practices
    * usually make the types of fields non-null
    * however, make all root fields nullable
        * in this case, nullability means that something went wrong but we’re allowing it to show other fields
* scalar
    * fields don't have any sub-fields
    * predefined: ID, Boolean, Int, String

## operations
* three types of operations
    * queries (READ operations)
    * mutations (WRITE-then-READ operations)
        * queries that have side effects
    * subscriptions
        * stream of responses
        * used for real-time data monitoring
        * require the use of a data-transport channel that supports continuous pushing
        of data
            * usually done with WebSockets
### query
* example
    ```
    {
      hero {
        name
        friends {
          name
        }
      }
    }
    ```
* steps
    1. validate the request against its schema
    1. traverse the tree of fields in that request and invoke the resolver function
    associated with each field
* fragment
    * example
        ```
        {
          leftComparison: hero(episode: EMPIRE) {
            ...comparisonFields // spread that fragment
          }
          rightComparison: hero(episode: JEDI) {
            ...comparisonFields // spread that fragment
          }
        }

        fragment comparisonFields on Character {
          name
          appearsIn
          friends {
            name
          }
        }
        ```
    * are the composition units of the language
    * are the reusable piece of any GraphQL operation
    * split operations into smaller parts
    * data required by an application = sum of the data required by individual components
        * makes a fragment the perfect match for a component
        * represent the data requirements for a single component and then compose them

## mutation
* queries vs mutations
  * queries - read only
  * mutations - add, edit, delete (commands)
    * typically, the transport protocol is http
  * queries will be done in parallel, mutations sequentially
* A mutation can contain multiple fields, resulting in the server executing
  multiple database WRITE / READ operations. However, unlike query fields,
  which are executed in parallel, mutation fields run in a series, one after the
  other. If an API consumer sends two mutation fields, the first is guaranteed to
  finish before the second begins. This is to ensure that a race condition does
  not happen, but it also complicates the task of something like DataLoader
* Remember that a GraphQL mutation is always a WRITE operation followed by a READ
  operation.
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

## subscription
  * Subscriptions are extremely useful when you need your UIs to autoupdate
  * For
    example, while looking at the list of Tasks on the home page, we planned to notify the
    user when new Task records are available—just like the way Twitter notifies you when
    there are new tweets on your timeline.
  * To implement such a feature, you have two options:
     Make your app continuously ask the server about the list of Tasks.
     Make your app tell the server that it is interested in new Tasks and would like to
    be notified when they are created.
* subscriptions - typically the transport is websocket


## data loaders
* Now that we have a GraphQL service with a multimodel schema, we can look at one
  of GraphQL’s most famous problems, the N+1 queries problem.
    * Because the GraphQL runtime traverses the tree field by field and resolves each field
      on its own as it does, this simple GraphQL query resulted in a lot more SQL state-
      ments than necessary.

        * Batching—We can delay asking the database about a certain resource until we
          figure out the IDs of all the records that need to be resolved. Once these IDs
          are identified, we can use a single query that takes in a list of IDs and returns
          the list of records for them.
          * After releasing the GraphQL.js reference implementation, the Facebook team also
            released a reference implementation for such a library. They named it DataLoader

                * DataLoader is a generic JavaScript utility library that can be injected into your applica-
                  tion’s data-fetching layer to manage caching and batching operations on your behalf.
                * The DataLoader class constructor expects a function as its argument, and
                  that function is expected to do the data fetching.
                  * This function is known as the batch-
                    loading function because it expects an array of key IDs and should fetch all records asso-
                    ciated with those IDs in one batch action and then return the records as an array that
                    has the same order as the array of input IDs.
                  * DataLoader takes care of batching the first two statements into a single SQL statement
                    because they happen in the same frame of execution, which is known in Node.js as a
                    single tick of the event loop
                  * For the third statement, Dataloader uses its memoization cache of .load() calls.
                  * While you can do the batching and caching manually, DataLoader enables you to
                    decouple the data-loading logic in your application without sacrificing the perfor-
                    mance of the caching and batching optimizations. DataLoader instances present a
                    consistent API over your various data sources (PostgreSQL, MongoDB, and any oth-
                    ers)
                  * DataLoader caching is not meant to be part of your application-level caching that’s
                    shared among requests.
                    * It’s meant to be a simple memoization to avoid repeatedly
                      loading the same data in the context of a single request in your application.

 * One task that GraphQL makes a bit more challenging is clients’ caching of data.
    * Responses from REST APIs are a lot easier to cache because of their dictionary nature.
    * A specific URL gives certain data, so you can use the URL itself as the cache key.
    * A graph query means a graph cache. If
      you normalize a GraphQL query response into a flat collection of records and give
      each record a global unique ID, you can cache those records instead of caching the
      full responses.
    * One of the other most famous problems you may encounter when working with
      GraphQL is commonly referred to as N+1 SQL queries.
      * Luckily, Facebook is pioneering one possible solution to this data-loading-
        optimization problem: it’s called DataLoader.
      * As the name implies, DataLoader is a
        utility you can use to read data from databases and make it available to GraphQL resolver
        functions.
      * You can use DataLoader instead of reading the data directly from databases
        with SQL queries, and DataLoader will act as your agent to reduce the SQL queries you
        send to the database
      * DataLoader uses a combination of batching and caching to accomplish that.
      * If the
        same client request results in a need to ask the database about multiple things, Data-
        Loader can consolidate these questions and batch load their answers from the data-
        base.
      * DataLoader also caches the answers and makes them available for subsequent
        questions about the same resources.
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

## pagination
* https://graphql.org/learn/pagination/

## caliban
* val api = graphQL(resolver)
  * println(api.render) // prints schema
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

## security
* A critical threat for GraphQL APIs is resource-exhaustion attacks (aka denial-of-service
  attacks). A GraphQL server can be attacked with overly complex queries that consume
  all the server resources.
* It is very simple to query for deeply nested relationships (user
  –> friends –> friends –> friends …) or use field aliases to ask for the same field many
  times.
* Resource-exhaustion attacks are not specific to GraphQL, but when working with
  GraphQL, you have to be extra careful about them.
* You can implement cost analysis on the query
  in advance and enforce limits on the amount of data that can be consumed.
* You can
  also implement a timeout to kill requests that take too long to resolve.