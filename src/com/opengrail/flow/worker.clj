(ns com.opengrail.flow.worker
  (:require [com.opengrail.flow.common :as common])
  (:import (java.nio.file Path)))

(defn handle [event]
  (let [file (.toFile ^Path (:path event))
        data (slurp file)
        folder (common/file-name->folder-name (.getAbsolutePath file))
        item-name (common/file-name->item-name (.getAbsolutePath file))]
    (prn ::handle data)
    (common/save-item! {:folder folder
                        :type   common/RESULT
                        :item {:key item-name :value data}})))


#_(defn save-item! [{:keys [folder item type]}]
    (let [{:keys [key value]} item
          work-item-name (str key (type->file-suffix type))]
      (spit (str folder "/" work-item-name) value)))

(defn handle-event [event]
  (handle event))


