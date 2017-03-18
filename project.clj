(defproject redoo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89"]
                 [figwheel-sidecar "0.5.6"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.1"]
                 [re-frisk "0.3.2"]
                 [org.clojure/core.async "0.2.391"]
                 [re-com "0.8.3"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [ns-tracker "0.3.0"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-garden "0.2.8"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "script"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "resources/public/css"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler redoo.handler/dev-handler
             :open-file-command "open-in-intellij"
             }

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   redoo.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]
                   [com.cemerick/piggieback "0.2.1"]
                   [org.clojure/tools.nrepl "0.2.10"]
                   [figwheel-sidecar "0.5.6"]]
    :plugins      [
                   ;[lein-figwheel "0.5.7"]
                   ]
    :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                   }}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "script"]
     :figwheel     {:on-jsload "redoo.core/mount-root"}
     :compiler     {:main                 redoo.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs" "script"]
     :jar true
     :compiler     {:main            redoo.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}

  :main redoo.server

  :aot [redoo.server]

  :uberjar-name "redoo.jar"

  :prep-tasks [["cljsbuild" "once" "min"]["garden" "once"]["less" "once"] "compile"]
  )
