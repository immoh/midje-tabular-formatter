(ns midje-tabular-formatter.core
  (:require
    [clojure.string]
    [rewrite-clj.custom-zipper.core :as custom-zipper]
    [rewrite-clj.zip :as zip]
    [rewrite-clj.zip.whitespace :as zip.whitespace]
    ))

(def example "
(tabular
  \"moioimoi\"
  (fact
    \"adad\"
    1 => 1)
  ?foo ?bar ?baz
  1 2 3
  123123234 2343  322342 ;; TODO
232123  (+ 1 1) 23)
")

(defn tabular? [zloc]
  (and (list? (zip/sexpr zloc))
       (= 'tabular (zip/sexpr (zip/down zloc)))))

(defn table-header? [s]
  (clojure.string/starts-with? s "?"))

(defn column-count [entry-strings]
  (count (take-while table-header? entry-strings)))

(defn every-nth [n coll]
  (->> (partition n coll)
       (apply interleave)
       (partition (/ (count coll) n))))

(defn entry-strings [zloc]
  (map zip/->string (take-while identity (iterate zip/right zloc))))

(defn max-lengths [entry-strings column-count]
  (->> entry-strings
       (map count)
       (every-nth column-count)
       (map (partial reduce max))))

(defn- table-indentation [zloc]
  (or (let [left-zloc (custom-zipper/left zloc)]
        (when (= (zip/tag left-zloc) :whitespace)
          (count (zip/->string left-zloc))))
      0))

(defn right-pad-to-length! [zloc length]
  (zip.whitespace/insert-space-right zloc (inc (- length (count (zip/->string zloc))))))


(defn remove-table-whitespace! [zloc]
  (let [right-zloc (custom-zipper/right zloc)]
    (cond
      (not right-zloc) zloc
      (#{:whitespace :newline} (zip/tag right-zloc)) (recur (zip/remove right-zloc))
      :else (recur right-zloc))))

(defn root [zloc]
  (if-let [parent (zip/up zloc)]
    (recur parent)
    zloc))

(defn cell-type [entry-count column-count entry-index]
  (cond
    (zero? entry-index) :table-first
    (= entry-index (dec entry-count)) :table-last
    (zero? (mod entry-index column-count)) :column-first
    (= column-count (inc (mod entry-index column-count))) :column-last
    :else :middle))

(defn cell-whitespace-specs [indentation lengths entry-count column-count entry-index]
  (let [cell-type (cell-type entry-count column-count entry-index)]
    (merge
      (when-not (= :table-last cell-type)
        {:length (nth lengths (mod entry-index column-count))})
      (when (= :column-first cell-type)
        {:indentation indentation})
      (when (= :column-last cell-type)
        {:newline true}))))

(defn format-cell! [zloc {:keys [indentation length newline] :as x}]
  (prn :format-cell! (zip/->string zloc) x)
  (cond->
    zloc
    indentation (zip.whitespace/insert-space-left indentation)
    newline (zip.whitespace/insert-newline-right)
    length (right-pad-to-length! length)))

(defn insert-table-whitespace! [zloc]
  (let [indentation (table-indentation zloc)
        entry-strings (entry-strings zloc)
        column-count (column-count entry-strings)
        lengths (max-lengths entry-strings column-count)]
    (loop [zloc zloc
           i 0]
      (let [modified-zloc (format-cell! zloc (cell-whitespace-specs indentation
                                                                    lengths
                                                                    (count entry-strings)
                                                                    column-count
                                                                    i))]
        (if-let [next-zloc (zip/right modified-zloc)]
          (recur next-zloc (inc i))
          modified-zloc)))))

(defn find-table-start [zloc]
  (zip/find-next (zip/down zloc) (comp table-header? zip/->string)))

(defn format-table! [zloc]
  (-> (find-table-start zloc)
      (remove-table-whitespace!)
      (root)
      (find-table-start)
      (insert-table-whitespace!)))

(defn foo [s]
  (zip/postwalk (zip/of-string s)
                tabular?
                format-table!))


(println (zip/->root-string (foo example)))

