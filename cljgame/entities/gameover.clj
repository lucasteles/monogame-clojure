(ns cljgame.entities.gameover 
  (:require [cljgame.physics :as physics]
            [cljgame.entities.bird :as bird]))

(def time-to-restart 1)

(defn init []
  {:timer 0
   :is-game-over false })

(defn reset-game! [current-state world]
  (doseq [pipe-body (->> current-state :pipe-manager :pipes (mapcat (fn [b] [(:body-up b) 
                                                                             (:body-down b)])))]
       (physics/destroy-body! world pipe-body))

       (-> current-state :bird :body (physics/set-position! bird/start-position))
       (->  current-state
           (assoc-in [:pipe-manager :pipes] [])
           (assoc-in [:pipe-manager :last-spawn] 0)
           (assoc-in [:bird :rotation] 0)
           (assoc-in [:score :score] 0)
           (assoc :game-over (init))
           (assoc :paused true)))

(defn update- [{ {timer :timer go :is-game-over} :game-over :as state} world delta-time]
  (cond 
    (and go (>= timer time-to-restart)) 
    (reset-game! state world)

    (and go (< timer time-to-restart)) 
    (assoc-in state [:game-over :timer] (+ timer delta-time))

    :else state))
