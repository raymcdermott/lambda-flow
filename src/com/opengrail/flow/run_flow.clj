(ns com.opengrail.flow.run-flow
  (:require
    [com.opengrail.flow.collector :as collector]
    [com.opengrail.flow.common :as common]
    [com.opengrail.flow.event-manager :as event-manager]
    [com.opengrail.flow.initiator :as initiator]
    [com.opengrail.flow.result-checks :as result-checks]
    [com.opengrail.flow.worker :as worker]))

(def event->handler
  {:worker    worker/handle-event
   :result    result-checks/handle-event
   :collector collector/handle-event})

(defn run-flow! []
  (let [{:keys [folder batch-id items] :as work} (initiator/initiate)]
    (-> work
        (merge {:item {:key batch-id :value (count items)}
                :type common/BATCH})
        (common/save-item!))
    (event-manager/watch event->handler folder)
    (common/save-items! work)))

(defn -main [& args]
  (run-flow!))


