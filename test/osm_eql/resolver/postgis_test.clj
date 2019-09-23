(ns osm-eql.resolver.postgis-test
  (:require [clojure.test :refer :all]
            [osm-eql.resolver.postgis]
            [hugsql.core :as hugsql]
            [osm-eql.parser :refer [build-parser]]
            [clojure.string :refer [join]]))

(hugsql/def-sqlvec-fns "postgis/schema.sql")

(deftest hugsql-test
  (testing "Check that queries are correctly generated by hugsql"
     (is (= (schema-tables-sqlvec {:owner "osm" :filter "^osm_"})
            [(join "\n" ["SELECT tablename"
                         "FROM pg_catalog.pg_tables"
                         "WHERE tableowner = ?"
                         "AND tablename ~ ?"])
             "osm" "^osm_"]))
     (is (= (schema-tables-details-sqlvec {})
            [(join "\n" ["SELECT schemaname,relname,n_live_tup"
                         "FROM pg_stat_user_tables"
                         "ORDER BY n_live_tup DESC"])]))
     (is (= (schema-columns-sqlvec {:table "osm_roads"})
            [(join "\n" ["SELECT *"
                         "FROM information_schema.COLUMNS"
                         "WHERE TABLE_NAME = ?"])
             "osm_roads"]
            ))))

(def parser (build-parser [(osm-eql.resolver.postgis/resolvers)] {}))

(deftest postgis-schema-test
  (testing "Check that the osm database has the expected imposm3 schema"
    (is (= (parser {} [{:postgis/tables [:postgis/table-name :postgis/columns]}])
           {:postgis/tables
            [#:postgis{:table-name "osm_barrierways"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_housenumbers_interpolated"
                       :columns '("id" "osm_id" "name" "type"
                                  "addr:street" "addr:postcode" "addr:city" "addr:inclusion" "geometry")}
             #:postgis{:table-name "osm_amenities"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_barrierpoints"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_buildings"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_waterways"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_admin"
                       :columns '("id" "osm_id" "name" "type" "admin_level" "geometry")}
             #:postgis{:table-name "osm_aeroways"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_places"
                       :columns '("id" "osm_id" "name" "type" "z_order" "population" "geometry")}
             #:postgis{:table-name "osm_transport_points"
                       :columns '("id" "osm_id" "name" "type" "ref" "geometry")}
             #:postgis{:table-name "osm_landusages"
                       :columns '("id" "osm_id" "name" "type" "area" "z_order" "geometry")}
             #:postgis{:table-name "osm_transport_areas"
                       :columns '("id" "osm_id" "name" "type" "geometry")}
             #:postgis{:table-name "osm_waterareas"
                       :columns '("id" "osm_id" "name" "type" "area" "geometry")}
             #:postgis{:table-name "osm_roads"
                       :columns '("id" "osm_id" "type" "name"
                                  "tunnel" "bridge" "oneway" "ref" "z_order" "access" "service" "class" "geometry")}
             #:postgis{:table-name "osm_housenumbers"
                       :columns '("id" "osm_id" "name" "type" "addr:street" "addr:postcode" "addr:city" "geometry")}
             #:postgis{:table-name "osm_waterways_gen1"
                       :columns '("osm_id" "geometry" "name" "type")}
             #:postgis{:table-name "osm_roads_gen1"
                       :columns '("osm_id" "geometry" "type" "name"
                                  "tunnel" "bridge" "oneway" "ref" "z_order" "access" "service" "class")}
             #:postgis{:table-name "osm_landusages_gen0"
                       :columns '("osm_id" "geometry" "name" "type" "area" "z_order")}
             #:postgis{:table-name "osm_landusages_gen1"
                       :columns '("osm_id" "geometry" "name" "type" "area" "z_order")}
             #:postgis{:table-name "osm_waterareas_gen1"
                       :columns '("osm_id" "geometry" "name" "type" "area")}
             #:postgis{:table-name "osm_waterways_gen0"
                       :columns '("osm_id" "geometry" "name" "type")}
             #:postgis{:table-name "osm_waterareas_gen0"
                       :columns '("osm_id" "geometry" "name" "type" "area")}
             #:postgis{:table-name "osm_roads_gen0"
                       :columns '("osm_id" "geometry" "type" "name"
                                  "tunnel" "bridge" "oneway" "ref" "z_order" "access" "service" "class")}]}))))

(deftest resolver-feature-test
  (is (= (parser {} ['(:postgis/tables {:filter "transport"})])
         {:postgis/tables [{:postgis/table-name "osm_transport_points"}
                           {:postgis/table-name "osm_transport_areas"}]})))
