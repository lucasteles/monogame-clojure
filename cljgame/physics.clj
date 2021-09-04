(ns cljgame.physics
  (:require [cljgame.interop :refer [load-monogame]])
  (:import [System Math]))

(load-monogame)

(import [tainicom.Aether.Physics2D.Dynamics World BodyType Body Fixture]
        [tainicom.Aether.Physics2D.Dynamics.Contacts Contact]
        [Microsoft.Xna.Framework Color Vector2 Rectangle])

(def pixels-per-meter 100)

(defn create-world [& [gravity]]
  (new World (or gravity (new Vector2 0 10))))

(defn step [world delta-time]
  (.Step world delta-time))

(def ^:private body-type-map
  {:static BodyType/Static
   :kinematic BodyType/Kinematic
   :dynamic BodyType/Dynamic })

(defn pixels->meters [n] (/ n pixels-per-meter))
(defn meters->pixels [n] (* n pixels-per-meter))

(defn vect-pixels->meters [n] (Vector2/op_Division n pixels-per-meter))
(defn vect-meters->pixels [n] (Vector2/op_Multiply n pixels-per-meter))

(defn- config-body! [body tag]
    (-> body .Mass (set! 1))
    (-> body .Tag (set! tag))
    (-> body (.SetRestitution 0))
    (-> body (.SetFriction 0)))

(defn create-body
  [^World world
   ^BodyType body-type
   ^Rectangle rectangle
   &[tag rotation]]
  (let [internal-body-type (or (body-type body-type-map) (throw (Exception. "INVALID BODY TYPE")) )
        width (-> rectangle .Width pixels->meters)
        height (-> rectangle .Height pixels->meters)
        x (pixels->meters (.X rectangle))
        y (pixels->meters (.Y rectangle))
        body (.CreateRectangle world width height 
                               1  ; density
                               (Vector2. x y)
                               (or rotation 0) ; rotation
                               internal-body-type)]
    (config-body! body tag)
    body))

(defn create-body-circle
  [^World world
   ^BodyType body-type
   radios
   ^Vector2 position
   &[tag rotation]]
  (let [internal-body-type (or (body-type body-type-map) (throw (Exception. "INVALID BODY TYPE")) )
        position-converted (vect-pixels->meters position)
        body (.CreateCircle world 
                            (pixels->meters radios) 
                            1 ; density
                            position-converted
                            internal-body-type)]
    (config-body! body tag)
    body))


(defn destroy-body! [world body] (.Remove world body))
(defn apply-impulse! [^Body body ^Vector2 force] (.ApplyLinearImpulse body force))
(defn velocity [^Body body] (-> body .LinearVelocity vect-meters->pixels))
(defn position [^Body body] (-> body .Position vect-meters->pixels))
(defn rotation [^Body body] (-> body .Rotation))

(defn set-linear-velocity! [^Body body ^Vector2 velocity] 
  (set! (.LinearVelocity body) (vect-pixels->meters velocity)))

(defn set-rotation! [^Body body rotation] 
  (set! (.Rotation body) rotation))


(defn on-collide! [^Body body f] 
  (let [handler (gen-delegate 
                  |tainicom.Aether.Physics2D.Dynamics.OnCollisionEventHandler| 
                  [sender other contact] (let [res (f sender other contact)] 
                                           (if (nil? res) true res)))
        event (.GetEvent |Body| "OnCollision")]
  (.AddEventHandler event body handler)))

