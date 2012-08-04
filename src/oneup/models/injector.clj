(ns oneup.models.injector
  (:use [oneup.models.domain]
        [oneup.models.io]
        [oneup.models.read]))

;configure the event store and read denormalizer
(publisher denormalize)
(storer store)

;load the domain and read model from past events
(read-events hydrate)
