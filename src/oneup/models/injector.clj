(ns oneup.models.injector
  (:use [oneup.models.domain]
        [oneup.models.io]
        [oneup.models.read]))

(read-events (partial send world accept))
(publisher denormalize)
(storer store)
