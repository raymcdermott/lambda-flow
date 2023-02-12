(ns com.opengrail.flow.common
  (:require [clojure.set :as set]
            [clojure.string :as string]))

(def BATCH :batch)
(def WORK :work)
(def RESULT :result)
(def COLLECTOR :collector)
(def OUTPUT :output)

(def BATCH_SUFFIX (str "." (name BATCH)))
(def WORK_SUFFIX (str "." (name WORK)))
(def RESULT_SUFFIX (str "." (name RESULT)))
(def COLLECTOR_SUFFIX (str "." (name COLLECTOR)))
(def OUTPUT_SUFFIX (str "." (name OUTPUT)))

(def type->file-suffix
  {BATCH     BATCH_SUFFIX
   WORK      WORK_SUFFIX
   RESULT    RESULT_SUFFIX
   COLLECTOR COLLECTOR_SUFFIX
   OUTPUT    OUTPUT_SUFFIX})

(def file-suffix->type
  (set/map-invert type->file-suffix))

(defn suffixed-name? [file-name]
  (nat-int? (string/index-of file-name ".")))

(defn file-name->folder-name [file-name]
  (->> (string/last-index-of file-name "/")
       (subs file-name 0)))

(defn file-name->batch-id [file-name]
  (let [folder (file-name->folder-name file-name)
        batch (string/last-index-of folder "/")]
    (subs folder (inc batch))))

(defn file-name->item-name [file-name]
  (let [item-start (string/last-index-of file-name "/")
        item-end (string/last-index-of file-name ".")]
    (subs file-name (inc item-start) item-end)))

(defn save-item! [{:keys [folder item type]}]
  (let [{:keys [key value]} item
        work-item-name (str key (type->file-suffix type))]
    (spit (str folder "/" work-item-name) value)))

(defn save-items! [inputs]
  (reduce-kv
    (fn [_ k v]
      (-> (select-keys inputs [:folder :type])
          (merge {:item {:key k :value v}})
          save-item!))
    {} (inputs :items)))

