(ns lispmon.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defprotocol Usable
  (use [this user battle])
  (use-text [this user battle]))

(defn activepmon [user battle]
  (first (:team (get battle user))))

(defn other-user [user]
  (if (= user :player) :rival :player))

(defmulti  execute-move #(:type %1))
(defrecord Move [name description power type]
  Usable
  (use [this user battle]
    (println battle)
    (if (not=  0 (:hp (activepmon user battle)))
      (execute-move this user battle)
      battle))
  (use-text [this user battle]
    (str (:name user) " used " (:name this) ".")))

(defmethod execute-move :attack [this user battle]
  (let [thismon  (activepmon user battle)
        othermon (activepmon (other-user user) battle)
        atk (:attack thismon)
        hp  (:hp othermon)
        pwr (:power this)
        new-hp (- hp (* pwr atk))
        new-othermon (assoc othermon :hp new-hp)
        old-other-user (get battle (other-user user))
        [_ & other-rest-team] (:team old-other-user)
        new-team (cons new-othermon other-rest-team)]
    (assoc-in battle [(other-user user) :team] new-team)))
(defmethod execute-move :buff   [this user battle] battle)
(defmethod execute-move :debuff [this user battle] battle)

(def moves
  {:tackle (map->Move
`            {:name        "Tackle"
             :description "Tackles the opponent."
             :power       1
             :type        :attack})

   :scratch (map->Move
             {:name        "Scratch"
              :description "Scratches the opponent."
              :power       1
              :type        :attack})

   :agility (map->Move
             {:name        "Agility"
              :description "Boosts speed by two stages."
              :power       2
              :type        :buff
              :target      :speed})

   :tail-whip (map->Move
               {:name        "Tail Whip"
                :description "Reduces opponent defense by one stage."
                :power       1
                :type        :debuff
                :target      :defense})

   :thunder (map->Move
             {:name        "Thunder"
              :description "Calls down lightning on the opponent."
              :power       2
              :type        :attack})

   :thunderbolt (map->Move
                 {:name        "Thunderbolt"
                  :description "Jolts the opponent with lightning."
                  :power       1.5
                  :type        :attack})

   :bubble (map->Move
            {:name        "Bubble"
             :description "Blows bubbles at the opponent."
             :power       1.2
             :type        :attack})

   :hydro-pump (map->Move
                {:name        "Hydro Pump"
                 :description "Shoots a powerful blast of water."
                 :power       2
                 :type        :attack})

   :bite (map->Move
          {:name        "Bite"
           :description "Chomps down on the opponent."
           :power       1.2
           :type        :attack})

   :gust (map->Move
          {:name        "Gust"
           :description "Blows strong winds onto the opponent."
           :power       1.2
           :type        :attack})

   :confusion (map->Move
               {:name        "Confusion"
                :description "Hurts the opponent with powerful mental blows."
                :power       1.3
                :type        :attack})

   :string-shot (map->Move
                 {:name        "String Shot"
                  :description "Shoots a web to slow down the opponent."
                  :power       1
                  :type        :debuff
                  :target      :speed})

   :growl (map->Move
           {:name        "Growl"
            :description "Intimidates the opponent, reducing its attack."
            :power       1
            :type        :debuff
            :target      :attack})

   :ember (map->Move
           {:name        "Ember"
            :description "Blows embers into the opponent."
            :power       1.2
            :type        :attack})

   :flamethrower (map->Move
                  {:name        "Flamethrower"
                   :description "Shoots powerful jets of fire onto the opponent."
                   :power       1.8
                   :type        :attack})

   :horn-drill (map->Move
                {:name        "Horn Drill"
                 :description "Drills the opponent."
                 :power       1.2
                 :type        :attack})

   :poison-sting (map->Move
                  {:name        "Poison Sting"
                   :description "Stings the opponent."
                   :power       1.2
                   :type        :attack})})

(defrecord Pokemon
    [name sprite
     hp attack speed
     move-1 move-2
     move-3 move-4]
  Usable
  (use [this user battle] battle)
  (use-text [this user battle] nil))

(def pokemon
  {:pikachu    (map->Pokemon
                {:name    "Pikachu"
                 :sprite  "pikachu.png"
                 :hp      25
                 :attack  8
                 :speed   15
                 :move-1  (:thunder moves)
                 :move-2  (:thunderbolt moves)
                 :move-3  (:agility moves)
                 :move-4  (:tackle moves)})

   :squirtle   (map->Pokemon
                {:name    "Squirtle"
                 :sprite  "squirtle.png"
                 :hp      36
                 :attack  10
                 :speed   6
                 :move-1  (:bubble moves)
                 :move-2  (:hydro-pump moves)
                 :move-3  (:bite moves)
                 :move-4  (:tail-whip moves)})

   :butterfree (map->Pokemon
                {:name    "Butterfree"
                 :sprite  "butterfree.png"
                 :hp      18
                 :attack  10
                 :speed   12
                 :move-1  (:gust moves)
                 :move-2  (:confusion moves)
                 :move-3  (:tackle moves)
                 :move-4  (:string-shot moves)})

   :eevee       (map->Pokemon
                {:name    "Eevee"
                 :sprite  "eevee.png"
                 :hp      25
                 :attack  12
                 :speed   10
                 :move-1  (:tackle moves)
                 :move-2  (:tail-whip moves)
                 :move-3  (:bite moves)
                 :move-4  (:growl moves)})

   :charmander (map->Pokemon
                {:name    "charmander"
                 :sprite  "charmander.png"
                 :hp      22
                 :attack  15
                 :speed   12
                 :move-1  (:ember moves)
                 :move-2  (:flamethrower moves)
                 :move-3  (:bite moves)
                 :move-4  (:scratch moves)})

   :beedrill   (map->Pokemon
                {:name    "Beedrill"
                 :sprite  "beedrill.png"
                 :hp      16
                 :attack  14
                 :speed   18
                 :move-1  (:horn-drill moves)
                 :move-2  (:poison-sting moves)
                 :move-3  (:tackle moves)
                 :move-4  (:agility moves)})})

(defrecord Battle [player rival])

(defrecord Trainer [name sprite team])

(def game
  (Battle.
   (Trainer.
    "RCer"
    "rcer.png"
    [(:pikachu    pokemon)
     (:squirtle   pokemon)
     (:butterfree pokemon)])
   (Trainer.
    "Jerk Rival"
    "rival.png"
    [(:eevee      pokemon)
     (:charmander pokemon)
     (:beedrill   pokemon)])))

(def text-bottom-style
  {:text-align "center"
   :font-size "30px"
   :font-weight "bold"
   :position "absolute"
   :top "75%"
   :width "100%"
   :height "25%"
   :padding "0"
   :margin "0"
   :background-color "black"})

(def inner-box
  {:top "5%"
   :left "1%"
   :height "90%"
   :width "98%"
   :position "relative"
   :background-color "white"})

(def top-style
  {:font-family "Helvetica Neue"})

(defonce app-state (atom game))

(defn active-playermon [state] (-> state :player :team first))
(defn active-rivalmon  [state] (-> state :rival :team first))

(defn menu-box [& e]
  [:div
   {:style text-bottom-style}
   (conj [:div {:style inner-box}] e)])

(defmulti  render-ui :stage)

(defmethod render-ui nil [state]
  (menu-box
   [:div (str (-> state :rival :name) " wants to battle!")]
   [:button
    {:on-click
     (fn []
       (swap! app-state #(assoc % :stage :select-action)))}
    "Start Fight"]))

(defmethod render-ui :select-action [state]
  (menu-box
   [:button
    {:on-click
     (fn []
       (swap! app-state #(assoc % :stage :select-move)))}
    "Select Move"]
   [:button
    {:on-click
     (fn []
       (swap! app-state #(assoc % :stage :select-switch)))}
    "Switch Pokemon"]))

(defn build-turn [state move]
  (let [rmon (active-rivalmon state)
        pmon (active-playermon state)]
    (if (> (:speed pmon) (:speed rmon))
      [[:player move] [:rival (:move-1 rmon)]]
      [[:rival (:move-1 rmon)] [:player move]])))

(defn take-turn [move swap?]
  (fn [state]
    (if swap?
      state
      (let [turns (build-turn state move)]
        (reduce (fn [battle [user move]]
                  (use move user battle))
                state
                turns)))))

(defmethod render-ui :select-move [state]
  (let [mon (active-playermon state)]
    (menu-box
     [:button
      {:on-click
       (fn []
         (swap! app-state (take-turn (:move-1 mon) false)))}
      (:name (:move-1 mon))]
     [:button
      {:on-click
       (fn []
         (swap! app-state (take-turn (:move-2 mon) false)))}
      (:name (:move-2 mon))]
     [:button
      {:on-click
       (fn []
         (swap! app-state (take-turn (:move-3 mon) false)))}
      (:name (:move-3 mon))]
     [:button
      {:on-click
       (fn []
         (swap! app-state (take-turn (:move-4 mon) false)))}
      (:name (:move-4 mon))]
     [:button
      {:on-click
       (fn []
         (swap! app-state #(assoc % :stage :select-action)))}
      "Back"])))

(defmethod render-ui :select-switch [state]
  [:div])

(defmethod render-ui :display-turn [state]
  [:div])

(defn render-pokemon [state]
  [:div])

(defn lispmon-battle []
  (let [state @app-state]
    [:div
     {:style top-style}
     (render-ui state)
     (render-pokemon state)]))

(reagent/render-component [lispmon-battle]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  (reset! app-state game))
