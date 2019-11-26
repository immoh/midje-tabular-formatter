(ns midje-tabular-formatter.main
  (:require
    [clojure.java.io :as io]
    [midje-tabular-formatter.core :as formatter])
  (:import
    (java.io File)))

(defn clojure-file? [^File file]
  (and (.isFile file)
       (re-matches #".+\.cljc?" (.getName file))))

(defn find-clojure-files [path]
  (->> (file-seq (io/file path))
       (filter clojure-file?)
       (sort-by (memfn getAbsolutePath))))

(defn format-file! [^File file]
  (let [original-source (slurp file)
        modified-source (formatter/format-tables original-source)]
    (when-not (= original-source modified-source)
      (spit file modified-source)
      true)))

(defn format-files! [path]
  (let [files (find-clojure-files path)
        modified-files (doall (filter format-file! files))]
    (if (zero? (count modified-files))
      {:success? true
       :summary  (format "Checked %s files, all fine." (count files))}
      {:success? false
       :summary  (format "Checked %s files, formatted %s files:\n%s"
                         (count files)
                         (count modified-files)
                         (clojure.string/join "\n" (map (memfn getPath) modified-files)))})))

(defn -main
  "Finds Clojure files recusively from current location and formats Midje tabular fact tables
   in them. Prints summary of checked and formatted files.

   Returns with exit code 0 if all files were correctly formatted, otherwise returns with exit code 1."
  [& _]
  (let [{:keys [success? summary]} (format-files! ".")]
    (println summary)
    (when-not success?
      (System/exit 1))))
