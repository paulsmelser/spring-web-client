# spring-web-client

### GET a resource
```kotlin
val response = resource.request()
                .path("/test")
                .get<TestClass>()
val test: TestClass = response.body
```

### POST a resource
```kotlin
val response = resource.request()
                .path("/test")
                .post(test)
```

### Query Param
```kotlin
var response = resource.request()
                .path("/test")
                .queryParam("hi", "my", "values")
                .get<TestClass>()
```

### Query Param
```kotlin
var response = resource.request()
                .path("/test")
                .queryParam("hi", "my", "values")
                .get<TestClass>()
```

### Adding Headers
```kotlin
 val entity = resource.request()
                    .header("Authorization", "noAuth")
                    .path("/test/list")
                    .get<List<TestClass>>()
```