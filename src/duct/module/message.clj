(ns duct.module.message
  (:require [duct.core :as core]
            [duct.core.merge :as merge]
            [integrant.core :as ig])
  (:import  [com.google.pubsub.v1 SubscriptionName]
            [com.google.cloud.pubsub.v1 Subscriber]))



(defn- get-environment [config options]
  (:enviroment options (:duct.core/environment config :production)))

(def ^:private prod-config
  {:duct.message/pubusb
   {:logger (ig/ref :duct/logger)}})

(def ^:private dev-config
  {:duct.message/pubsub
    {:logger (ig/ref :duct/logger)
     :emulator (merge/displace "localhost:8085")}})

(def ^:private env-configs
  {:production prod-config
   :development dev-config})


(defmethod ig/init-key :duct.message/pubsub
  [_ {:keys [p-id s-id] :as opt}]
  (assoc opt :s-name (SubscriptionName/create p-id s-id)))


(defmethod ig/init-key :duct.module/message [_ options]
  {:fn (fn [config]
         (core/merge-configs
                     config
                     {:duct.message/pubsub {:logger (ig/ref :duct/logger)}}))})
