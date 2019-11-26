(ns unformatted.game-of-life-tests
  (:require [midje.sweet :refer :all]))

(tabular
  (fact "The rules of Conway's life"
        (alive? ?cell-status ?neighbor-count) => ?expected)
  ?cell-status ?neighbor-count ?expected
  :alive 1 FALSEY
  :alive 2 truthy
  :alive 3 truthy
  :alive 4 FALSEY

  :dead 2 FALSEY
  :dead 3 truthy
  :dead 4 FALSEY)
