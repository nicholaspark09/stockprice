
## Market Lib
This project is a library for Android that receives stock price information
via the api provided by: https://api.worldtradingdata.com/

### Structure
The library `marketpricelibrary` uses Kotlin and coroutines. While it doesn't
use Dagger2 or any dependency injection library, it does inject its dependencies
via their constructors. The hope is to maintain the smallest size aar and the
least amount of upstream dependencies.

### Dependencies
Retrofit, Okhttp, and Coroutines are some of the larger dependencies that will
be absorbed for any project using this library.

### Use
In order to use this library, you will need an `api_token` provided for free
if you sign up with api.worldtradingdata.com. This library was created as a 
side project and has no affiliation with that company.

### Contributions
This library is still in the works and anyone is free to open a PR against it.
