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
- **GitHub Packages authentication** for BEAST 3 artifacts (not needed if BEAST 3 is available on Maven Central) — add this to your `~/.m2/settings.xml`:

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

Replace `YOUR_GITHUB_USERNAME` with your GitHub username and `YOUR_GITHUB_PAT` with a [personal access token](https://github.com/settings/tokens) that has the `read:packages` scope. BEAST 3 dependencies are resolved automatically from [GitHub Packages](https://github.com/CompEvol/beast3/packages).

Alternatively, you can install BEAST 3 to your local Maven repository from source:

```bash
cd /path/to/beast3
mvn install:install-file -Dfile=lib/beagle.jar -DgroupId=io.github.compevol -DartifactId=beagle -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/colt.jar -DgroupId=io.github.compevol -DartifactId=colt -Dversion=1.0 -Dpackaging=jar
mvn install -DskipTests
```

## Build and test

```bash
mvn compile   # compile against beast-base
mvn test      # run MyDistributionTest
```

## How to customise this skeleton

1. **Rename the Maven coordinates** in `pom.xml`:
   - Change `groupId` (should be a verified Maven Central namespace, e.g. `io.github.yourname`), `artifactId`, and `version`
   - Update `<url>`, `<developers>`, and `<scm>` to point to your repository

2. **Rename the Java module** in `src/main/java/module-info.java`:
   - Change `module my.beast.example` to your module name
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
    <groupId>io.github.compevol</groupId>
    <artifactId>beast-fx</artifactId>
    <version>${beast.version}</version>
</dependency>
```

And add `requires beast.fx;` to your `module-info.java`.

## Releasing your package

The included `release.sh` script automates the full release process: build, package, and
optionally create a GitHub release.

### 1. Build the package ZIP

```bash
./release.sh
```

This will:
- Read the package name and version from `version.xml`
- Run `mvn clean package -DskipTests`
- Assemble a BEAST package ZIP with the correct flat structure
- Output a file like `MyPackage.v1.0.0.zip`

### 2. Create a GitHub release

```bash
./release.sh --release
```

This additionally creates a GitHub release (e.g. `v1.0.0`) with the ZIP attached,
and prints the CBAN XML entry you'll need for the next step.

### 3. Submit to CBAN

The [CBAN repository](https://github.com/CompEvol/CBAN) is where BEAST's Package
Manager discovers available packages. To make your package installable:

1. Fork [CompEvol/CBAN](https://github.com/CompEvol/CBAN)
2. Add your package entry to `packages2.8.xml` (the `--release` flag prints this for you):

```xml
<package name="MyPackage" version="1.0.0"
    url="https://github.com/YOU/YOUR-REPO/releases/download/v1.0.0/MyPackage.v1.0.0.zip"
    projectURL="https://github.com/YOU/YOUR-REPO"
    description="One-line description of your package">
    <depends on="BEAST.base" atleast="2.8.0"/>
</package>
```

3. Open a pull request against CompEvol/CBAN

Once merged, your package will appear in the BEAST Package Manager.

## Publishing to Maven Central

BEAST 3 can also install packages directly from Maven Central. This is an
alternative (or complement) to the ZIP/CBAN distribution above.

### Prerequisites

1. **Sonatype account** — register at [central.sonatype.com](https://central.sonatype.com/)
2. **Verified namespace** — verify your `groupId` namespace (e.g. `io.github.yourname`)
3. **GPG key** — generate a signing key and publish it to a key server
4. **Maven settings** — add credentials to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>YOUR_SONATYPE_TOKEN_USER</username>
      <password>YOUR_SONATYPE_TOKEN_PASS</password>
    </server>
  </servers>
</settings>
```

### Customisation

1. Set your `groupId` to your verified namespace (e.g. `io.github.yourname`)
2. Fill in the `<url>`, `<licenses>`, `<developers>`, and `<scm>` sections in `pom.xml`
3. Remove the `-SNAPSHOT` suffix from `<version>` for release builds

### Deploy

```bash
mvn clean deploy -Prelease
```

This builds the JAR (with `version.xml` embedded), generates sources and javadoc
JARs, signs everything with GPG, and uploads to Maven Central.

### User install

Once published, BEAST 3 users can install your package with:

```
Package Manager > Install from Maven > groupId:artifactId:version
```

Or from the command line:

```bash
packagemanager -maven groupId:artifactId:version
```

### ZIP structure

The BEAST Package Manager expects a flat ZIP (no wrapper directory) containing:

```
version.xml            # required — package name, version, service providers
lib/                   # required — your JARs (and any third-party runtime deps)
fxtemplates/           # optional — BEAUti templates
examples/              # optional — example BEAST XML files and data
```

**Important:** the ZIP must NOT contain a top-level directory named after your package.
The Package Manager extracts the ZIP into its own directory, so a wrapper would
cause double-nesting and break service discovery.

## Further reading

- [BEAST 3 source](https://github.com/CompEvol/beast3)
- [BEAST 2 → 3 migration guide](https://github.com/CompEvol/beast3/blob/master/scripts/migration-guide.md)
- [morph-models](https://github.com/CompEvol/morph-models) — worked example of a migrated multi-module BEAST 3 package
