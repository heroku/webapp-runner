if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then
  if [ -n "`git config --get user.email`" ]; then
    echo 'already set'; else `git config --global user.email 'buildpack@example.com'`
  fi
  if [ -n "`git config --get user.name`" ]; then
    echo 'already set'; else `git config --global user.name 'BuildpackTester'`
  fi

  cat <<EOF > ~/.ssh/config
Host heroku.com
    StrictHostKeyChecking no
    CheckHostIP no
    UserKnownHostsFile=/dev/null
Host github.com
    StrictHostKeyChecking no
EOF

  curl --fail --retry 3 --retry-delay 1 --connect-timeout 3 --max-time 30 https://toolbelt.heroku.com/install-ubuntu.sh | sh

  yes | heroku keys:add
fi
