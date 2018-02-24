; This was originally based on the re-frame template.
;
; Modified to work with dirac tools: https://github.com/binaryage/dirac
;
; For Dirac to work on your local machine you will need the following ingredients:
;
; 1) Dirac Chrome Extension installed in your Chrome Canary (note to self: run with `devchrome`)
; 2) Dirac Runtime - a ClojureScript library installed in your page
; 3) nREPL server with Dirac nREPL middleware
; 4) Dirac Agent - a helper server providing a proxy tunnel between the nREPL server and the Dirac Extension
;
; This project uses the figwheel development server to serve files (no production server has been defined).
;
; To start the app with Dirac:
;
; 1) Start Chrome Canary in Dev mode with devchrome script (with remote debugging enabled)
; 2) In the project dir use `lein with-profile +dev figwheel` to start the development server and figwheel.
; 3) In a new terminal window switch to the project dir and use `lein with-profile +dev repl` to start direc agent and repl.
; 4) In Chrome Canary click the devtools button - Dirac Tools should connect to the Dirac Agent
; 5) In Cursive, start a remote nrepl and at the repl prompt run the command: `(dirac! :join)` to connect to the cljs process in the browser.
;
;
; If you're using Cursive to connect to a the running nrepl server see this guide:
; https://github.com/binaryage/dirac/blob/master/docs/integration.md

(defproject redoo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 ;[binaryage/devtools "0.9.4"]
                 ;[binaryage/dirac "1.2.17"]
                 ;[figwheel "0.5.14"]
                 ;[figwheel-sidecar "0.5.14" :exclusions [ring/ring-codec joda-time clj-time]]
                 [reagent "0.8.0-alpha1"]
                 [re-frame "0.9.2"]
                 [re-frisk "0.4.5"]
                 [org.clojure/core.async "0.3.443"]
                 [re-com "0.9.0"]
                 [secretary "1.2.3"]
                 [garden "1.3.3"]
                 [ns-tracker "0.3.0"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]
            [lein-garden "0.2.8" :exclusions [org.clojure/clojure org.apache.commons/commons-compress]]
            [lein-less "1.7.5"]
            [lein-shell "0.4.1"]
            [lein-cooper "1.2.2" :exclusions [org.clojure/clojure]]
            [lein-figwheel "0.5.14" :exclusions [org.clojure/clojure]]
            [lein-pprint "1.1.2"]
            ]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs" "script"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "resources/public/css"]

  ;Server-side figwheel configs
  :figwheel {
             :css-dirs          ["resources/public/css"]
             :ring-handler      redoo.handler/dev-handler
             :server-port       7111
             :repl              false
             :server-logfile    ".figwheel/redoo.log"
             :open-file-command "open-in-intellij"
             }

  ;:garden {:builds [{:id           "screen"
  ;                   :source-paths ["src/clj"]
  ;                   :stylesheet   redoo.css/screen
  ;                   :compiler     {:output-to     "resources/public/css/screen.css"
  ;                                  :pretty-print? true}}]}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/clj" "src/cljs" "script"]
     ; Client side figwheel configs
     :figwheel     {
                    :on-jsload "redoo.core/mount-root"
                    }
     :compiler     {:main                 redoo.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :optimizations        :none
                    :source-map-timestamp true
                    :preloads             [devtools.preload dirac.runtime.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }

     }
    {:id           "min"
     :source-paths ["src/clj" "src/cljs" "script"]
     :jar          true
     :compiler     {:main            redoo.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]}

  :profiles {:dev
             {
              :dependencies [
                             [binaryage/devtools "0.9.4"]
                             [binaryage/dirac "RELEASE"]
                             ;[binaryage/dirac "RELEASE" :exclusions [binaryage/env-config]]
                             [figwheel "0.5.14"]
                             [com.cemerick/piggieback "0.2.1"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [figwheel-sidecar "0.5.14" :exclusions [clj-time jota-time]]
                             [clj-logging-config "1.9.12"]
                             ]
              :plugins      [
                             [lein-figwheel "0.5.14"]
                             ]
              :repl-options {
                             :port             8230
                             :nrepl-middleware [dirac.nrepl/middleware]
                             ;:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 ;(require 'dirac.logging)
                                                 ;(dirac.logging/setup! {:log-out   :console
                                                 ;                       :log-level "TRACE"})
                                                 (use 'figwheel-sidecar.repl-api)
                                                 (start-figwheel!
                                                   '{:figwheel-options {:server-port       7111 ;; <-- figwheel server config goes here
                                                                        :css-dirs          ["resources/public/css"]
                                                                        :repl              true
                                                                        :ring-handler      redoo.handler/dev-handler
                                                                        :server-logfile    ".figwheel/redoo.log"
                                                                        :open-file-command "open-in-intellij"
                                                                        }
                                                     :build-ids        ["dev"] ;; <-- a vector of build ids to start autobuilding
                                                     :all-builds       [{:id           "dev"
                                                                         :figwheel     {:on-jsload "redoo.core/mount-root"}
                                                                         :source-paths ["src/clj" "src/cljs"]
                                                                         :compiler     {:main                 redoo.core
                                                                                        :output-to            "resources/public/compiled/app.js"
                                                                                        :output-dir           "resources/public/compiled/out"
                                                                                        :asset-path           "js/compiled/out"
                                                                                        :preloads             [devtools.preload dirac.runtime.preload]
                                                                                        :external-config      {:devtools/config {:features-to-install :all}}
                                                                                        :optimizations        :none
                                                                                        :source-map           true
                                                                                        :source-map-timestamp true}}
                                                                        {:id           "min"
                                                                         :source-paths ["src/clj" "src/cljs" "script"]
                                                                         :jar          true
                                                                         :compiler     {:main            redoo.core
                                                                                        :output-to       "resources/public/js/compiled/app.js"
                                                                                        :optimizations   :advanced
                                                                                        :closure-defines {goog.DEBUG false}
                                                                                        :pretty-print    false}
                                                                         }
                                                                        ]})
                                                 (println "Preparing to boot dirac.agent")
                                                 (dirac.agent/boot!)
                                                 ;(cljs-repl)
                                                 )
                             }}
             :foo
             {:repl-options {:port             8230
                             :nrepl-middleware [dirac.nrepl/middleware]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 (dirac.agent/boot!))}}}

  :main redoo.server

  :aot [redoo.server]

  :uberjar-name "redoo.jar"

  ;:prep-tasks [["cljsbuild" "once" "min"] ["garden" "once"] ["less" "once"] "compile"]
  )
