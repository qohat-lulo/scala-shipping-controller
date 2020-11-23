# Scala Shipping Controller

_Application to control the shipments of lunch orders_

## Comenzando 🚀

_Clone the repository and run the next command to export the env vars configurations_

**IMPORTANT** - You should add this env vars in your command line manager (zsh, bash, etc)

```
$ export INPUT_FOLDER = ${YOUR_INPUT_FOLDER}
$ export SHIPPING_CONCURRENT = ${YOUR_SHIPPING_CONCURRENT}
$ export MAX_SHIPMENTS = ${YOUR_MAX_SHIPMENTS}
$ export SHIPPING_COVERAGE = ${YOUR_SHIPPING_COVERAGE}
$ export OUTPUT_FOLDER = {YOUR_OUTPUT_FOLDER}
```

### Requirements 📋

```
- jdk >= 1.8
- Scala 2.13.x
- sbt
```

## Executing the tests ⚙️

```
sbt test
```

## Run the app 📦
Inside the root directory run the next command
```
sbt run
```

## Tools 🛠️

* [Cats](https://typelevel.org/cats-effect/) - Effects management
* [fs2](https://fs2.io/guide.html) - Resource-safe, Concurrent streams (using in **FileRW** type class)
* [ciris](https://rometools.github.io/rome/) - Functional configurations (Reading env vars)

## Autor ✒️

* **Qohat U. Pretel Polo**
