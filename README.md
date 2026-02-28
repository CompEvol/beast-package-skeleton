# BEAST 3 Package Skeleton

A minimal, ready-to-build template for creating a BEAST 3 external package
using the strongly-typed `spec` class hierarchy.

This skeleton demonstrates:
- A custom scalar distribution (`MyDistribution`) extending `ScalarDistribution` — usable directly as a prior (no `Prior` wrapper)
- A custom MCMC operator (`MyScaleOperator`) working with `RealScalarParam`
- JPMS `module-info.java` with `provides` declarations
- `version.xml` for package service discovery
- JUnit 5 testing with the new strongly-typed API
- A BEAST XML file using both custom classes with `RealScalarParam` and domain constraints

## Prerequisites

- Java 25+
- Maven 3.9+
- **GitHub Packages authentication** for BEAST 3 artifacts — add this to your `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
```

Replace `YOUR_GITHUB_USERNAME` with your GitHub username and `YOUR_GITHUB_PAT` with a [personal access token](https://github.com/settings/tokens) that has the `read:packages` scope. BEAST 3 dependencies are resolved automatically from GitHub Packages.

Alternatively, you can install BEAST 3 to your local Maven repository from source:

```bash
cd /path/to/beast3modular
mvn install:install-file -Dfile=lib/beagle.jar -DgroupId=beast -DartifactId=beagle -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/colt.jar -DgroupId=beast -DartifactId=colt -Dversion=1.0 -Dpackaging=jar
mvn install -DskipTests
```

## Build and test

```bash
mvn compile   # compile against beast-base
mvn test      # run MyDistributionTest
```

## How to customise this skeleton

1. **Rename the Maven coordinates** in `pom.xml`:
   - Change `groupId`, `artifactId`, and `version`

2. **Rename the Java module** in `src/main/java/module-info.java`:
   - Change `module my.beast.pkg` to your module name
   - Update `exports` and `provides` declarations

3. **Rename the Java package** under `src/main/java/`:
   - Move source files to your package directory
   - Update `package` statements in all `.java` files

4. **Update `version.xml`**:
   - Change the package `name` and `version`
   - List your `BEASTInterface` providers

5. **Replace the example classes** with your own:
   - See `MyDistribution.java` for the `ScalarDistribution` pattern
   - See `MyScaleOperator.java` for the `Operator` + `RealScalarParam` pattern

## Key concepts (new spec API)

| Old (deprecated)                    | New (spec)                           |
|-------------------------------------|--------------------------------------|
| `RealParameter`                     | `RealScalarParam<D>` / `RealVectorParam<D>` |
| `ParametricDistribution`            | `ScalarDistribution<S, T>`           |
| `Prior` wrapper + `ParametricDistribution` | Distribution with `param` input (acts as its own prior) |
| `lower`/`upper` bounds              | Domain types: `Real`, `PositiveReal`, `NonNegativeReal`, `UnitInterval` |

## Adding beast-fx for GUI packages

If your package includes BEAUti input editors or other GUI components,
add this dependency to `pom.xml`:

```xml
<dependency>
    <groupId>beast</groupId>
    <artifactId>beast-fx</artifactId>
    <version>${beast.version}</version>
</dependency>
```

And add `requires beast.fx;` to your `module-info.java`.

## Deploying as a BEAST package

To deploy your package for others to install via the BEAST Package Manager:

1. Run `mvn package` to create the JAR in `target/`
2. Ensure `version.xml` lists all your `BEASTInterface` providers
3. Distribute the JAR and `version.xml` to users (publishing workflow TBD for BEAST 3)

## Further reading

- [BEAST 3 source](https://github.com/CompEvol/beast3)
- [BEAST 2 → 3 migration guide](https://github.com/CompEvol/beast3/blob/master/scripts/migration-guide.md)
- [morph-models](https://github.com/alexeid/morph-models) — worked example of a migrated multi-module BEAST 3 package
