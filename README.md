# file_server
A minimal file server based on Akka Http.
The project has been built keeping in mind, a service that can be built within approx 4-5 hours.

###  Requirements: Create an HTTP service (server) with the following specification:

1. The service has a single endpoint with the following signature and request body structure
POST /api/server/create
{"requestId": "a random alphanumeric string (S1)"}
2. The endpoint is rate limited - it only accepts 2 request / s / unique resourceId
3. The endpoint creates a local file but it takes 5-10s to create it (no less than 5s, no longer than
10s). The value of the file content is another random alphanumeric string (S2).
4. The endpoint responds immediately with either


Output:
A:
{"requestId”: "{{S1}}", "created": true, "fileContent": "{{S2}}"} - if the time resource was created
or B:
{"requestId”: "{{S1}}", "created": false} - if the time resource was not created
or C:
HTTP response code 429 (Too Many Requests) and an empty body if rate limit was exceeded


####Suggested Solution:
1. Idiomatic approach, using custom code than Akka streams.
2. Akka HTTP based application exposing a single route with Post method POST /api/server/create.
3. Use Custom direction to manage Rate limiting and internal threadsafe cache to keep track of request and resource.
4. Use Akka Actor for async calls and adding 5s delay.
5. Store file in a central location.

####Assumptions:
1. Since currently we do not have a criteria to identify a unique resource, only for test purpose
   1. We assume the requestId as the unique resource.
   2. Can be further extended to create a checksum of whatever request entities we require.
2. The time duration of the rate limit is currently based on the function/service call.
   1. Max no of request when the server is processing a single resource (requestID)
   2. Can be extended to take a specific time duration via the directive.


####Acceptance Criteria:
1. When a user calls the API, a file is created in a central location and random generated fileContent is returned as the response for a unique requestId with 200 status code.
2. When a user calls the API with previous requestId, a new file is created in a central location and random generated fileContent is returned with 200 status code.
3. When a user makes more than 2 consecutive calls, when the server is processing the previous request, an empty response with 429 (Too many request) status code is returned.
4. When a user calls the API and error occurs in the server, an empty response with 500 (Internal error) status code is returned.


## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, type/run the following command in the terminal:

```bash
sbt run
or
Run via IDE
```

Limitation:

Extensions:

####Note:


Disclaimer & Credit:
The custom directive idea has been taken from https://gist.github.com/johanandren/b87a9ed63b4c3e95432dc0497fd73fdb 
However instead of using AtomicNumber, we are using a threadSafe map (ConcurrentHashMap)