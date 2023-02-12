(ns com.opengrail.flow.event-manager
  (:require
    [clojure.string :as string]
    [nextjournal.beholder :as beholder]
    [com.opengrail.flow.common :as common])
  (:import (java.nio.file Path)))

(defn worker-event? [{:keys [type path]}]
  (and (= type :create)
       (-> (.toFile ^Path path)
           (.getAbsolutePath)
           (string/ends-with? common/WORK_SUFFIX))))

(defn result-event? [{:keys [type path]}]
  (and (= type :create)
       (-> (.toFile ^Path path)
           (.getAbsolutePath)
           (string/ends-with? common/RESULT_SUFFIX))))

(defn collector-event? [{:keys [type path]}]
  (and (= type :create)
       (-> (.toFile ^Path path)
           (.getAbsolutePath)
           (string/ends-with? common/COLLECTOR_SUFFIX))))

(defn output-event? [{:keys [type path]}]
  (and (= type :create)
       (-> (.toFile ^Path path)
           (.getAbsolutePath)
           (string/ends-with? common/OUTPUT_SUFFIX))))

(defn batch-id [{:keys [path]}]
  (-> (.toFile ^Path path)
      (.getAbsolutePath)
      (common/file-name->batch-id)))

(defn event-dispatch [{:keys [worker collector result]} event]
  (cond
    (worker-event? event) (worker event)
    (result-event? event) (result event)
    (collector-event? event) (collector event)
    (output-event? event) (prn ::BATCH-ALL-DONE (batch-id event))
    :else (prn ::unhandled event)))

(def watcher (atom nil))

(defn watch [actions folder]
  (let [dispatcher (partial event-dispatch actions)]
    (reset! watcher (beholder/watch dispatcher folder))))

(defn stop-watch []
  (beholder/stop @watcher))
