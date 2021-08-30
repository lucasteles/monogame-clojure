(ns jogo (:gen-class))

(assembly-load-from "MonoGame.dll")

(import [System Console])
; (import [Microsoft.Xna.Framework Input])

(def game (new Microsoft.Xna.Framework.Game))
(.Run game)

(Console/WriteLine "Ola Delboni")
