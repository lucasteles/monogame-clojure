(ns interop
  (:import [System.Linq Enumerable]
           [System Console Convert Object]))

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
