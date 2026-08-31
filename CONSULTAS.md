# Consultas

1. [WebConfig](webapp/src/main/java/ar/edu/itba/paw/webapp/config/WebConfig.java) debiera usar un `CookieLocaleResolver` o un `DatabaseLocaleResolver`.

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
