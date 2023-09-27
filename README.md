# mvd-checks-light

Service for update a list of expired passports and checking the presence of a passport in this list.

This project uses Quarkus 3, the Supersonic Subatomic Java Framework. Quarkus 3 uses Java 17.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in container

You can mount your own volume in Dockerfile<sup>*</sup>:

```dockerfile
VOLUME /uploads
```

And run your application in docker container using:

```shell script
.\gradlew build
docker build --no-cache -f src\main\docker\Dockerfile.jvm -t mvd-checks .
docker run -it --rm --name mvd-checks -p 8080:8080 --user user:root mvd-checks
```

<sub>* - The productive volume is mounted at the okd level.</sub>

### Usefull commands
#### Command Line Tools for Determining Java Heap Size

```shell script
jps -l 
jcmd <pid> GC.heap_info
```

#### Terminate the process that is currently using the port

```shell script
netstat -ano | findstr :<port>
taskkill /PID <pid> /f
```