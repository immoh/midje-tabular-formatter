(ns midje-tabular-formatter.main
  (:require
    [clojure.java.io :as io]
    [midje-tabular-formatter.core :as formatter])
  (:import
    (java.io File)))

(defn clojure-file? [^File file]
  (and (.isFile file)
       (re-matches #".+\.cljc?" (.getName file))))

(defn find-clojure-files []
  (->> (file-seq (io/file "."))
       (filter clojure-file?)
       (sort-by (memfn getAbsolutePath))))

(defn format-file! [^File file]
  (let [original-source (slurp file)
        modified-source (formatter/format-tables original-source)]
    (when-not (= original-source modified-source)
      (spit file modified-source)
      true)))

(defn -main [& _]
  (let [files (find-clojure-files)
        modified-files (doall (filter format-file! files))]
    (if (zero? (count modified-files))
      (println (format "Checked %s files, all fine." (count files)))
      (do
        (println (format "Checked %s files, formatted %s files:" (count files) (count modified-files)))
        (doseq [file modified-files]
          (println (.getPath file)))
        (System/exit 1)))))
