# Contributing

## General
Please use the following workflow to contribute:
* Fork the repository
* Create a branch which accurately describes your fix or feature (prefix branches with `fix/`, `feat/`, `docs/`, etc). 
  * Please use signed commits if you can - you are contributing to the backbone of a cryptocurrency.
* Submit a pull request against `main`

*Note:* A minimum of 2 approving reviews are required to merge. Squash merges are preferred unless there are very few commits to merge

## Code formatting
We are using the Google Java Style.
Please be aware that we use the Google Java Format GitHub Action (https://github.com/axel-op/googlejavaformat-action) to ensure that the code is formatted according to our coding guidelines.
That means if there have been changes detected during a git push operation between your coding style and the wanted target style the GitHub Action enforces the target style with a new commit.
Important: Please see https://github.com/google/google-java-format for plugins you can use inside your IDE to avoid such reformatting commits.

If you got a problem using the plugin: We provide an example of a formatter settings file for Eclipse in the dev folder which can be imported in the Eclipse Preferences/Formatter section (see [Eclipse Java Style XML](dev/signum-eclipse-java-style.xml)). This file is based on https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml and was modified according to the current ruleset.

Please be careful when using features like "Organize imports" as this can lead to an unwanted mass change of import lines resulting in unnecessary large code reviews.

