(ns metabase.query-processor.middleware.results-metadata-test
  (:require [expectations :refer [expect]]
            [metabase
             [query-processor :as qp]
             [util :as u]]
            [metabase.models.card :refer [Card]]
            [metabase.query-processor.middleware.results-metadata :as results-metadata]
            [metabase.test
             [data :as data]
             [util :as tu]]
            [toucan.db :as db]
            [toucan.util.test :as tt]))

;; test that Card result metadata is saved after running a Card
(expect
  [{:name "ID",          :display_name "ID",          :base_type "type/Integer"}
   {:name "NAME",        :display_name "NAME",        :base_type "type/Text"}
   {:name "PRICE",       :display_name "PRICE",       :base_type "type/Integer"}
   {:name "CATEGORY_ID", :display_name "CATEGORY_ID", :base_type "type/Integer"}
   {:name "LATITUDE",    :display_name "LATITUDE",    :base_type "type/Float"}
   {:name "LONGITUDE",   :display_name "LONGITUDE",   :base_type "type/Float"}]
  (tt/with-temp Card [card]
    (qp/process-query {:database (data/id)
                       :type     :native
                       :native   {:query (format "SELECT ID, NAME, PRICE, CATEGORY_ID, LATITUDE, LONGITUDE FROM VENUES")}
                       :info     {:card-id    (u/get-id card)
                                  :query-hash (byte-array 0)}})
    (db/select-one-field :result_metadata Card :id (u/get-id card))))


;; tests for valid-checksum?
(tu/resolve-private-vars metabase.query-processor.middleware.results-metadata metadata-checksum)

(expect
  (results-metadata/valid-checksum? "ABCDE" (metadata-checksum "ABCDE")))

(expect
  false
  (results-metadata/valid-checksum? "ABCD" (metadata-checksum "ABCDE")))
