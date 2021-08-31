(ns jogo 
  (:require [interop :refer :all]
            [monogame :as g])
  (:gen-class))

(assembly-load-from "MonoGame.dll")
(import [Microsoft.Xna.Framework  Color])

(defn initialize [game graphics]
  (println 1)
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn load-content [game graphics state]
  { :texture/logo (g/load-texture-2d game "logo") })

(defn tick [{:keys [game game-time state]}]
  (println "Update..." (->> game-time .TotalGameTime .Milliseconds))
  state)

(defn draw [{:keys [game sprite-batch delta-time state graphics-device]}]
  (.Clear graphics-device Color/LightGray))

(Console/WriteLine "Ola Delboni")
(g/run load-content initialize tick draw)


