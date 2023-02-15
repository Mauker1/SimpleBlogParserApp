Blog parser - Maurício Pessoa
---

This project contains an app that handles 3 simultaneous requests to the Android Essence "The Imposter's Guide To Dependency Injection" blog post and do some processing with the results, finding the 10th character in the first request, every 10th character in the second request and counts each unique word in the final request.

### Technologies and patterns used

- Kotlin
- MVP + Repository pattern
- DI: Koin
- Retrofit + Scalars
- Coroutines
- Mockk for unit testing


### How the requests are made

Once the app is open, a floating action button is presented on the screen. As soon as it's pressed, the view handles the touch event to the presenter which calls the repository to fetch the requested data, this is done using 3 separate coroutines with async/await. 

The repository uses a SharedPreferences as a cache which has a validity of 2 minutes, if the cache exists and it's valid, it'll return this data instead of reaching out to the network. If we don't have a cache or it has expired, a new request will be made and it'll be written in disk for future lookups. As an extra, if we don't have network connectivity, the cache will be returned.

Once the coroutines have their data from the repository, each one will handle their specific tasks. The first one will find the 10th character and display it on a TextView; The second one will loop through the text and place every 10th character on a list and display it on another TextView; The third and last task will split the text based on whitespace characters and it'll count how many times each word showed up using a MutableMap<String, Int>, displaying the result in the last TextView once it's done.

### Decision making process

The app is written in Kotlin, as it's one of the official Android languages and it speeds up development time with its simple syntax and robust out of the box standard libraries.

The MVP pattern is used along with the Repository pattern for better scalability and testability of the project, making a clear division of responsibilities between each layer.

For dependency injection Koin was used because it's a very lightweight library and it resolves its dependencies at runtime, speeding up compile times when compared with other DI solutions such as Dagger. As a bonus, it was made with Kotlin in mind.

As for networking, Retrofit was used since it's simple to use and it's one of the best libraries for making HTTP/SPDY requests on Android. This lib can also work with many converters to handle data serialization, such as GSON or Moshi. In this case I've simply used Scalars since I only needed to handle the response as plain text.

For multithreading Coroutines was used since it's a very powerful and well supported way within Kotlin to handle asynchronous tasks, some may even refer to it as " lightweight threads".

At last, for unit testing Mockk is used. This mocking library was designed for Kotlin and it makes argument matching easy to do.

### Navigating through the project

The project files are structured as follows:

- extensionFunctions
  - This package contains all the extension functions created for this project, simplifying some common tasks such as changing a View visibility or incrementing a value on a Map.
- main
  - This package has the main activity for the project along with the presenter, repository, DI definitions and all data sources.
- mvp
  - This package has the base MVP View and Presenter interfaces.
- utils
  - This package has some utilitarian networking classes, used to check for connectivity.
- Consts
  - This file has some constants used in the app.
- BlogParserApp
  - This is the main application class, used to initialize the Timber logger and Koin.