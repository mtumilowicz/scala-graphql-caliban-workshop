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


* task: implement get all with limit

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
* scalar = leaf nodes
* https://github.com/graphql-java/graphql-java-extended-scalars
* https://medium.com/@ghostdogpr/wrapping-impure-code-with-zio-9265c219e2e
* https://graphql.org/learn/pagination/

* * The word graph in GraphQL comes from the fact that the best way to represent data in
    the real world is with a graph-like data structure.
  * If you analyze any data model, big or
    small, you’ll always find it to be a graph of objects with many relations between them.
  * Why think of data in terms of resources (in URLs) or tables when you can think of it
    naturally as a graph?
* In a nutshell, GraphQL is all about optimizing data communication between a cli-
  ent and a server
* At the core of GraphQL is a strong type system that is used to describe data and
  organize APIs.
* GraphQL operations
  * Queries represent READ operations.
  * Mutations represent WRITE -then- READ opera-tions.
    * You can think of mutations as queries that have side effects.
  * GraphQL also supports a third request type called
    a subscription, used for real-time data monitoring requests
    * GraphQL subscriptions require the use of a data-transport channel that supports con-
      tinuous pushing of data.
    * That’s usually done with WebSockets for web applications.
* A GraphQL service can be written in any programming language, and it can be
  conceptually split into two major parts, structure and behavior:
  * The structure is defined with a strongly typed schema. A GraphQL schema is like
    a catalog of all the operations a GraphQL API can handle. It simply represents
    the capabilities of an API. GraphQL client applications use the schema to know
    what questions they can ask the service. The typed nature of the schema is a core
    concept in GraphQL. The schema is basically a graph of fields that have types;
    this graph represents all the possible data objects that can be read (or updated)
    through the service.
  * The behavior is naturally implemented with functions that in the GraphQL
    world are called resolver functions.
    * Each field in a GraphQL schema is backed by
      a resolver function. A resolver function defines what data to fetch for its field.
    * A resolver function represents the instructions on how and where to access
      raw data.
    * For example, a resolver function might issue a SQL statement to a
      relational database, read a file’s data directly from the operating system, or
      update some cached data in a document database.
* example
          query {
          employee(id: 42) {
          name
          email
          }
          }
    * type Employee(id: Int!) {
      name: String!
      email: String!
      }
      * The service can receive and parse any request. It then tries to validate the request
        against its schema.
      * The next step is to pre-
        pare the data it is asking for. To do that, the GraphQL service traverses the tree of
        fields in that request and invokes the resolver function associated with each field.
      * It
        then gathers the data returned by these resolver functions and uses it to form a single
        response.
* It basically
  decouples clients from servers and allows both of them to evolve and scale inde-
  pendently. This enables faster iteration in both frontend and backend products.
* One of the most significant—and perhaps most popular—technological reasons to
  consider a GraphQL layer between clients and servers is efficiency.
  * API clients often need
    to ask the server about multiple resources, and the API server usually knows how to
    answer questions about a single resource.
  * As a result, the client ends up having to com-
    municate with the server multiple times to gather all the data it needs
* Another big technological benefit of GraphQL is communicating with multiple
  services
  * When you have multiple clients requesting data from multiple data storage
    services (like PostgreSQL, MongoDB, and a Redis cache), a GraphQL layer in the
    middle can simplify and standardize this communication
  * Instead of a client going
    directly to multiple data services, you can have that client communicate with the
    GraphQL service
  * Then the GraphQL service communicates with the different data
    services
* The biggest relevant problem with REST APIs is the client’s need to communicate
  with multiple data API endpoints
  * REST APIs are an example of servers that require
    clients to do multiple network round trips to get data.
  * A REST API is a collection of
    endpoints where each endpoint represents a resource. So, when a client needs data
    about multiple resources, it has to perform multiple network requests to that REST
    API and then put together the data by combining the multiple responses it receives.
  * In a pure REST API (not a customized one), a client cannot specify which fields to
    select for a record in that resource.
  * One other big problem with REST APIs is versioning. If you need to support multi-
    ple versions, that usually means new endpoints.
  * REST APIs eventually turn into a mix of regular REST endpoints plus custom ad hoc
    endpoints crafted for performance reasons.

  * To create a GraphQL API, you need a typed schema. A GraphQL schema contains
    fields that have types. Those types can be primitive or custom. Everything in the
    GraphQL schema requires a type.

  * To solve the multiple round-trip problem, GraphQL makes the responding server
    work as a single endpoint.
  * Basically, GraphQL takes the custom endpoint idea to an
    extreme and makes the whole server a single smart endpoint that can reply to all data
    requests.

  * Versioning can be
    avoided altogether. Basically, you can add new fields and types without removing the
    old ones because you have a graph and can flexibly grow it by adding more nodes.

  * Clients can continue to use older features, and
    they can also incrementally update their code to use new features.
  * This is especially important for mobile clients because you cannot control the ver-
    sion of the API they are using.
  * GraphQL offers a way to deprecate (and hide)
    older nodes so that consumers of the schema only see the new ones.

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

* All GraphQL operations must specify their selections down to fields that return
  scalar values (leaf values)
    * For example, they cannot have fields that describe objects
      without providing further nested selection sets to specify which scalar values to fetch
      for these objects.

* When you ask for a list of records from a collection, a good API will always ask you to
  provide a limit.

    * In GraphQL, fragments are the composition units of the language.
    * They provide a
      way to split big GraphQL operations into smaller parts.
    * A fragment in GraphQL is sim-
      ply a reusable piece of any GraphQL operation.

        * Splitting a big GraphQL document into smaller parts is the main advantage of
          GraphQL fragments.
          * However, fragments can also be used to avoid duplicating a
            group of fields in a GraphQL operation.

              * original
                * query OrgInfo {
                  organization(login: "jscomplete") {
                  name
                  description
                  websiteUrl
                  }
                  }
              * fragment orgFields on Organization {
                name
                description
                websiteUrl
                }

  * query OrgInfoWithFragment {
    organization(login: "jscomplete") {
    ...orgFields
    }
    }

        * The three dots before orgFields are what you use to spread that fragment.
        * The three-dotted fragment name ( ...orgFields ) is called a fragment spread

    * The
      data required by an application is the sum of the data required by that application’s
      individual components, and GraphQL fragments offer a way to split a big query into
      smaller ones.
    * This makes a GraphQL fragment the perfect match for a component!

    * We can use a GraphQL fragment to represent the data requirements for a single com-
      ponent and then put these fragments together to compose the data requirements for
      the entire application.

            * By making every component
              responsible for declaring the data it needs, these components have the power to
              change their data requirements when necessary without having to depend on any of
              their parent components in the tree.

  * type Query {
    taskMainList: [Task!]
}
  * The square brackets modify the type
    to indicate that this field is a list of objects from the Task model
  * The exclamation mark after the Task type
    inside the square brackets indicates that all items in this array should have a value and
    that they cannot be null.
      * A general good practice in GraphQL schemas is to make the types of fields non-null,
        unless you have a reason to distinguish between null and empty
        * This is why I made the taskMainList nullable, and it’s why I will make all root fields
          nullable. The semantic meaning of this nullability is, in this case, “Something went
          wrong in the resolver of this root field, and we’re allowing it so that a response can
          still have partial data for other root fields.”
            * It’s a
              good idea to represent errors caused by invalid uses of mutations differently from
              other root errors a GraphQL API consumer can cause.
              * For example, trying to request
                a nonexistent field is a root error. However, trying to create a user with a username
                that’s already in the system is a user error that we should handle differently.


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

* A mutation can contain multiple fields, resulting in the server executing
  multiple database WRITE / READ operations. However, unlike query fields,
  which are executed in parallel, mutation fields run in a series, one after the
  other. If an API consumer sends two mutation fields, the first is guaranteed to
  finish before the second begins. This is to ensure that a race condition does
  not happen, but it also complicates the task of something like DataLoader
* Remember that a GraphQL mutation is always a WRITE operation followed by a READ
  operation.

  * Subscriptions are extremely useful when you need your UIs to autoupdate
  * For
    example, while looking at the list of Tasks on the home page, we planned to notify the
    user when new Task records are available—just like the way Twitter notifies you when
    there are new tweets on your timeline.
  * To implement such a feature, you have two options:
     Make your app continuously ask the server about the list of Tasks.
     Make your app tell the server that it is interested in new Tasks and would like to
    be notified when they are created.
