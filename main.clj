(ns jogo (:gen-class))

(assembly-load-from "MonoGame.dll")

(import [System Console])
(import [Microsoft.Xna.Framework Game])

(def game (new Game))
(.Run game)

(Console/WriteLine "Ola Delboni")
