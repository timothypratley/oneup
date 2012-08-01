(ns oneup.models.injector
  (:use [oneup.models.domain]
        [oneup.models.io]
        [oneup.models.read]))

(read-events (partial send world accept))
(read-events denormalize)
(publisher denormalize)
(storer store)
