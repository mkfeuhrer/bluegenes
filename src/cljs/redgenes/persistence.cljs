(ns redgenes.persistence
  "
  /*
  * Copyright (C) 2016 Intermine
  *
  * This code may be freely distributed and modified under the
  * terms of the GNU Lesser General Public Licence. This should
  * be distributed with the code. See the LICENSE file for more
  * information or http://www.gnu.org/copyleft/lesser.html.
  *
  */

  This namespace provides functions for saving
  and loading the state of the application to&from
  localstorage

  Also functions for exporting in various formats
  "
  (:require
    [dommy.core :as dommy :refer-macros [sel sel1]]
    [cognitect.transit :as t]))

; (:total-re-mix :re-ratio :nnn-score-maps :active-mixture :mixtributions :max-H :total-score-ratio :re-p :maxHmix :total-re-nnn :max-re :id :total-score-nnn :score-maps :distributions :total-score-mix :maxh :NNN)
(defn filter-state [state]
  ;(println "filter state")
  (select-keys state
    [:msas :trees :selected-sequences :selected-columns :selected-msa :diversities :mixversities :ui]))

(defn merge-state [state other-state except-paths]
  (println "Merge loaded state " (count (:history other-state)))
  (merge state
    (reduce
      (fn [r p]
        (assoc-in r p (get-in state p)))
      other-state
      except-paths)))

(defn to-transit [state]
  (t/write (t/writer :json-verbose) (filter-state state)))

(defn persist! [state]
  (println "persist state")
  (js/localStorage.setItem "redgenes/state" (to-transit state))
  state)

(defn get-state!
  ([state]
    (get-state! state []))
  ([state paths]
   (println "restore state")
    (merge-state state
      (t/read (t/reader :json)
        (js/localStorage.getItem "redgenes/state"))
      paths)))

(defn merge-state-from-file [state file]
  (println "merge state with " file)
  (merge state (t/read (t/reader :json) file)))

(defn download!
  ([tipe naym contents]
    (let [
          a (.createElement js/document "a")
          f (js/Blob. (clj->js [contents]) {:type (name tipe)})
          ]
      (set! (.-href a) (.createObjectURL js/URL f))
      (set! (.-download a) (str naym "." (name tipe)))
      (println "<a>" a)
      (.dispatchEvent a (js/MouseEvent. "click")))))

(defn make-filename [s]
  (str (get-in s [:msas (:selected-msa s) :name]) "-" (:selected-msa s) ".dg"))

(defn load! [s]
(println "load state")
  (-> (sel1 :#file_button) .click)
   s)

(defn save! [s]
(println "save state")
  (download! "JSON"
    (make-filename s)
    (to-transit s)) s)




