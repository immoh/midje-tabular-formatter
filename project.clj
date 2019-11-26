(defproject midje-tabular-formatter "0.1.0-SNAPSHOT"
  :description "Tool for formatting Midje tabular tests"
  :url "http://github.com/immoh/midje-tabular-formatter"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[rewrite-clj "0.6.1"]]
  :repl-options {:init-ns midje-tabular-formatter.core}
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [org.clojure/clojure "1.10.1"]]
                   :plugins      [[lein-midje "3.2.1"]]}})
