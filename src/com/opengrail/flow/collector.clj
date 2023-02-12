(ns com.opengrail.flow.collector
  (:require
    [clojure.string :as string]
    [com.opengrail.flow.common :as common])
  (:import (java.io File)
           (java.nio.file Path)))

(defn handle-event [{:keys [path]}]
  (let [file-name (-> (.toFile ^Path path) (.getAbsolutePath))
        batch-id (common/file-name->batch-id file-name)
        folder (common/file-name->folder-name file-name)
        listing (-> (File. ^String folder) (.list))
        results (filter #(string/ends-with? % common/RESULT_SUFFIX) listing)
        results-data (doall (map #(slurp (str folder "/" %)) results))]
    (prn ::handle-event (sort results-data))
    (common/save-item! {:folder folder
                        :type   common/OUTPUT
                        :item   {:key batch-id :value results-data}})))