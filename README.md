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
    * https://www.apollographql.com/docs
    * https://medium.com/the-marcy-lab-school/what-is-the-n-1-problem-in-graphql-dd4921cb3c1a

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

### mutation
* queries will be done in parallel, mutations sequentially
    * if an API consumer sends two mutation fields, the first is guaranteed to
    finish before the second begins
* example
    ```
    mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
      createReview(episode: $ep, review: $review) {
        stars
        commentary
      }
    }

    { // variables
      "ep": "JEDI",
      "review": {
        "stars": 5,
        "commentary": "This is a great movie!"
      }
    }
    ```
* is always a WRITE operation followed by a READ operation

### subscription
* in the majority of cases, client should NOT use subscriptions to stay up to date with backend
    * use poll intermittently with queries, or re-execute queries on demand when a user performs
    a relevant action (such as clicking a button)
* use subscriptions for
    * small, incremental changes to large objects
        * polling for a large object is expensive, especially when most of the object's fields rarely change
        * fetch the object's initial state with a query, and your server can proactively push updates to individual fields
    * low-latency, real-time updates
        * example: a chat application's client wants to receive new messages as soon as they're available

## data loaders
* problem
    * authors "has many" books
    * we would like to achieve two SQL calls
        ```
        SELECT *
        FROM authors;
        -- pretend this returns 3 authors
        SELECT *
        FROM books
        WHERE author_id in (1, 2, 3); -- an array of the author's ids
        ```
    * query
        ```
        {
          query { // 1 call
            authors {
              name
              books { // each book resolver would only get it’s own parent author: N calls
                title
              }
            }
          }
        }
        ```
    * in REST: ORM will help
        * graphQl: each resolver function really only knows about its own parent object
            * runtime traverses the tree field by field and resolves each field on its own
            * ORM won’t have the luxury of a list of author IDs anymore
            * N+1 calls
                ```
                SELECT *
                FROM authors;

                SELECT *
                FROM books
                WHERE author_id in (1);

                SELECT *
                FROM books
                WHERE author_id in (2);

                SELECT *
                FROM books
                WHERE author_id in (3);
                ```
* solutions
    * batching
        * delay asking the database until we will have all appropriate IDs
    * caching
        * is not meant to be application-level caching hared among requests
        * it should be simple memoization to avoid repeatedly loading the same data in the context of a single request
    * example library: DataLoader (JavaScript utility library)
        * enables to decouple the data-loading logic without sacrificing the performance

## client cache
* responses from REST are easy to cache
    * dictionary nature
        * specific URL gives certain data
        * use the URL itself as the cache key
* solution: graph cache
    * no URL-like primitive that provides this globally unique identifier for a given object
    * best practice for the API to expose such an identifier for clients to use
        ```
        {
          starship(id:"3003") { // id field provides a globally unique key
            id
            name
          }
          droid(id:"2001") {
            id
            name
            friends {
              id
              name
            }
          }
        }
        ```

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