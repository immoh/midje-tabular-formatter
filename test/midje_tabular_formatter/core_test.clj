(ns midje-tabular-formatter.core-test
  (:require [clojure.test :refer [deftest are]]
            [midje-tabular-formatter.core :as formatter]))

(def unformatted-tabular-fact "
(tabular
  (fact \"The rules of Conway's life\"
        (alive? ?cell-status ?neighbor-count) => ?expected)
  ?cell-status ?neighbor-count ?expected
    :alive 1 FALSEY
  :alive 2 truthy
   :alive 3 truthy
  :alive 4 FALSEY

  :dead 2 FALSEY
  :dead 3 truthy
  :dead 4 FALSEY)
")


(def formatted-tabular-fact "
(tabular
  (fact \"The rules of Conway's life\"
        (alive? ?cell-status ?neighbor-count) => ?expected)
  ?cell-status ?neighbor-count ?expected
  :alive       1               FALSEY
  :alive       2               truthy
  :alive       3               truthy
  :alive       4               FALSEY
  :dead        2               FALSEY
  :dead        3               truthy
  :dead        4               FALSEY)
")

(def unformatted-1x3-table "
(tabular
  (fact
    (int? ?number) => true)
  ?number 1 2)
")

(def formatted-1x3-table "
(tabular
  (fact
    (int? ?number) => true)
  ?number
  1
  2)
")

(def unformatted-1x2-table "
(tabular
  (fact
    (int? ?number) => true)
  ?number 1)
")

(def formatted-1x2-table "
(tabular
  (fact
    (int? ?number) => true)
  ?number
  1)
")

(def unformatted-table-with-list "
(tabular
  (fact
    (= ?expr ?evaluated) => true)
  ?expr ?evaluated
  (+ 1 1) 2
  (+ 2 3)         5)
")

(def formatted-table-with-list "
(tabular
  (fact
    (= ?expr ?evaluated) => true)
  ?expr   ?evaluated
  (+ 1 1) 2
  (+ 2 3) 5)
")

(deftest format-tabular-fact-test
  (are [unformatted-fact formatted-fact]
       (= (formatter/format-tables unformatted-fact) formatted-fact)
       unformatted-tabular-fact    formatted-tabular-fact
       unformatted-1x3-table       formatted-1x3-table
       unformatted-1x2-table       formatted-1x2-table
       unformatted-table-with-list formatted-table-with-list))
