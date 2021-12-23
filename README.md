[![Build Status](https://app.travis-ci.com/mtumilowicz/scala-graphql-caliban-workshop.svg?branch=master)](https://app.travis-ci.com/mtumilowicz/scala-graphql-caliban-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
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
    * https://ghostdogpr.github.io/caliban
    * https://www.manning.com/books/graphql-in-action
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-1-8ceb6099c3c2
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-2-c7762110c0f9
    * https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-3-8962a02d5d64
    * https://www.peerislands.io/how-to-use-graphql-to-build-bffs/
    * https://graphql.org/
    * https://www.apollographql.com/docs
    * https://medium.com/the-marcy-lab-school/what-is-the-n-1-problem-in-graphql-dd4921cb3c1a
    * https://www.apollographql.com/blog/graphql/explaining-graphql-connections/
    * https://www.apollographql.com/blog/graphql/pagination/understanding-pagination-rest-graphql-and-relay
    * https://www.sitepoint.com/paginating-real-time-data-cursor-based-pagination/
    * https://shopify.dev/api/usage/pagination-graphql

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
    * instead of the frontend application aggregating data by calling multiple sources - create a BFF layer
        * layer does the following:
            * receive request from the client application
            * call multiple backend services
            * format and response according to what is needed by the client
            * respond to the client
    * pros
        * simplify the frontend logic
        * avoid over-fetching or under-fetching
        * reduce the number of network calls from the client perspective
* real world data representation
    * best way: graph-like data structure
        * data model is usually a graph of objects with relations between them
    * why think of data in terms of resources (in URLs) or tables?

## graphql
* GraphQL = Graph Query Language
* is a new API standard that provides
    * more efficient,
    * powerful
    * flexible
    * alternative to REST
* can be written in any programming language
* has two major parts
    * structure = strongly typed schema
        * schema = graph of fields that have types
            * all possible data objects that can be read (or updated)
        * client uses the schema to know what are the capabilities of API
    * behavior = resolver functions
        * each field in a GraphQL schema is backed by a resolver function
        * resolver function defines what data to fetch for its field
        * resolver function represents the instructions on how and where to access raw data
* takes the custom endpoint idea to an extreme
    * the whole server = single smart endpoint
    * multiple round-trip problem
        * client has to communicate with the server multiple times to gather all data
        * API server knows how to answer questions about a single resource
        * solution: GraphQL
* 5 key characteristics
    1. hierarchical: queries = hierarchies of data definitions
    1. view-centric: built to satisfy frontend requirements
    1. strongly-typed: typed context (schema) + queries are executed within this context
    1. introspective: type system (schema) itself is queryable
    1. version-free: tools for the continuous evolution
* vs REST
    * don't need any documentation like swagger
    * REST = over-fetching (to many fields) and under-fetching (many calls to get data you need)
        * client cannot specify which fields to select
    * REST: each endpoint represents a resource
        * multiple network requests
    * REST: problem with versioning
        * usually means new endpoints
        * GraphQL: add new fields and types without removing the old ones
            * clients can continue to use older features
                * can also incrementally update their code to use new features
            * important for mobile clients
                * you cannot control the version of the API they are using
            * GraphQL offers a way to deprecate (and hide)
    * REST: turn into a mix of regular REST endpoints plus custom endpoints crafted for performance reasons
* summary
    * pros
        * decouples clients from servers
            * allows both of them to evolve and scale independently
        * efficiency
        * simplify client logic: client communicate with the GraphQL service
            * GraphQL service communicates with the different services
    * cons
        * malicious queries
            * complex queries
                * solution: analyze AST complexity
            * large result size
                * solution: limit depth
            * long execution time
                * solution: limit the execution time
                * solution: stream the results
        * n+1 problem
            * solution: data loader
        * caching is no longer simple
            * network layer - unsuitable as there is a common URL for all operations
            * solution: granularity (per field)
        * hard to return simple Map

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
    * don't have any sub-fields
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
    1. traverse the tree of fields and invoke the resolver functions
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
* is always a WRITE operation followed by a READ operation
* vs queries
    * queries will be done in parallel
    * mutations sequentially
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

### subscription
* client should NOT use subscriptions to stay up to date with backend
    * use poll intermittently with queries
    * re-execute queries on demand when a user performs
    a relevant action (such as clicking a button)
* use subscriptions for
    * small, incremental changes to large objects
        * polling for a large object is expensive
            * especially when most of the object's fields rarely change
        * fetch the object's initial state with a query
            * server can proactively push updates to individual fields
    * low-latency, real-time updates
        * example: a chat application

## data loaders
* problem
    * querying for authors and books
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
    * in graphQl: each resolver function really only knows about its own parent object
        * ORM won’t have the luxury of a list of author IDs anymore
        * result: N+1 calls
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
        * no application-level caching shared among requests
        * rather simple memoization in the context of a single request
    * example library: DataLoader (JavaScript utility library)

## client cache
* responses from REST are easy to cache
    * dictionary nature
        * specific URL gives certain data
        * use the URL itself as the cache key
* in graphQl: graph cache
    * no URL-like primitive (globally unique identifier for a given object)
    * best practice: expose such an identifier for clients to use
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
* two scenarios
    * UX concern: too many items to display
        * mental overload for the user to see them all at once
    * performance concern: too many items to load
        * it would overload our backend, the connection, or the client to load all of the items at once
* types of pagination from the UX perspective
    * numbered pages
        * example: book, Google search
        * expect it to be consistent over some period of time
        * sql
            ```
            SELECT * FROM posts ORDER BY created_at LIMIT 10 OFFSET 20; // page 3, with a page size of 10
            SELECT COUNT(*) FROM posts; // total number of entries or pages in the results
            ```
        * drawbacks
            * only for mostly static content
            * however, usually items are added and removed while the user is navigating
                * leads to skipping items
                * or displaying the same item twice
                    * new item was added at the top of the list
                        * skip and limit approach to show the item at the boundary between pages twice
    * sequential pages like Reddit
        * aren’t numbered
        * content changes so rapidly - no point in page numbers at all
        * specify the place in the list we want to begin, and how many items we want to fetch
            * it doesn’t matter how many items were added to the top of the list
            * we have a constant pointer to the specific spot where we left off
                * pointer is called a cursor
                * cursor is a piece of data
                    * generally some kind of ID
                    * represents a location in a paginated list
        * example
            ```
            SELECT * FROM posts
            WHERE created_at < $after
            ORDER BY created_at LIMIT $page_size;

            https://www.reddit.com/?count=25&after=t3_49i88b
            ```
        * good practice: encoded cursor with some metadata or a timestamp (instead of a row ID)
            * resilient to row deletion
            * we don’t want the query to fail if a specific item is removed
    * infinite scroll like Twitter
        * illusion of one very long page
    * modern apps today use either the second or third approach
        * app’s content is constantly changing
        * doesn’t make sense to create the illusion of numbered pages

### relay cursor connections
* generic specification for how a GraphQL server should expose paginated data
* generalized concepts we were talking about above
    * `friends(first:2 after:$opaqueCursor) // vs friends(first:2 after:$friendId)`
        * cursors are opaque and their format should not be relied upon
            * suggestion: base64 encoding
        * additional flexibility for pagination model changes
            * user just uses opaque cursors
* example
    * request
        ```
        {
          user {
            id
            name
            friends(first: 10, after: "opaqueCursor") {
              edges { // each edge has a reference to the user object of the friend, and a cursor
                cursor // every item in the paginated list has its own cursor
                node {
                  id
                  name
                }
              }
              pageInfo {
                hasNextPage
              }
            }
          }
        }
        ```
        * notice that if we want to, we can ask for 10 friends starting from the middle of the list we last fetched
    * response
        ```
        {
          "data": {
            "products": {
              "pageInfo": {
                "hasNextPage": true,
                "hasPreviousPage": false
              },
              "edges": [
                {
                  "cursor": "eyJsYXN0X2lkIjoxMDA3OTc4ODg3NiwibGFzdF92YWx1ZSI6IjEwMDc5Nzg4ODc2In0=",
                  "node": {
                    "id": "1",
                    "name": "Michal"
                  }
                },
                {
                  "cursor": "eyJsYXN0X2lkIjoxMDA3OTc5MzQyMCwibGFzdF92YWx1ZSI6IjEwMDc5NzkzNDIwIn0=",
                  "node": {
                    "id": "2",
                    "name": "Marcin"
                  }
                },
                {
                  "cursor": "eyJsYXN0X2lkIjoxMDA3OTc5NDM4MCwibGFzdF92YWx1ZSI6IjEwMDc5Nzk0MzgwIn0=",
                  "node": {
                    "id": "3",
                    "name": "Anna"
                  }
                }
              ]
            }
          },
          ...
        }
        ```
* glossary
    * connection - paginated field on an object
        * example: friends field on a user
    * edge - metadata about one object in the paginated list
        * includes a cursor to allow pagination starting from that object
    * node - actual object
    * pageInfo - info about more pages of data to fetch

## security
* critical threat: resource-exhaustion attacks (DOS attacks) with overly complex queries
    * are not specific to GraphQL
    * example: query for deeply nested relationships
        * (user –> friends –> friends –> friends …)
    * example use field aliases to ask for the same field many times
        ```
        {
          empireHero: hero(episode: EMPIRE) {
            name
          }
          jediHero: hero(episode: JEDI) {
            name
          }
        }
        ```
* solution
    * cost analysis on the query
    * enforce limits on the amount of data
    * timeouts

## caliban
* features
    * minimize boilerplate
    * purely functional (strongly typed, explicit errors)
    * user friendly
    * schema / resolver separation
* vs sangria
    * sangria: lots of boilerplate (macros to the rescue)
    * sangria: future based (effects are better)
    * sangria: schema and resolved tied together
* schema is derived automatically from the case classes
    * mangolia - used for create schema for traits and case classes
        * provide a schema for custom types
            ```
            implicit val nesSchema: Schema[NonEmptyString] = Schema.stringSchema.contramap(_.value)
            ```
            * many useful types: https://github.com/niqdev/caliban-extras
    ```
    val api = graphQL(resolver)
    println(api.render) // prints derived schema
    ```
* schema deriving examples
    * case classes
        ```
        case class Pug(name: String, nicknames: List[String], pictureUrl: Option[String])
        ```
        is transformed into
        ```
        type: Pug {
            name: String,
            nicknames: [String!]!,
            pictureUrl: String // optionality -> option
        }
        ```
    * enums
        ```
        sealed trait Color
        case object FAWN extends Color
        ```
        is transformed into
        ```
        type: enum Color { FAWN }
        ```
    * arguments
        ```
        case class PugName(name: String)
        case class Queries(pug: PugName => Option[Pug])
        ```
        is transformed into
        ```
        type: Queries { pug(name: String!): Pug }
        ```
* n+1 problem
    * solution: `ZQuery`
        * parallelize queries
        * cache identical queries
        * batch queries if batching function provided
* builtin wrappers
    ```
    val api = graphQL(...) @@
      maxDdepth(30) @@
      maxFields(200) @@
      timeout(10 seconds) @@
      printSlowQueries(1 second)
    ```