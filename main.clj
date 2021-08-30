(ns jogo (:gen-class))

(assembly-load-from "MonoGame.dll")

(import [System Console Object Convert])
(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color])
(import [Microsoft.Xna.Framework.Graphics SpriteBatch])

(defn int32 [n] (Convert/ToInt32 n))


; workaround!
; for some reason this is not working
; (.GetProperty (.GetType this) "GraphicsDevice" )
; (.GraphicsDevice this)
; both throws:
;   Syntax error (AmbiguousMatchException) compiling at (/Users/lucas.teles/dev/pessoal/clj-clr/main.clj:53:1).
;   Ambiguous match found.
; I will open a question about this in the clojure clr group
(defn get-prop [obj prop-name]
  (let [prop (->> obj .GetType .GetProperties
                  (filter #(= (.Name %) prop-name)) first)]
    (.GetValue prop obj)))

(defn graphics-device [game] 
  (get-prop game "GraphicsDevice"))

(defn run-game []
  (let [deps (atom {})
        game-instance (proxy
                        ;; first vector contains superclass and interfaces that the created class should extend/implement
                        ;; second vector contains arguments to superclass constructor
                        [Game] []

                        ;; below are all overriden/implemented methods
                        (Initialize []
                          (let [{graphics :graphics} @deps]
                            (set! (.PreferredBackBufferWidth graphics) (int32 1024))
                            (set! (.PreferredBackBufferHeight graphics) (int32 768))
                            (.ApplyChanges graphics)
                            (proxy-super Initialize)))

                        (LoadContent []
                          (swap! deps assoc :sprite-batch (new SpriteBatch (graphics-device this) 0))
                          (proxy-super LoadContent))

                        (Update [gameTime]
                          (println "Update..." (->> gameTime .TotalGameTime .Seconds))
                          (proxy-super Update gameTime))

                        (Draw [gameTime]
                          (.Clear (graphics-device this) Color/LightGray)
                          (proxy-super Draw gameTime)) 
                        )]

    (swap! deps assoc :graphics (new GraphicsDeviceManager game-instance))
    (set! (.IsMouseVisible game-instance) true)
    (set! (->> game-instance .Content .RootDirectory ) "Content")
    (.Run game-instance)))

(Console/WriteLine "Ola Delboni")
(run-game)


