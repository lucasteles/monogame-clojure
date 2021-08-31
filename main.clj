(ns jogo (:gen-class))

(assembly-load-from "MonoGame.dll")

(import [System Console Convert Object])
(import [System.IO Directory Path])
(import [System.Linq Enumerable])
(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color])
(import [Microsoft.Xna.Framework.Graphics SpriteBatch Texture2D])

(assembly-load-from "test.dll")
(import [test Class1])

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

(defn generic-method [obj method-name generic-type & args]
  (let [prop (-> obj .GetType (.GetMethod method-name) (.MakeGenericMethod generic-type))
        args-array (when args (Enumerable/ToArray (type-args Object) args))] 
    (.Invoke prop obj args-array)))

(defn graphics-device [game]
  (get-prop game "GraphicsDevice"))

(defn load-texture-2d [game texture-name]
  (let [content (get-prop game "Content")]
    #_ (.Load content (type-args Texture2D) texture-name)
    (generic-method content "Load" |Texture2D| texture-name)))


(def x (new Class1))
(generic-method x "Foo" |String|)


(defn run-game []
  (let [props (atom {})
        game-instance (proxy
                        ;; first vector contains superclass and interfaces that the created class should extend/implement
                        ;; second vector contains arguments to superclass constructor
                        [Game] []

                        ;; below are all overriden/implemented methods
                        (Initialize []
                          (let [{graphics :graphics} @props]
                            (set! (.PreferredBackBufferWidth graphics) (int32 1024))
                            (set! (.PreferredBackBufferHeight graphics) (int32 768))
                            (.ApplyChanges graphics)
                            (proxy-super Initialize)))

                        (LoadContent []
                          (swap! props assoc :sprite-batch (new SpriteBatch (graphics-device this) 0))
                          (swap! props assoc :texture/logo (load-texture-2d this "logo"))
                          (proxy-super LoadContent))

                        (Update [gameTime]
                          (println "Update..." (->> gameTime .TotalGameTime .Milliseconds))
                          (proxy-super Update gameTime))

                        (Draw [gameTime]
                          (.Clear (graphics-device this) Color/LightGray)
                          (proxy-super Draw gameTime))
                        )]

    (swap! props assoc :graphics (new GraphicsDeviceManager game-instance))
    (set! (.IsMouseVisible game-instance) true)
    (set! (->> game-instance .Content .RootDirectory ) 
          (Path/Combine (Directory/GetCurrentDirectory) "Content/bin/DesktopGL"))
    (.Run game-instance)))

(Console/WriteLine "Ola Delboni")
;(run-game)


