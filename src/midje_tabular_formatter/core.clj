(ns midje-tabular-formatter.core
  (:require
    [clojure.string]
    [rewrite-clj.custom-zipper.core :as custom-zipper]
    [rewrite-clj.node :as node]
    [rewrite-clj.zip :as zip]
    [rewrite-clj.zip.whitespace :as zip.whitespace]))

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

(defn zmap [f zloc]
  (let [modified-zloc (f zloc)]
    (if-let [next-zloc (zip/right modified-zloc)]
      (recur f next-zloc)
      modified-zloc)))

(defn zmap-indexed [f zloc]
  (loop [zloc zloc
         i 0]
    (let [modified-zloc (f i zloc)]
      (if-let [next-zloc (zip/right modified-zloc)]
        (recur next-zloc (inc i))
        modified-zloc))))

(defn tabular? [zloc]
  (and (not (node/printable-only? (zip/node zloc)))
       (= :list (zip/tag zloc))
       (= 'tabular (zip/sexpr (zip/down zloc)))))

(defn table-header? [s]
  (clojure.string/starts-with? s "?"))

(defn find-table-start [zloc]
  (zip/find-next (zip/down zloc) (comp table-header? zip/->string)))

(defn column-count [cell-strings]
  (count (take-while table-header? cell-strings)))

(defn every-nth [n coll]
  (->> (partition n coll)
       (apply interleave)
       (partition (/ (count coll) n))))

(defn cell-strings [zloc]
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
      (#{:whitespace :newline} (zip/tag right-zloc)) (recur (-> right-zloc
                                                                (zip/remove)
                                                                ;; remove navigates to last node in depth order
                                                                ;; therefore we need to start from beginning
                                                                (zip/find zip/up tabular?)
                                                                (find-table-start)))
      :else (recur right-zloc))))

(defn cell-features [entry-count column-count entry-index]
  {:table-first  (boolean (zero? entry-index))
   :table-last   (boolean (= entry-index (dec entry-count)))
   :column-first (boolean (zero? (mod entry-index column-count)))
   :column-last  (boolean (= column-count (inc (mod entry-index column-count))))})

(defn cell-whitespace-specs [indentation lengths entry-count column-count entry-index]
  (let [cell-features (cell-features entry-count column-count entry-index)]
    (merge
      (when-not (:column-last cell-features)
        {:length (nth lengths (mod entry-index column-count))})
      (when (and (:column-first cell-features) (not (:table-first cell-features)))
        {:indentation indentation})
      (when (and (:column-last cell-features) (not (:table-last cell-features)))
        {:newline true}))))

(defn format-cell [zloc {:keys [indentation length newline] :as x}]
  (cond->
    zloc
    indentation (zip.whitespace/insert-space-left indentation)
    newline (zip.whitespace/insert-newline-right)
    length (right-pad-to-length! length)))

(defn insert-table-whitespace [zloc]
  (let [indentation (table-indentation zloc)
        cell-strings (cell-strings zloc)
        column-count (column-count cell-strings)
        lengths (max-lengths cell-strings column-count)]
    (zmap-indexed (fn [i zloc]
                    (format-cell zloc (cell-whitespace-specs indentation
                                                             lengths
                                                             (count cell-strings)
                                                             column-count
                                                             i)))
                  zloc)))

(defn format-table [zloc]
  (-> (find-table-start zloc)
      (remove-table-whitespace!)
      (zip/find zip/up tabular?)
      (find-table-start)
      (insert-table-whitespace)))

(defn format-tables [s]
  (->> (zip/of-string s)
       (zmap (fn [zloc] (zip/postwalk zloc tabular? format-table)))
       (zip/->root-string)))
