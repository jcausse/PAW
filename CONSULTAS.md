# Consultas

1. Estan bien las dependencias entre los modulos?
2. Esta bien haber sacado `<link>` de los POM?
3. Esta bien `<httpConnector>` para envolver `<port>` en la dependencia de Jetty en el POM de `webapp`?
    ```xml
    <plugin>
      <groupId>org.eclipse.jetty</groupId>
      <artifactId>jetty-maven-plugin</artifactId>
      <version>9.4.58.v20250814</version>
      <configuration>
        <scanIntervalSeconds>5</scanIntervalSeconds>
        <httpConnector>
          <port>8080</port>
        </httpConnector>
        <useTestScope>true</useTestScope>
      </configuration>
    </plugin>
   ```
4. Esta bien que en el POM de `webapp` se dependa tanto de `service` como de `persistence` con `runtime`?
5. Revisar los warnings de este estilo al usar `make run`:
   ```text
   [WARNING] org.apache.taglibs.standard.Version scanned from multiple locations: jar:file:///home/juani/.m2/repository/org/apache/taglibs/taglibs-standard-impl/1.2.5/taglibs-standard-impl-1.2.5.jar!/org/apache/taglibs/standard/Version.class, jar:file:///home/juani/.m2/repository/javax/servlet/jstl/1.2/jstl-1.2.jar!/org/apache/taglibs/standard/Version.class
   [WARNING] org.apache.taglibs.standard.functions.Functions scanned from multiple locations: jar:file:///home/juani/.m2/repository/org/apache/taglibs/taglibs-standard-impl/1.2.5/taglibs-standard-impl-1.2.5.jar!/org/apache/taglibs/standard/functions/Functions.class, jar:file:///home/juani/.m2/repository/javax/servlet/jstl/1.2/jstl-1.2.jar!/org/apache/taglibs/standard/functions/Functions.class
   ```
6. Es necesario `language="java"` en los JSP?
