(ns redgenes.routes
  (:require [compojure.core :refer [GET POST defroutes context ANY]]
            [compojure.route :refer [resources]]

            [redgenes.api.modelcount :refer [modelcount modelcount-children cache cacheall]]
            [ring.util.response :refer [response resource-response]]))



(defroutes routes
  (GET "/" []
       (println "Slime and snails")
       (resource-response "index.html" {:root "public"}))
  (resources "/")

  (GET "/version" [] (response {:version "0.1.0"}))

  (context "/api/model/count" [paths]
    (GET "/cache" [mine] (cache mine)
      (response {:loading (str "We're caching counts for " mine "! Well done.")}))
    (GET "/cacheall" [] (cacheall)
      (response {:loading "We're caching counts for all mines! Please wait."}))
    (GET "/children" [path mine]
         (response (modelcount-children path mine)))
    (POST "/" [paths mine]
      (response (modelcount paths mine)))
    (GET "/" [paths mine]
      (response (modelcount paths mine))
    ))
  )
