(ns redial.live
  (:use overtone.live)
  (:require [overtone.inst.synth :as synth]))

(def kick-sample (freesound 2086))

(definst kick
  [vol 1]
  (let [env (env-gen (perc 0.01 1) :action FREE)
        snd (play-buf 1 kick-sample)]
    (-> (* env snd)
        (rlpf 600 0.5)
        (* vol))))

(def hat1-sample (freesound 404889))

(def hat2-sample (freesound 404890))

(def hat-open-sample (freesound 42548))

(definst hat
  [vol 0.7]
  (let [env (env-gen (perc 0.01 1) :action FREE)
        snd (play-buf 1 hat1-sample)]
    (rlpf (decay (* vol env snd) 0)
          5000
          0.9)))

(definst bass
  [freq 440
   attack 0.01
   sustain 0.4
   release 0.1
   vol 0.6]
  (let [env (env-gen (lin attack sustain release) 1 1 0 1 FREE)
        sines (+ (saw freq)
                 (* 0.7 (saw (* 2 freq)))
                 (* 0.25 (saw (* 3 freq))))
        filtered (rlpf sines (* (+ 150 freq) (abs (sin-osc 0.25))) 0.5)
        signal (* filtered env vol)]
    (pan2 signal)))

(defn looper [nome]
  (let [beat (nome)]
    (at (nome beat)
        (kick)
        (bass (midi->hz (note :C2)) :vol 0.7 :sustain 2))
    (at (nome (+ 0.5 beat)) (hat))
    (at (nome (+ 1 beat)) (kick))
    (at (nome (+ 1.5 beat)) (hat))
    (at (nome (+ 2 beat)) (kick))
    (at (nome (+ 2.5 beat)) (hat))
    (at (nome (+ 3 beat)) (kick))
    (at (nome (+ 3.5 beat)) (hat))
    (apply-by (nome (+ 4 beat)) looper nome [])))

(comment

  (def x (synth/cs80lead 440))

  (def nome (metronome 120))

  (looper nome)

  (stop)

  )
