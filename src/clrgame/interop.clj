(ns clrgame.interop
  (:import [System Convert]
           [System.IO Path File]
           [System.Reflection Assembly]))

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

(def current-exe-dir (-> (Assembly/GetEntryAssembly) .Location Path/GetDirectoryName))

(def current "MonoGame.Framework.dll")

(def executable (Path/Combine current-exe-dir  current))

(defn load-monogame []
    (assembly-load-from
        (cond (File/Exists current) current
              (File/Exists executable) executable)))