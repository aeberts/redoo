# redoo

A [re-frame](https://github.com/Day8/re-frame) application designed to ... well, that part is up to you.

## Development Mode


### Dirac

Dirac Tools Setup

One time installation:
Download Chome Canary

Install Dirac DevTools Extension here:
https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi

To Launch Dirac:
* Launch Chrome Canary from command line with devchrome command which starts Chrome in debug mode.

```
#!/bin/bash
/Applications/Google\ Chrome\ Canary.app/Contents/MacOS/Google\ Chrome\ Canary \
  --remote-debugging-port=9222 \
  --no-first-run \
  --user-data-dir=/Users/zand/.chromedevprofile
```

* Run the project from terminal - starts the server (dirac-sample command: lein demo)
* In a separate terminal tab start the nREPL server and the Dirac agent (dirac-sample command: lein repl)
* Click the dirac button in the browser - dirac devtools pop up.
* Click retry connection - Dirac should connect to nRepl

Reference:

Dirac Installation:
https://github.com/binaryage/dirac/blob/master/docs/installation.md

Instructions on how to integrate with Cursive:
https://github.com/binaryage/dirac/blob/master/docs/integration.md

Dirac-sample:
https://github.com/binaryage/dirac-sample

General Info on compiling cljs:
https://clojurescript.org/guides/quick-start

Compiling cljs using tools:
https://funcool.github.io/clojurescript-unraveled/#tooling-compiler

CLJS Compiler Options:
https://github.com/clojure/clojurescript-site/blob/1c3e4d25cea7ce600071f803c2e5cef5b3fb27a1/content/reference/compiler-options.adoc

Lein-Cljsbuild:
https://github.com/emezeske/lein-cljsbuild

Figwheel and nREPL
https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl

Shared compiler build options:
https://github.com/emezeske/lein-cljsbuild/issues/444



## These are probably mostly broken now:

### Compile css:

Compile css file once.

```
lein garden once
```

Automatically recompile css file on change.

```
lein garden auto
```

### Compile css:

Compile css file once.

```
lein less once
```

Automatically recompile css file on change.

```
lein less auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

```
lein clean
lein uberjar
```

That should compile the clojurescript code first, and then create the standalone jar.

When you run the jar you can set the port the ring server will use by setting the environment variable PORT.
If it's not set, it will run on port 3000 by default.

To deploy to heroku, first create your app:

```
heroku create
```

Then deploy the application:

```
git push heroku master
```

To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
