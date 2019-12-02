(ns midje-tabular-formatter.main-tests
  (:require [midje.sweet :refer :all]
            [midje-tabular-formatter.main :as main]))

(fact
  "Main function reports unformatted files"
  (with-redefs [spit (fn [& _])]
    (main/format-files! "test_files")
    => {:success? false
        :summary  (str "Checked 2 files, formatted 1 files:" \newline
                       "test_files/unformatted/game_of_life_tests.clj")}))

(fact
  "Main function reports success if everything is properly formatted"
  (main/format-files! "test_files/formatted")
  => {:success? true
      :summary  "Checked 1 files, all fine."})
