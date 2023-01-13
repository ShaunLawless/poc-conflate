# Contributing

Reading and following these guidelines will help us make the contribution process easy and effective for everyone involved.
Please use the available tags and milestones to help identify the reason for the change.

***All changes should be pre-authorized by the Global Distribution Team, and a card raised on the team's board.***

---

## Quicklinks

* [Coding Standards](#coding-standards)
* [Test Standards](#test-standards)
* [Pull Request Process](#pull-request-process)
* [Continuous Integration](#continuous-integration)
* [Modules](#modules)
    * [core](#core)
* [Release Process](#creating-a-release)

---

## Coding Standards

### Code Style

This project follows the [Scala Style Guide](https://docs.scala-lang.org/style/) and is aided by [scalafmt](https://scalameta.org/scalafmt/).
If using Intellij the formatter should be set to use the `.scalafmt` file in the repository.

## Test Standards

As a rule of thumb on where to locate tests:

**Unit testing** means testing individual modules of an application in isolation (without any interaction with dependencies) to confirm that the code is doing
things right.

**Integration testing** means checking if different modules are working fine when combined as a group.

**Functional testing** means testing a slice of functionality in the system (may interact with dependencies) to confirm that the code is doing the right things.

#### Unit Tests

Unit tests are located in the `test` directory of each module. Unit tests should be added to cover each individual function.
[Property Based Testing](https://www.scalatest.org/user_guide/property_based_testing) should be used were feasible to capture edge cases.

- To run unit tests via sbt use: `sbt test`.
- Individual modules can be run using `sbt module / test` where module is the module name defined in `build.sbt`.
- Individual tests can be run using `testOnly`.

For further information see [SBT Testing](https://www.scala-sbt.org/1.x/docs/Testing.html).

To generate HTML test reports run `sbt testWithReports`, the generated reports will be made available in `target/report`
of each module.

#### Functional Tests

Functional tests are located in the `fun` directory of each module. Functional tests must be added for features functionality,
especially where another module or external service is used. [Property Based Testing](https://www.scalatest.org/user_guide/property_based_testing)
should be used were feasible to capture edge cases.

- To run integration tests via sbt use: `sbt FunTest / test`.
- Individual modules can be run using `sbt module / FunTest / test` where module is the module name defined in `build.sbt`.
- Individual tests can be run using`FunTest / testOnly`.

#### Integration Tests

Integrations tests are located in the `it` directory of each module. Integration tests must be added for functionality across modules and external services,
especially where another module or external service is used. [Property Based Testing](https://www.scalatest.org/user_guide/property_based_testing)
should be used were feasible to capture edge cases.

- To run integration tests via sbt use: `sbt IntegrationTest / test`.
- Individual modules can be run using `sbt module / IntegrationTest / test` where module is the module name defined in `build.sbt`.
- Individual tests can be run using`IntegrationTest / testOnly`.

For further information see [SBT Testing](https://www.scala-sbt.org/1.x/docs/Testing.html#Integration+Tests).

#### Coverage

Code coverage is enabled using [scoverage plugin](https://github.com/scoverage/scalac-scoverage-plugin). To run the tests with coverage use
`sbt clean coverage test coverageReport`. The build has defined minimum coverage for each module and will fail if the minimum is not obtained.
Ensure that you have followed the test standards and that the minimum required coverage is met.

## Pull Request Process

Pull Requests should be kept small. Larger pieces of work should be broken down to be as small as possible.

1. Ensure that you have created a branch from the latest `main` branch.
2. Ensure that the build runs locally and that all unit and integration tests pass.
3. Rebase and squash the commits into one.
4. You may merge the Pull Request in once you have the sign-off of one other developer, or if you
   do not have permission to do that, you may request the second reviewer to merge it for you, and when all automated checks have passed.
5. Use the squash and merge for merging the Pull Request and ensure that the feature/fix branch is deleted.

## Continuous Integration

Continuous Integration is managed through [Github Actions](https://docs.github.com/en/actions). On creation of a Pull Request, the
`.github/workflows/PullRequest.yml` action will be triggered automatically. This will run the unit and integration tests against the Pull Request.
The Pull Request will be blocked until the action has succeeded and at least one reviewer has approved the change.

Auto-Merge is enabled on this repository. This may be used for low risk changes where only one person needs to approve.

## Modules

### core

This module is the main application.


## Creating A Release

[sbt-release plugin](https://github.com/sbt/sbt-release) is used to manage the release steps. These are defined in `build.sbt` and is run through
the continuous integration process. `version.sbt` in the root of the repository will be automatically updated as part of the standard release and
will bump the **minor** version for standard changes.

In the case of a bug fix or patch, as part of your change manually update the **bugfix** part of the semantic version.
In the case of a major change, as part of your change manually update the **major** part of the semantic version.

On the repository [actions release page](https://github.com/Flutter-Global/$repo-name$/actions/workflows/createRelease.yml), choose `Run workflow`.
Fill in the inputs for the name of the release branch to be created and the reason for the release and run the workflow. The release will publish
artifacts and tag the commit, along with updating `version.sbt` accordingly. A pull request will then be generated on success for the release to be
merged back into main. The pull request will also include and automated update to `CHANGELOG.md` based on the commit messages since the last
release tag.

The release process will generate both the core application artifacts along with a docker image for the standalone application.  Both are pushed to Github Package Repository
from where they can be pulled by anyone with appropriate authorisation.
