(set-env!
  :resource-paths #{"src"}
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                  [integrant "0.1.5"]
                  [org.clojure/clojurescript "1.9.293" :scope "test"]
                  [adzerk/boot-test        "1.1.2"     :scope "test"]
                  [adzerk/boot-cljs        "1.7.228-1" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]])

(require '[adzerk.boot-test :as t]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
 pom {:project 'irresponsible/codependence
      :version "0.1.0"
      :description "Lightweight, flexible, configuration-driven dependencies-resolved app structure"}
 target {:dir #{"target"}})

(deftask testing []
  (set-env! :source-paths  #(conj % "test")
            :resource-paths #(conj % "test"))
  identity)

(deftask test []
  (testing)
  (t/test)
  (test-cljs))

(deftask autotest []
  (comp (testing) (watch) (test)))

;; RlsMgr Only stuff
(deftask make-jar []
  (comp (pom) (jar) (target)))

(deftask install-jar []
  (comp (pom) (jar) (install)))

(deftask release []
  (comp (pom) (jar) (push)))

;; Travis Only stuff
(deftask travis []
  (testing)
  (comp (t/test) (make-jar)))

(deftask travis-installdeps []
  (testing) identity)

(deftask jitpak-deploy []
  (task-options! pom {
    :project (symbol (System/getenv "ARTIFACT"))
  })
  (comp
    (pom)
    (jar)
    (target)      ; Must install to build dir
    (install)     ; And to .m2 https://jitpack.io/docs/BUILDING/#build-customization
  )
)
