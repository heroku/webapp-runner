#!/usr/bin/env bash

set -e

# Releasing to Maven Central can be confusing. So this will partially automate
# the process.
# http://central.sonatype.org/pages/apache-maven.html

# First, make sure you have your gpg key setup
# http://central.sonatype.org/pages/working-with-pgp-signatures.html

# Then make sure your ~/.m2/settings.xml has this stuff in it:
#
# <server>
#   <id>ossrh</id>
#     <username>youruser</username>
#     <password>XXXXXXXXXX</password>
#   </server>
# </servers>
#
# <profiles>
#   <profile>
#     <id>ossrh</id>
#     <activation>
#     <activeByDefault>true</activeByDefault>
#     </activation>
#     <properties>
#       <gpg.executable>gpg</gpg.executable>
#       <gpg.passphrase>XXXXXXXXXX</gpg.passphrase>
#     </properties>
#   </profile>
# </profiles>

stty -echo
printf "GPG passphrase: "
read gpgPwd
printf '\n'
stty echo

./mvnw release:clean release:prepare -DdryRun

./mvnw release:prepare

./mvnw release:perform -Darguments="-Dgpg.passphrase=$gpgPwd"

echo "Now make sure you update these articles and projects:

    https://devcenter.heroku.com/articles/java-webapp-runner
    https://github.com/heroku/devcenter-webapp-runner
"
