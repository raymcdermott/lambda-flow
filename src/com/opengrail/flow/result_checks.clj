(ns com.opengrail.flow.result-checks
  (:require
    [clojure.string :as string]
    [clojure.edn :as edn]
    [com.opengrail.flow.common :as common])
  (:import (java.io File)
           (java.nio.file Path)))

(defn handle-event [event]
  (let [file (.toFile ^Path (:path event))
        file-name (.getAbsolutePath file)
        batch-id (common/file-name->batch-id file-name)
        folder (common/file-name->folder-name file-name)
        directory (File. ^String folder)
        listing (.list ^File directory)
        results (filter #(string/ends-with? % common/RESULT_SUFFIX) listing)
        batch-data-file (str folder "/" batch-id common/BATCH_SUFFIX)
        batch-data (edn/read-string (slurp batch-data-file))]
    (when (= (count results) batch-data)
      (prn ::handle-event :results-are-in)
      (common/save-item! {:folder folder
                          :type   common/COLLECTOR
                          :item   {:key batch-id :value :READY}}))))