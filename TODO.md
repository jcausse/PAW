# ToDo

1. Doble `LocaleResolver`

    [WebConfig](webapp/src/main/java/ar/edu/itba/paw/webapp/config/WebConfig.java) debiera usar AMBOS `CookieLocaleResolver` y un `DatabaseLocaleResolver`.

    Actualmente tenemos:
    ```java
    @Bean
    public LocaleResolver localeResolver() {
        final var localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }
    ```
   
    Alternativa:
    ```java
    @Bean
    public LocaleResolver localeResolver(UserService userService) {
        return new DatabaseLocaleResolver(userService);
    }
    ```

2. Chequear el fix del `PasswordEncoder` en su rama aparte.

