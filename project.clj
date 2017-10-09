(defproject redoo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 ;;[org.clojure/tools.nrepl "0.2.13"]
                 ;;[binaryage/devtools "0.9.4"]
                 [binaryage/dirac "1.2.16"]
                 [figwheel "0.5.14"]
                 ;[figwheel-sidecar "0.5.13"]
                 [reagent "0.8.0-alpha1"]
                 [re-frame "0.9.2"]
                 [re-frisk "0.4.5"]
                 [org.clojure/core.async "0.3.443"]
                 [re-com "0.9.0"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [ns-tracker "0.3.0"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-garden "0.2.8"]
            [lein-less "1.7.5"]
            [lein-shell "0.4.1"]
            [lein-cooper "1.2.2"]
            [lein-figwheel "0.5.14"]
            ]

  :min-lein-version "2.5.3"

  :source-paths ["src/cljs" "src/clj" "script"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "resources/public/css"]

  ; Server-side figwheel configs
  :figwheel {
             :css-dirs          ["resources/public/css"]
             :ring-handler      redoo.handler/dev-handler
             :server-port       7111
             :server-logfile    ".figwheel/redoo.log"
             :open-file-command "open-in-intellij"
             }


  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   redoo.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles {:dev
             {
              :dependencies [
                             [binaryage/devtools "0.9.4"]
                             [binaryage/dirac "1.2.16"]
                             [figwheel "0.5.14"]
                             ;[com.cemerick/piggieback "0.2.1"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [figwheel-sidecar "0.5.14"]
                             [clj-logging-config "1.9.12"]
                             ]
              :plugins      [
                             ;[lein-figwheel "0.5.14"]
                             ]
              :repl-options {
                             :port             8230
                             :nrepl-middleware [dirac.nrepl/middleware]
                             ;:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 (require 'dirac.logging)
                                                 (dirac.logging/setup! {:log-out   :console
                                                                        :log-level "TRACE"})
                                                 (use 'figwheel-sidecar.repl-api)
                                                 (start-figwheel!
                                                   {:figwheel-options {:server-port       7111 ;; <-- figwheel server config goes here
                                                                       :css-dirs          ["resources/public/css"]
                                                                       :ring-handler      redoo.handler/dev-handler
                                                                       :server-logfile    ".figwheel/redoo.log"
                                                                       :open-file-command "open-in-intellij"
                                                                       }
                                                    :build-ids        ["dev"] ;; <-- a vector of build ids to start autobuilding
                                                    :all-builds ;; <-- supply your build configs here
                                                                      [({:id           "dev"
                                                                         :figwheel     {:on-jsload "redoo.core/mount-root"}
                                                                         :source-paths ["src/clj" "src/cljs"]
                                                                         :compiler     {:main                 redoo-core
                                                                                        :output-to            "resources/public/compiled/app.js"
                                                                                        :output-dir           "resources/public/compiled/out"
                                                                                        :asset-path           "js/compiled/out"
                                                                                        :preloads             ['devtools.preload 'dirac.runtime.preload]
                                                                                        :external-config      {:devtools/config {:features-to-install :all}}
                                                                                        :optimizations        :none
                                                                                        :source-map           true
                                                                                        :source-map-timestamp true}}
                                                                         {:id           "min"
                                                                          :source-paths ["src/cljs" "script"]
                                                                          :jar          true
                                                                          :compiler     {:main            redoo.core
                                                                                         :output-to       "resources/public/js/compiled/app.js"
                                                                                         :optimizations   :advanced
                                                                                         :closure-defines {goog.DEBUG false}
                                                                                         :pretty-print    false}
                                                                          }
                                                                         )]})
                                                 (dirac.agent/boot!)
                                                 (cljs-repl))
                             }}}


  ;:cljsbuild
  ;{:builds
  ; [{:id           "dev"
  ;   :source-paths ["src/cljs" "script"]
  ;   ;; Client side figwheel configs
  ;   :figwheel     {
  ;                  :figwheel  true
  ;                  :on-jsload "redoo.core/mount-root"
  ;                  }
  ;   :compiler     {:main                 redoo.core
  ;                  :output-to            "resources/public/js/compiled/app.js"
  ;                  :output-dir           "resources/public/js/compiled/out"
  ;                  :asset-path           "js/compiled/out"
  ;                  :source-map-timestamp true
  ;                  :preloads             [devtools.preload]
  ;                  :external-config      {:devtools/config {:features-to-install :all}}
  ;                  }
  ;
  ;   }
  ;  {:id           "min"
  ;   :source-paths ["src/cljs" "script"]
  ;   :jar          true
  ;   :compiler     {:main            redoo.core
  ;                  :output-to       "resources/public/js/compiled/app.js"
  ;                  :optimizations   :advanced
  ;                  :closure-defines {goog.DEBUG false}
  ;                  :pretty-print    false}}]}

  :main redoo.server

  :aot [redoo.server]

  :uberjar-name "redoo.jar"

  ;:prep-tasks [["cljsbuild" "once" "min"] ["garden" "once"] ["less" "once"] "compile"]
  )
