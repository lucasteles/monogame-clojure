# clojure-clr-monogame-test
[Under testing]

## Requirements

- `dotnet sdk 3.1`

## Running on Dotnet
Install and register the monogame tools:
```bash
dotnet tool restore
dotnet mgcb-editor --register
```

Compile and run:
```bash
dotnet build
dotnet run
```

## Running on ClojureCLR

Install the [Clojure.Main](https://www.nuget.org/packages/Clojure.Main) global tool:
```bash
dotnet tool install --global Clojure.Main
```
Run:
```bash
Clojure.Main -m program
```

More info here:
https://github.com/clojure/clojure-clr/wiki/Getting-started
