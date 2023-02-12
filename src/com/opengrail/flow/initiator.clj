(ns com.opengrail.flow.initiator
  (:require [com.opengrail.flow.common :as common])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(defn- make-items []
  (let [ids (->> (repeatedly random-uuid)
                 (take 10)
                 (map str))
        works (range 10)]
    (zipmap ids works)))


(defn- make-batch-folder! [^String batch-id]
  (let [abs-file-name (str "/tmp/" batch-id)
        file (-> (File. abs-file-name))]
    (.mkdir file)
    abs-file-name))

(defn initiate []
  (let [batch-id (str (random-uuid))]
    (prn ::BATCH-STARTED batch-id)
    {:batch-id batch-id
     :folder   (make-batch-folder! batch-id)
     :items    (make-items)
     :type     common/WORK}))

