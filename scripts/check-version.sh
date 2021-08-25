#!/usr/bin/env bash

set -euo pipefail

# Run Maven once to ensure Maven wrapper downloads Maven before executing any scripting commands.
# There is currently no way to silence Maven wrapper itself, making this step necessary.
./mvnw --quiet help:help &>/dev/null

webappRunnerVersion="$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -B -DforceStdout)"
tomcatCoreVersion="$(./mvnw -DskipTests install dependency:list | awk -F: '/org.apache.tomcat.embeda:tomcat-embed-core/{print $4}' | sort | uniq)"

if [[ $(echo "${tomcatCoreVersion}" | wc -l) -ne 1 ]]; then
	cat <<-EOF
		Ambiguous Tomcat version!
		=========================
		Could not detect distinct Tomcat version used by this project. This information is required for this check to
		operate correctly. Ensure only one Tomcat version is referenced in the POM.

		Detected Tomcat versions:
		${tomcatCoreVersion}
	EOF
	exit 1
fi

if [[ $webappRunnerVersion != "${tomcatCoreVersion}"* ]]; then
	cat <<-EOF
		Incorrect webapp-runner Version!
		================================
		By convention, this project's version number always starts with the version number of the
		embedded Tomcat server. Read more on DevCenter:

		https://devcenter.heroku.com/articles/java-webapp-runner#configure-maven-to-download-webapp-runner

		In this case, the version number of webapp-runner is "${webappRunnerVersion}" which does not start with the
		version number of the embedded Tomcat server "${tomcatCoreVersion}".

		To set the webapp-runner version to "${tomcatCoreVersion}.0-SNAPSHOT", run:
		./mvnw versions:set -DnewVersion=${tomcatCoreVersion}.0-SNAPSHOT -DgenerateBackupPoms=false
	EOF
	exit 1
else
	echo "OK!"
	exit 0
fi
