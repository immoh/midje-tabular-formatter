(ns midje-tabular-formatter.core-test
  (:require
    [midje.sweet :refer :all]
    [midje-tabular-formatter.core :as formatter]))

(def unformatted-table "
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


(def formatted-table "
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

(tabular
  (fact
    "Unformatted tables are formatted"
    (formatter/format-tables ?unformatted) => ?formatted)
  ?unformatted                ?formatted
  unformatted-table           formatted-table
  unformatted-1x3-table       formatted-1x3-table
  unformatted-1x2-table       formatted-1x2-table
  unformatted-table-with-list formatted-table-with-list)
