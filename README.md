
# Thing that could be better
Things that should be addressed properly (but keeping the UI simple):
- What happens when they don't have any available Auths?
- What should we do on an error from the Auth?
- What happens if the key doesn't exist in the SharedPreferences?
- More extensive UI tests to check behaviour of the View
- KeyGenParameterSpec needed PowerMock to test, intentionally left this out 
    - means theres no test covering what happens when the key isn't found and it has to generate it
    - not a fan of powermock would want longer to evaluate the right thing
- Better to not use inheritance for the BiometricViewModel, instead it should be composition (but in this instance, it was all too similar)
- There is a nicer way to write the instrumentation tests using Hilt, Espresso, and a customer runner to start each fragment in its own container, but I haven't played with it too much so I've opted for what I know for time.
