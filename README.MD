# Task

- Imagine you're building an URL shortener as a potential next-big-thing product. To test it, we would like to first
  have an MVP with only basic functionality.
- However, if everything goes according to plan, we will invest more effort into this so even the MVP should be a
  modern, nicely written, maintainable and production ready API.
- For the MVP we don't want any kind of user registration or security. We only care that we provide a proper REST API
  that will enable other users/ clients to send a URL and will receive some kind of identifier/ hash to which this URL
  corresponds in our system. And the other way around of course, meaning that a user should be able to resolve the full
  URL.
- To speed up the development, we won't be writing everything on our own, so please think of libraries/ components you
  could use to fulfill the task.
- Production ready API can mean a lot of things to different people. We're interested to see what you think a quality
  production ready API solution looks like. Imagine that the moment you deliver the solution, we'll deploy it to all our
  environments, consisting of multiple instances. So, provide data in a way that allows customization for dedicated
  environments
- When designing the solution please think about global scalability: what endpoints will be used the most, what might be
  a bottleneck, what could be a possible solution to identify problems

# Solution

- First I google how to build an url shortener (warning! acid green background!)
  https://medium.com/hackernoon/url-shortening-service-in-java-spring-boot-and-redis-d2a0f8848a1d
- Initial design uses Redis to keep the sequence and to store urls, and base64 to encode them.
- I originally wanted to send the shortened url in the response body as a full url (host + port). However, in production
  environment the application will most likely be behind a reverse proxy or a load balancer, so I decided to provide
  only the resource identifier (uri) in the Location header. Frontend should be responsible for resolving it relative to
  the correct host.
- Redis might not be good enough even for MVP, we probably want shortened urls to be stored for longer than a day.

- Second voyage to google (less of a tutorial, more about systems design and scaling)
  https://www.enjoyalgorithms.com/blog/design-a-url-shortening-service-like-tiny-url
- We can create a shortened url by hashing the full url, but this results in rather long string, and if we truncate it
  we risk having duplicate values (same truncated hash for multiple values). The preferred approach seems to be using an
  auto-incremented id and encoding it to base62 or base64. However, the question is where to store the sequence.
- SQL databases provide an auto-incremented identity out-of-the-box, so we could insert the full url, get the id, encode
  it, give it to the client, then decode it back to an id and fetch the full url. However, this approach doesn't scale
  well - if we use only a single database, it becomes a single point of failure.
- NoSql databases (Redis, Mongo) do not provide auto-incremented identity out-of-the-box, but it could still be obtained
  by creating a sequence manually. In this case we separate the sequence and the table id column, which seems like a
  disadvantage. However, on second thought, this might be a better approach in terms of horizontal scaling.

- Third voyage to google
  https://alexmarquardt.com/2017/01/30/how-to-generate-unique-identifiers-for-use-with-mongodb
- Mongo - fetch a batch of 1000 ids from the sequence and store in memory
- Redis - Spring Repository does not support reactive operations nor direct insertion - replaced with Template

# Design

- frontend
- load balancer (+ rate limiting)
- application (multiple instances)
- db cluster (router + sharding)

# Advanced features (more than MVP)

- multiple service implementations provided, with different profiles
- use cache for fetching more popular urls (preferably LRU)

# Premium options
- provide a custom shortened url
- longer expiration time
- higher rate limiting quota
